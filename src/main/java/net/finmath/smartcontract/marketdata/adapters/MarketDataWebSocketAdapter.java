package net.finmath.smartcontract.marketdata.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.marketdata.util.IRMarketDataItem;
import net.finmath.smartcontract.marketdata.util.IRMarketDataParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


import java.util.*;
import java.util.stream.Collectors;

public class MarketDataWebSocketAdapter extends WebSocketAdapter   {// implements Callable<String> {

    private final JsonNode authJson;
    private final String position;
    private final Map<String, IRMarketDataItem> marketdataItemMap;

    final private PublishSubject<String> publishSubject;

    final private Sinks.Many<String> sink;

    boolean requestSent;

    public MarketDataWebSocketAdapter(JsonNode authJson, String position, List<IRMarketDataItem> itemList) {
        this.authJson = authJson;
        this.position = position;
        this.marketdataItemMap = itemList.stream().collect(Collectors.toMap(r->r.getRic(),r->r));
        requestSent = false;
        publishSubject  = PublishSubject.create();
        sink = Sinks.many().multicast().onBackpressureBuffer();   // https://prateek-ashtikar512.medium.com/projectreactor-sinks-bac6c88e5e69

    }



    public Set<IRMarketDataItem>     getMarketDataItems(){
        return marketdataItemMap.values().stream().collect(Collectors.toSet());
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
        long quotesRetrieved = this.getMarketDataItems().stream().filter(item -> item.getValue() != null).count();
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
                        this.marketdataItemMap.get(ric).setValue(quote);
                        this.marketdataItemMap.get(ric).setDate(fields.get("VALUE_DT1").textValue()); //fields.getString("VALUE_DT1"), fields.getString("VALUE_TS1")
                        this.marketdataItemMap.get(ric).setTimestamp(fields.get("VALUE_TS1").textValue());
                    }

                }
            } catch (Exception e) {
                //  System.out.println("FAIL");
//                websocket.disconnect();
            }
        }


        if (quotesRetrieved == this.getMarketDataItems().size()) {

            String json = IRMarketDataParser.serializeToJson(this.getMarketDataItems());
            this.publishSubject.onNext(json);
            this.sink.tryEmitNext(json);
            this.getMarketDataItems().forEach(item -> {
                item.setValue(null);
            });
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

        for (IRMarketDataItem item : this.marketdataItemMap.values())
            ricsAsString +=  "\"" + item.getRic() + "\",";
        ricsAsString = ricsAsString.substring(0,ricsAsString.length()-1);
        ricsAsString += "]}";

        return ricsAsString;


    }

    /*
    @Override
    public String call() throws Exception {
        while(true) {
            long quotesRetrieved = this.getMarketDataItems().stream().filter(item -> item.getValue() != null).count();
            if (quotesRetrieved == this.getMarketDataItems().size()) {
                String json = IRMarketDataParser.serializeToJson(this.getMarketDataItems());
                try {
                    this.getMarketDataItems().forEach(item -> {
                        item.setValue(null);
                    });
                    requestSent = false;
                    return json;
                } catch (Exception e) {
                    return "";
                }
            }
    }*/


}