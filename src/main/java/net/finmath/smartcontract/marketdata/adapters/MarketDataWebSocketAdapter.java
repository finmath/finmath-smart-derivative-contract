package net.finmath.smartcontract.marketdata.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataSet;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.springframework.cglib.core.Local;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MarketDataWebSocketAdapter extends WebSocketAdapter   {// implements Callable<String> {

    private final JsonNode authJson;
    private final String position;
    private final Set<CalibrationDataItem.Spec> calibrationSpecs;
    private Set<CalibrationDataItem> calibrationDataSet;

    final private PublishSubject<CalibrationDataSet > publishSubject;

    final private Sinks.Many<CalibrationDataSet> sink;

    boolean requestSent;

    public MarketDataWebSocketAdapter(JsonNode authJson, String position, List<CalibrationDataItem.Spec> itemList) {
        this.authJson = authJson;
        this.position = position;
        this.calibrationSpecs = itemList.stream().collect(Collectors.toSet());
        this.calibrationDataSet = new HashSet<>();
        requestSent = false;
        publishSubject  = PublishSubject.create();
        sink = Sinks.many().multicast().onBackpressureBuffer();   // https://prateek-ashtikar512.medium.com/projectreactor-sinks-bac6c88e5e69

    }

    private CalibrationDataItem.Spec getSpec(String key){
        return this.calibrationSpecs.stream().filter(spec->spec.getKey().equals(key)).findAny().orElse(null);
    }


    public boolean    allQuotesRetrieved(){
        return this.calibrationDataSet.size() >= this.calibrationSpecs.size();
    }

    public void   reset(){
        this.calibrationDataSet= new HashSet<>();
    }

    public Set<CalibrationDataItem>     getMarketDataItems(){
        return calibrationDataSet.stream().collect(Collectors.toSet());
    }

    /**
     * Called when handshake is complete and websocket is open, send login
     */
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        System.out.println("WebSocket successfully connected!");
        sendLoginRequest(websocket, authJson.get("access_token").asText(), true);
    }

    public Observable<CalibrationDataSet > asObservable(){
        return this.publishSubject;
    }

    public Flux<CalibrationDataSet> asFlux() { return sink.asFlux(); }



    public void onTextMessage(WebSocket websocket, String message) throws Exception {
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
                        String ric = responseJson.get(i).get("Key").get("Name").asText();
                        JsonNode fields = responseJson.get(i).get("Fields");
                        Double BID = fields.has("BID") ? fields.get("BID").doubleValue() : null;
                        Double ASK = fields.has("ASK") ? fields.get("ASK").doubleValue() : null;
                        Double quote = ASK == null ? BID : BID == null ? ASK : (BID + ASK) / 2.0 / 100.;
                        String date = fields.get("VALUE_DT1").textValue();
                        String time = fields.get("VALUE_TS1").textValue();

                        LocalDateTime localDateTime = LocalDateTime.parse(date + "T" +time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        ZoneId zoneId = TimeZone.getDefault().toZoneId();
                        ZoneId gmt = TimeZone.getTimeZone("GMT").toZoneId();
                        localDateTime = localDateTime.atZone(gmt).withZoneSameInstant(zoneId).toLocalDateTime();

                        if (this.getSpec(ric).getProductName().equals("Fixing")){
                            if (ric.equals("ESTR")){ // The euro short-term rate (€STR) is published on each TARGET2 business day based on transactions conducted and settled on the previous TARGET2 business day.
                                localDateTime = localDateTime.plusDays(1);
                            }
                            quote = quote / 100.;

                        }

                        this.calibrationDataSet.add (new CalibrationDataItem(this.getSpec(ric),quote,localDateTime));
                    }

                }
            } catch (Exception e) {

            }
        }


        if ( this.allQuotesRetrieved()) {
            Set<CalibrationDataItem> clone = new HashSet<>();
            clone.addAll(this.calibrationDataSet);
            CalibrationDataSet set = new CalibrationDataSet(clone,LocalDateTime.now());
            this.calibrationDataSet.clear();
            this.publishSubject.onNext(set);
            this.sink.tryEmitNext(set);
            requestSent = false;
        }


    }



    /**
     * Create and send simple Market Price request
     * @param websocket Websocket to send the message on
     * @throws Exception
     */
    public void sendRICRequest(WebSocket websocket) throws Exception {
        String requestJsonString;
        String keyString1 = ricsToString(); //;+ ",\"Service\":\""; //  + "\"}}"; //
        requestJsonString = "{\"ID\":2,"+keyString1+",\"View\":[\"MID\",\"BID\",\"ASK\",\"VALUE_DT1\",\"VALUE_TS1\"]}";
        websocket.sendText(requestJsonString);
    }


    public void sendLoginRequest(WebSocket websocket, String authToken, boolean isFirstLogin) throws Exception {
        String loginJsonString = "{\"ID\":1,\"Domain\":\"Login\",\"Key\":{\"Elements\":{\"ApplicationId\":\"\",\"Position\":\"\",\"AuthenticationToken\":\"\"},\"NameType\":\"AuthnToken\"}}";
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode loginJson = (ObjectNode) mapper.readTree(loginJsonString);
         ((ObjectNode) loginJson.get("Key").get("Elements")).put("AuthenticationToken",authToken);
        ((ObjectNode) loginJson.get("Key").get("Elements")).put("ApplicationId","256");
        ((ObjectNode) loginJson.get("Key").get("Elements")).put("Position",this.position);

        if (!isFirstLogin) { // If this isn't our first login, we don't need another refresh for it.
            loginJson.put("Refresh", false);//.get("Key").get("Elements")).put("Position",this.position);
        }

        websocket.sendText(loginJson.toString());

    }


    private String      ricsToString(){

        String ricsAsString = "\"Key\":{\"Name\":[";

        for (CalibrationDataItem.Spec item : this.calibrationSpecs)
            ricsAsString +=  "\"" + item.getKey() + "\",";
        ricsAsString = ricsAsString.substring(0,ricsAsString.length()-1);
        ricsAsString += "]}";

        return ricsAsString;


    }



}