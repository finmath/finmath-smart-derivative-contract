package net.finmath.smartcontract.marketdata.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.util.IRMarketDataParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


import java.util.*;
import java.util.stream.Collectors;

public class MarketDataWebSocketAdapter extends WebSocketAdapter   {// implements Callable<String> {

    private final JsonNode authJson;
    private final String position;
    private final Set<CalibrationDataItem.Spec> calibrationSpecs;
    private Set<CalibrationDataItem> calibrationDataSet;

    final private PublishSubject<String> publishSubject;

    final private Sinks.Many<String> sink;

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

    public Observable<String> asObservable(){
        return this.publishSubject;
    }

    public Flux<String> asFlux() { return sink.asFlux(); }



    public void onTextMessage(WebSocket websocket, String message) throws Exception {
        JsonNode responseJson = null;
        //long quotesRetrieved = this.getMarketDataItems().stream().filter(item -> item.getQuote() != null).count();
        if (!message.isEmpty()) {

            ObjectMapper mapper = new ObjectMapper();
            responseJson = mapper.readTree(message);

            if (!requestSent) {
                sendRICRequest(websocket);
                requestSent = true;
            }

//            System.out.println(quotesRetrieved + " " + responseJson.get(0).get("Type").textValue() );
            try {
                for (int i = 0; i < responseJson.size(); i++) {
                    if (responseJson.get(i).has("Fields")) {
                        String ric = responseJson.get(i).get("Key").get("Name").asText();
                        JsonNode fields = responseJson.get(i).get("Fields");
                        Double BID = fields.has("BID") ? fields.get("BID").doubleValue() : null;
                        Double ASK = fields.has("ASK") ? fields.get("ASK").doubleValue() : null;
                        Double quote = ASK == null ? BID : BID == null ? ASK : (BID + ASK) / 2.0 / 100.;
                        String date = fields.get("VALUE_DT1").textValue();
                        String timestamp = fields.get("VALUE_TS1").textValue();
                        this.calibrationDataSet.add (new CalibrationDataItem(this.getSpec(ric),quote,date,timestamp));
                    }

                }
            } catch (Exception e) {

            }
        }


        //if (quotesRetrieved == this.getMarketDataItems().size()) {
        if ( this.allQuotesRetrieved()) {
            System.out.println("Quotes retrieved");
            String json = IRMarketDataParser.serializeToJson(this.getMarketDataItems());
            this.calibrationDataSet.clear();
            this.publishSubject.onNext(json);
            this.sink.tryEmitNext(json);

            //Thread.sleep(2000);
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