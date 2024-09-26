package net.finmath.smartcontract.valuation.marketdata.generators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("java:S125")
@Profile("prod")
@Service
public class MarketDataGeneratorWebsocket extends WebSocketAdapter implements MarketDataGeneratorInterface<MarketDataList> {// implements Callable<String> {

	private final BusinessdayCalendarExcludingTARGETHolidays bdCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
	private final JSONObject authJson;

	private static final Logger logger = LoggerFactory.getLogger(MarketDataGeneratorWebsocket.class);
	private final String position;
	private final Set<CalibrationDataItem.Spec> calibrationSpecs;
	private MarketDataList marketDataList;

	private final PublishSubject<MarketDataList> publishSubject;

	//final private Sinks.Many<MarketDataList> sink;

	boolean requestSent;

	public MarketDataGeneratorWebsocket(JSONObject authJson, String position, List<CalibrationDataItem.Spec> itemList) {
		this.authJson = authJson;
		this.position = position;
		this.calibrationSpecs = new LinkedHashSet<>(itemList);
		this.marketDataList = new MarketDataList();
		requestSent = false;
		publishSubject = PublishSubject.create();
		//sink = Sinks.many().multicast().onBackpressureBuffer();   // https://prateek-ashtikar512.medium.com/projectreactor-sinks-bac6c88e5e69
	}

	private CalibrationDataItem.Spec getSpec(String key) {
		return this.calibrationSpecs.stream().filter(spec -> spec.getKey().equals(key)).findAny().orElse(null);
	}


	public boolean allQuotesRetrieved() {
		return this.marketDataList.getSize() >= this.calibrationSpecs.size();
	}


	/**
	 * Called when handshake is complete and websocket is open, send login
	 */
	public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
		System.out.println("WebSocket successfully connected!");
		sendLoginRequest(websocket, authJson.getString("access_token"), true);
	}

	@Override
	public Observable<MarketDataList> asObservable() {
		return this.publishSubject;
	}

	//public Flux<MarketDataList> asFlux() {return sink.asFlux();}

	/**
	 * Sends a close login stream message. Closing the login stream also closes and resets all data streams.
	 *
	 * @param webSocket the socket on which the message must be sent
	 */
	public void closeStreamsAndLogoff(WebSocket webSocket) {
		String request = "{\"ID\":1, \"Type\": \"Close\", \"Domain\":\"Login\"}";
		webSocket.sendText(request);
		logger.info("WebSocket logged off");
	}


	@Deprecated(forRemoval = true)
	public void writeDataset(String importDir, MarketDataList s, boolean isOvernightFixing) throws IOException {
		throw new RuntimeException("Not implemented");
		/*String json = s.serializeToJson();
		String timeStamp = s.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		logger.info("Consumer MarketDataStorage: Stored Market Data at: " + timeStamp);
		Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
		Files.write(path, json.getBytes());*/
	}

	@Override
	public void onTextMessage(WebSocket websocket, String message) throws Exception {

		logger.debug("message: {}",  message);

		JsonNode responseJson = null;
		if (!message.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();
			responseJson = mapper.readTree(message);

			if (!requestSent) {
				sendRICRequest(websocket);
				requestSent = true;
			}

			try {
				for (int i = 0; i < responseJson.size(); i++) {
					if (responseJson.get(i).has("Fields")) {
						if (this.marketDataList.getSize()== 0)
							this.marketDataList.setRequestTimeStamp(LocalDateTime.now());
						String ric = responseJson.get(i).get("Key").get("Name").asText();
						JsonNode fields = responseJson.get(i).get("Fields");
						Double BID = fields.has("BID") ? fields.get("BID").doubleValue() : null;
						Double ASK = fields.has("ASK") ? fields.get("ASK").doubleValue() : null;
						double midQuote = ASK == null ? BID : BID == null ? ASK : (BID + ASK) / 2.0;
						Double quoteScaled = BigDecimal.valueOf(midQuote).setScale(3, RoundingMode.HALF_EVEN).divide(BigDecimal.valueOf(100.)).doubleValue();
						String date = fields.get("VALUE_DT1").textValue();
						String time = fields.get("VALUE_TS1").textValue();
						/* Adjust Time on GMT */
						LocalDateTime localDateTime = LocalDateTime.parse(date + "T" + time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
						LocalDateTime adjustedTime = this.adjustTimestampForOvernightFixing(localDateTime,ric);

						CalibrationDataItem.Spec spec = this.getSpec(ric);
						MarketDataPoint dataPoint = new MarketDataPoint(spec.getKey(),quoteScaled,adjustedTime);

						this.marketDataList.add(dataPoint);
					}

				}
			} catch (Exception e) {
				logger.error("Fetching Quote Error:", e);
			}

		}

		if (this.allQuotesRetrieved()) {
			logger.info("all quotes retrieved");
			logger.debug(marketDataList.toString());
			this.publishSubject.onNext(marketDataList);
			//this.sink.tryEmitNext(marketDataList);
			this.reset();
			requestSent = false;
		}
	}

	private void reset() {
		this.marketDataList = new MarketDataList();
	}

	private	LocalDateTime	adjustTimestampForOvernightFixing(LocalDateTime localDateTimeUnadjusted, String symbol){
		ZoneId zoneId = TimeZone.getDefault().toZoneId();
		ZoneId gmt = TimeZone.getTimeZone("GMT").toZoneId();
		LocalDateTime adjustedTime = localDateTimeUnadjusted.atZone(gmt).withZoneSameInstant(zoneId).toLocalDateTime();

		if (this.getSpec(symbol).getProductName().equals("Fixing")) {
			if (symbol.equals("EUROSTR=")) { // The euro short-term rate (€STR) is published on each TARGET2 business day based on transactions conducted and settled on the previous TARGET2 business day.
				adjustedTime = bdCalendar.getRolledDate(adjustedTime.toLocalDate(), 1).atTime(adjustedTime.toLocalTime());
			}
		}

		return adjustedTime;
	}



	/**
	 * Create and send simple Market Price request
	 *
	 * @param websocket Websocket to send the message on
	 */
	public void sendRICRequest(WebSocket websocket) {
		String requestJsonString;
		String keyString1 = ricsToString(); //;+ ",\"Service\":\""; //  + "\"}}"; //
		requestJsonString = "{\"ID\":2," + keyString1 + ",\"View\":[\"MID\",\"BID\",\"ASK\",\"VALUE_DT1\",\"VALUE_TS1\"]}";
		websocket.sendText(requestJsonString);
	}


	public void sendLoginRequest(WebSocket websocket, String authToken, boolean isFirstLogin) throws Exception {
		String loginJsonString = "{\"ID\":1,\"Domain\":\"Login\",\"Key\":{\"Elements\":{\"ApplicationId\":\"\",\"Position\":\"\",\"AuthenticationToken\":\"\"},\"NameType\":\"AuthnToken\"}}";
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode loginJson = (ObjectNode) mapper.readTree(loginJsonString);
		((ObjectNode) loginJson.get("Key").get("Elements")).put("AuthenticationToken", authToken);
		((ObjectNode) loginJson.get("Key").get("Elements")).put("ApplicationId", "256");
		((ObjectNode) loginJson.get("Key").get("Elements")).put("Position", this.position);

		if (!isFirstLogin) { // If this isn't our first login, we don't need another refresh for it.
			loginJson.put("Refresh", false);//.get("Key").get("Elements")).put("Position",this.position);
		}

		websocket.sendText(loginJson.toString());

	}


	private String ricsToString() {

		StringBuilder ricsAsString = new StringBuilder("\"Key\":{\"Name\":[");

		for (CalibrationDataItem.Spec item : this.calibrationSpecs)
			ricsAsString.append("\"").append(item.getKey()).append("\",");
		ricsAsString = new StringBuilder(ricsAsString.substring(0, ricsAsString.length() - 1));
		ricsAsString.append("]}");

		return ricsAsString.toString();


	}
}