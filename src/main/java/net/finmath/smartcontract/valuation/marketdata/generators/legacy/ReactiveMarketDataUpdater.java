package net.finmath.smartcontract.valuation.marketdata.generators.legacy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.model.*;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class that provides a reactive event emitter that listens to the Refinitiv live market feed and outputs marked data transfer messages.
 *
 * @author Luca Bressan
 */
public class ReactiveMarketDataUpdater extends LiveFeedAdapter<MarketDataSet> {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveMarketDataUpdater.class);
	private final JSONObject authJson;
	private final String position;
	private final Set<CalibrationDataItem.Spec> calibrationSpecs;
	private final PublishSubject<MarketDataSet> publishSubject;
	private final Sinks.Many<MarketDataSet> sink;
	private final ObjectMapper mapper;
	boolean requestSent;
	private MarketDataSet marketDataSet;

	public ReactiveMarketDataUpdater(JSONObject authJson, String position, List<CalibrationDataItem.Spec> itemList) {
		this.mapper = new ObjectMapper(); // Spring's default object mapper has some settings which are incompatible with this data pipeline, create a new one
		this.mapper.registerModule(new JavaTimeModule());
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.INDENT_OUTPUT, true)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		this.marketDataSet = new MarketDataSet();
		this.calibrationSpecs = new LinkedHashSet<>(itemList);
		this.authJson = authJson;
		this.position = position;
		requestSent = false;
		publishSubject = PublishSubject.create();
		sink = Sinks.many().multicast().onBackpressureBuffer();   // https://prateek-ashtikar512.medium.com/projectreactor-sinks-bac6c88e5e69
	}


	private boolean allQuotesRetrieved() {
		return this.marketDataSet.getValues().size() >= this.calibrationSpecs.size();
	}

	private void reset() {
		this.marketDataSet = new MarketDataSet();
	}

	/**
	 * Called when handshake is complete and websocket is open, send login
	 */
	@Override
	public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
		this.sendLoginRequest(websocket, authJson.getString("access_token"), true);
		logger.info("WebSocket successfully connected! Resetting connection.");
		this.closeStreamsAndLogoff(websocket);
		logger.info("Connection reset. Reopening connection...");
		this.sendLoginRequest(websocket, authJson.getString("access_token"), true);
		logger.info("...done");

	}


	public Observable<MarketDataSet> asObservable() {
		return this.publishSubject;
	}

	@Override
	public void onTextMessage(WebSocket websocket, String message) {


		if (!message.isEmpty()) {
			List<RefinitivMarketData> marketDataValues;
			try {
				marketDataValues = mapper.readerForListOf(RefinitivMarketData.class).readValue(message);
				marketDataSet.requestTimestamp(OffsetDateTime.now(ZoneId.of("GMT")).withNano(0));
				for (RefinitivMarketData mdi : marketDataValues) {

					RefinitivMarketDataKey refinitivMarketDataKey = Objects.requireNonNull(mdi.getKey());
					String symbol = refinitivMarketDataKey.getName();
					RefinitivMarketDataFields refinitivMarketDataFields = Objects.requireNonNull(mdi.getFields());
					OffsetDateTime dataTimestamp = OffsetDateTime.parse(refinitivMarketDataFields.getVALUEDT1() + "T" + refinitivMarketDataFields.getVALUETS1() + "Z");

					OptionalDouble optValue = Stream.of(refinitivMarketDataFields.getASK(), refinitivMarketDataFields.getBID()).filter(Objects::nonNull)
							.mapToDouble(x -> x)
							.average();
					if (optValue.isEmpty())
						throw new IllegalStateException("Failed to get average");

					boolean hasSymbol = false;
					for (MarketDataSetValuesInner i : marketDataSet.getValues())
						hasSymbol |= i.getSymbol().equals(refinitivMarketDataKey.getName());

					if (!hasSymbol) {
						marketDataSet.addValuesItem(new MarketDataSetValuesInner().value(optValue.getAsDouble()).dataTimestamp(dataTimestamp).symbol(symbol));
					}
				}


			} catch (JsonProcessingException | NullPointerException | IllegalStateException e) {
				logger.info("JSON mapper is failing silently in order to skip message:{}{}{}as it is not a quote/fixing update.", System.lineSeparator(), message, System.lineSeparator());
			}
			if (!requestSent) {
				sendRICRequest(websocket);
				requestSent = true;
			}

		}


		if (this.allQuotesRetrieved()) {
			this.publishSubject.onNext(this.marketDataSet);
			this.sink.tryEmitNext(this.marketDataSet);
			this.reset();

			requestSent = false;
		}


	}


	/**
	 * Create and send simple Market Price request
	 *
	 * @param websocket Websocket to send the message on
	 */
	private void sendRICRequest(WebSocket websocket) {
		String requestJsonString;
		String keyString1 = ricsToString(); //;+ ",\"Service\":\""; //  + "\"}}"; //
		requestJsonString = "{\"ID\":2," + keyString1 + ",\"View\":[\"MID\",\"BID\",\"ASK\",\"VALUE_DT1\",\"VALUE_TS1\"]}";
		websocket.sendText(requestJsonString);
		//System.out.println("Request sent.");
	}

	/**
	 * Sends a close login stream message. Closing the login stream also closes and resets all data streams.
	 *
	 * @param webSocket the socket on which the message must be sent
	 */
	public void closeStreamsAndLogoff(WebSocket webSocket) {
		String request = "{\"ID\":1, \"Type\": \"Close\", \"Domain\":\"Login\"}";
		webSocket.sendText(request);
	}

	private MarketDataSetValuesInner overnightFixingPostProcessing(MarketDataSetValuesInner datapoint, boolean isOvernightFixing) {
		if (datapoint.getSymbol().equals("EUROSTR=") && isOvernightFixing) {
			BusinessdayCalendarExcludingTARGETHolidays bdCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
			LocalDateTime rolledDate = bdCalendar.getRolledDate(datapoint.getDataTimestamp().toLocalDate(), 1).atTime(datapoint.getDataTimestamp().toLocalTime());
			System.out.println(rolledDate);
			OffsetDateTime dataTimestamp = OffsetDateTime.parse(rolledDate + "Z");
			return new MarketDataSetValuesInner().symbol("EUROSTR=").value(datapoint.getValue()).dataTimestamp(dataTimestamp);
		}
		return datapoint;
	}

	/**
	 * Writes the formatted output from the Refinitiv stream to an import candidates file.
	 *
	 * @param importFile        location of the import candidates file
	 * @param transferMessage   the transfer message to be written
	 * @param isOvernightFixing true when the correction for overnight rates time must be applied
	 * @throws IOException if the writing operation fails
	 */
	public void writeDataset(String importFile, MarketDataSet transferMessage, boolean isOvernightFixing) throws IOException {
		transferMessage.values(transferMessage.getValues().stream().map(x -> this.overnightFixingPostProcessing(x, isOvernightFixing)).toList());
		File targetFile = new File(importFile);
		mapper.writerFor(MarketDataSet.class).writeValue(targetFile, transferMessage);
	}


	private void sendLoginRequest(WebSocket websocket, String authToken, boolean isFirstLogin) throws Exception {
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