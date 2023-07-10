package net.finmath.smartcontract.marketdata.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.model.MarketDataMessage;
import net.finmath.smartcontract.model.MarketDataMessageFields;
import net.finmath.smartcontract.model.MarketDataMessageKey;
import net.finmath.smartcontract.model.MarketDataTransferMessage;
import net.finmath.smartcontract.model.MarketDataTransferMessageValuesInner;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Class that provides a reactive event emitter that listens to the Refinitiv live market feed and outputs marked data transfer messages.
 *
 * @author Luca Bressan
 */
public class ReactiveMarketDataUpdater extends LiveFeedAdapter<MarketDataTransferMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveMarketDataUpdater.class);
    private final JsonNode authJson;
    private final String position;
    private final Set<CalibrationDataItem.Spec> calibrationSpecs;
    private final PublishSubject<MarketDataTransferMessage> publishSubject;
    private final Sinks.Many<MarketDataTransferMessage> sink;
    private final ObjectMapper mapper;
    boolean requestSent;
    private MarketDataTransferMessage marketDataTransferMessage;

    public ReactiveMarketDataUpdater(JsonNode authJson, String position, List<CalibrationDataItem.Spec> itemList) {
        this.mapper = new ObjectMapper(); // Spring's default object mapper has some settings which are incompatible with this data pipeline, create a new one
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.marketDataTransferMessage = new MarketDataTransferMessage();
        this.calibrationSpecs = new LinkedHashSet<>(itemList);
        this.authJson = authJson;
        this.position = position;
        requestSent = false;
        publishSubject = PublishSubject.create();
        sink = Sinks.many().multicast().onBackpressureBuffer();   // https://prateek-ashtikar512.medium.com/projectreactor-sinks-bac6c88e5e69
    }


    private boolean allQuotesRetrieved() {
        return this.marketDataTransferMessage.getValues().size() >= this.calibrationSpecs.size();
    }

    private void reset() {
        this.marketDataTransferMessage = new MarketDataTransferMessage();
    }

    /**
     * Called when handshake is complete and websocket is open, send login
     */
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        this.sendLoginRequest(websocket, authJson.get("access_token").asText(), true);
        logger.info("WebSocket successfully connected! Resetting connection.");
        this.closeStreamsAndLogoff(websocket);
        logger.info("Connection reset. Reopening connection...");
        this.sendLoginRequest(websocket, authJson.get("access_token").asText(), true);
        logger.info("...done");

    }


    public Observable<MarketDataTransferMessage> asObservable() {
        return this.publishSubject;
    }


    public void onTextMessage(WebSocket websocket, String message) {


        if (!message.isEmpty()) {
            List<MarketDataMessage> marketDataValues;
            try {
                marketDataValues = mapper.readerForListOf(MarketDataMessage.class).readValue(message);
                marketDataTransferMessage.requestTimestamp(OffsetDateTime.now(ZoneId.of("GMT")).withNano(0));
                for (MarketDataMessage mdi : marketDataValues) {

                    MarketDataMessageKey marketDataMessageKey = Objects.requireNonNull(mdi.getKey());
                    String symbol = marketDataMessageKey.getName();
                    MarketDataMessageFields marketDataMessageFields = Objects.requireNonNull(mdi.getFields());
                    OffsetDateTime dataTimestamp = OffsetDateTime.parse(marketDataMessageFields.getVALUEDT1() + "T" + marketDataMessageFields.getVALUETS1() + "Z");

                    OptionalDouble optValue = Stream.of(marketDataMessageFields.getASK(), marketDataMessageFields.getBID()).filter(Objects::nonNull)
                            .mapToDouble(x -> x)
                            .average();
                    if (optValue.isEmpty())
                        throw new IllegalStateException("Failed to get average");

                    boolean hasSymbol = false;
                    for (var i : marketDataTransferMessage.getValues())
                        hasSymbol |= i.getSymbol().equals(marketDataMessageKey.getName());

                    if (!hasSymbol) {
                        marketDataTransferMessage.addValuesItem(new MarketDataTransferMessageValuesInner().value(optValue.getAsDouble()).dataTimestamp(dataTimestamp).symbol(symbol));
                    }
                }


            } catch (JsonProcessingException | NullPointerException | IllegalStateException e) {
                logger.info("JSON mapper is failing silently in order to skip message:" + System.lineSeparator() + message  + System.lineSeparator() + "as it is not a quote/fixing update.");
            }
            if (!requestSent) {
                sendRICRequest(websocket);
                requestSent = true;
            }

        }


        if (this.allQuotesRetrieved()) {
            this.publishSubject.onNext(this.marketDataTransferMessage);
            this.sink.tryEmitNext(this.marketDataTransferMessage);
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
     * @param webSocket the socket on which the message must be sent
     */
    public void closeStreamsAndLogoff(WebSocket webSocket) {
        String request = "{\"ID\":1, \"Type\": \"Close\", \"Domain\":\"Login\"}";
        webSocket.sendText(request);
    }

    private MarketDataTransferMessageValuesInner overnightFixingPostProcessing(MarketDataTransferMessageValuesInner datapoint, boolean isOvernightFixing) {
        if (datapoint.getSymbol().equals("EUROSTR=") && isOvernightFixing) {
            BusinessdayCalendarExcludingTARGETHolidays bdCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
            LocalDateTime rolledDate = bdCalendar.getRolledDate(datapoint.getDataTimestamp().toLocalDate(), 1).atTime(datapoint.getDataTimestamp().toLocalTime());
            System.out.println(rolledDate);
            OffsetDateTime dataTimestamp = OffsetDateTime.parse(rolledDate + "Z");
            return new MarketDataTransferMessageValuesInner().symbol("EUROSTR=").value(datapoint.getValue()).dataTimestamp(dataTimestamp);
        }
        return datapoint;
    }

    /**
     * Writes the formatted output from the Refinitiv stream to an import candidates file.
     * @param importDir location of the import candidates file
     * @param transferMessage the transfer message to be written
     * @param isOvernightFixing true when the correction for overnight rates time must be applied
     * @throws IOException if the writing operation fails
     */
    public void writeDataset(String importDir, MarketDataTransferMessage transferMessage, boolean isOvernightFixing) throws IOException {
        transferMessage.values(transferMessage.getValues().stream().map(x -> this.overnightFixingPostProcessing(x, isOvernightFixing)).toList());
        File baseFolder = new File(Objects.requireNonNull(importDir));
        File targetFile = new File(baseFolder, "import_candidate.json");
        mapper.writerFor(MarketDataTransferMessage.class).writeValue(targetFile, transferMessage);
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