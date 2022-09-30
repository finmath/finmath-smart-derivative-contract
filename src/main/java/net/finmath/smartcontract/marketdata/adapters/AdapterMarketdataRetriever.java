package net.finmath.smartcontract.marketdata.adapters;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AdapterMarketdataRetriever extends WebSocketAdapter {

    private final JSONObject authJson;
    private final String position;
    private final Map<String,MarketdataItem> marketdataItemMap;

    public AdapterMarketdataRetriever(JSONObject authJson, String position, List<MarketdataItem> itemList) {
        this.authJson = authJson;
        this.position = position;
        this.marketdataItemMap = itemList.stream().collect(Collectors.toMap(r->r.getRic(),r->r));
    }


    public Set<MarketdataItem>     getMarketDataItems(){
        return marketdataItemMap.values().stream().collect(Collectors.toSet());
    }

    /**
     * Called when handshake is complete and websocket is open, send login
     */
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws JSONException {
        System.out.println("WebSocket successfully connected!");
        sendLoginRequest(websocket, authJson.getString("access_token"), true);

    }

    public void onTextMessage(WebSocket websocket, String message) throws JSONException {

        if (!message.isEmpty()) {
            JSONArray jsonArray = new JSONArray(message);

            sendRICRequest(websocket);

            try {
                for (int i = 0; i < jsonArray.toList().size(); i++) {
                    if (jsonArray.getJSONObject(i).has("Fields")) {
                        String ric = jsonArray.getJSONObject(i).getJSONObject("Key").getString("Name");
                        JSONObject fields = (JSONObject) jsonArray.getJSONObject(i).get("Fields");
                        Double BID = fields.has("BID") ? fields.getDouble("BID") : null;
                        Double ASK = fields.has("ASK") ? fields.getDouble("ASK") : null;
                        Double quote = ASK == null ? BID : BID == null ? ASK : (BID + ASK) / 2.0 / 100.;
                        this.marketdataItemMap.get(ric).setValue(quote);
                        this.marketdataItemMap.get(ric).setDate(fields.getString("VALUE_DT1")); //fields.getString("VALUE_DT1"), fields.getString("VALUE_TS1")
                        this.marketdataItemMap.get(ric).setTimestamp(fields.getString("VALUE_TS1"));
                     }
                }
            } catch (Exception e) {
                System.out.println("FAIL");
//                websocket.disconnect();
            }
        }

    }



    /**
     * Create and send simple Market Price request
     * @param websocket Websocket to send the message on
     * @throws JSONException
     */
    public void sendRICRequest(WebSocket websocket) throws JSONException {
        String requestJsonString;
        String keyString1 = ricsToString(); //;+ ",\"Service\":\""; // + this.refinitiv_service_key + "\"}}"; //
        requestJsonString = "{\"ID\":2,"+keyString1+",\"View\":[\"MID\",\"BID\",\"ASK\",\"VALUE_DT1\",\"VALUE_TS1\"]}";
        JSONObject mpRequestJson = new JSONObject(requestJsonString);
        websocket.sendText(requestJsonString);
        System.out.println("SENT:\n" + mpRequestJson.toString(2));


    }



    public void sendLoginRequest(WebSocket websocket, String authToken, boolean isFirstLogin) throws JSONException {
        String loginJsonString = "{\"ID\":1,\"Domain\":\"Login\",\"Key\":{\"Elements\":{\"ApplicationId\":\"\",\"Position\":\"\",\"AuthenticationToken\":\"\"},\"NameType\":\"AuthnToken\"}}";
        JSONObject loginJson = new JSONObject(loginJsonString);
        loginJson.getJSONObject("Key").getJSONObject("Elements").put("AuthenticationToken", authToken);
        loginJson.getJSONObject("Key").getJSONObject("Elements").put("ApplicationId", "256");
        loginJson.getJSONObject("Key").getJSONObject("Elements").put("Position", this.position);

        if (!isFirstLogin) // If this isn't our first login, we don't need another refresh for it.
            loginJson.put("Refresh", false);

        websocket.sendText(loginJson.toString());
        System.out.println("SENT:\n" + loginJson.toString(2));
    }


    private String      ricsToString(){

        String ricsAsString = "\"Key\":{\"Name\":[";

        for (MarketdataItem item : this.marketdataItemMap.values())
            ricsAsString = ricsAsString + "\"" + item.getRic() + "\",";
        ricsAsString = ricsAsString.substring(0,ricsAsString.length()-1);
        ricsAsString += "]}";

        return ricsAsString;


    }

    /*public void processMessage(WebSocket websocket, JSONObject messageJson) throws JSONException {
        String messageType = messageJson.getString("Type");

        switch(messageType)
        {
            case "Refresh":
            case "Status":
                if(messageJson.has("Domain")) {
                    String messageDomain = messageJson.getString("Domain");
                    if(messageDomain.equals("Login")) {
                        // Check message state to see if login succeeded. If so, send item request. Otherwise stop.
                        JSONObject messageState = messageJson.optJSONObject("State");
                        if (messageState != null)
                        {
                            if (!messageState.getString("Stream").equals("Open") || !messageState.getString("Data").equals("Ok"))
                            {
                                System.out.println("Login failed.");
                                exit(1);
                            }

                            // Login succeeded, send item request.
                            sendRequest(websocket);

                        }

                    }
                }
                break;

            case "Ping":
                String pongJsonString = "{\"Type\":\"Pong\"}";
                JSONObject pongJson = new JSONObject(pongJsonString);
                websocket.sendText(pongJsonString);
                System.out.println("SENT:\n" + pongJson.toString(2));
                break;
            default:
                break;
        }
    }*/



}