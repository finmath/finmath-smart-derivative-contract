package net.finmath.smartcontract.valuation.marketdata.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import net.finmath.smartcontract.valuation.marketdata.generators.MarketDataGeneratorWebsocket;
import net.finmath.smartcontract.valuation.marketdata.generators.WebSocketConnector;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * ONLY ON DEV ENVIRONMENT
 * Integration test for websocket connection to market data provider
 *
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebSocketIT {

    // @Value("${storage.internals.marketDataProviderConnectionPropertiesFile}")
    private String connectionPropertiesFile;
    private Properties properties;

    private CountDownLatch latch;
    private boolean isOnPongFrameCalled;

    @BeforeAll
    void init() throws Exception {
        connectionPropertiesFile = "config/market_data_connect.properties"; // TODO: read-in via application.yml

        this.properties = new Properties();
        this.properties.load(new FileInputStream(connectionPropertiesFile));
    }

    @BeforeEach
    void setup() {
        this.latch = new CountDownLatch(1);
        this.isOnPongFrameCalled = false;
    }

    @Test
    void testWebsocketPing() throws Exception {
        WebSocketListener listener = new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket webSocket, Map<String, List<String>> map) throws Exception {
                System.out.println("onConnected called");
                webSocket.sendPing("TEST");
            }

            @Override
            public void onPongFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {
                System.out.println("onPongFrame called: " + webSocketFrame.getPayloadText());
                isOnPongFrameCalled = true;
                latch.countDown();
            }
        };

        WebSocketConnector webSocketConnector = new WebSocketConnector(properties);
        WebSocket ws = webSocketConnector.getWebSocket();
        ws.addListener(listener);
        System.out.println("Connecting...");
        ws.connect();
        System.out.println("Waiting for pong...");
        boolean timeOut = !latch.await(5, TimeUnit.SECONDS); // Blocks this thread until latch is counted down

        assertTrue(isOnPongFrameCalled && !timeOut, "No pong or timeout");
        System.out.println("Disconnecting ws...");
        ws.disconnect();
	    // TODO: Assert disconnect?
    }

    @Test
    void testWebsocketConnection() {
        // TODO: Test ws.connect() only?
    }

    @Test
    void testMarketDataProvision() throws Exception {
        WebSocketConnector wsConnector = new WebSocketConnector(properties);

        WebSocketListener listener = new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket webSocket, Map<String, List<String>> map) throws Exception {
                System.out.println("onConnected called");
                MarketDataGeneratorWebsocket.sendLoginRequest(
                        webSocket,
                        wsConnector.getAuthJson().getString("access_token"),
                        true, wsConnector.getPosition()
                );
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) {
                // if (containsLoginResponse(text))
                System.out.println("NEW TEXT MESSAGE: " + text); // TODO: let's assume response for login request
                latch.countDown();


            }

        };


        WebSocket ws = wsConnector.getWebSocket();

        ws.addListener(listener);
        System.out.println("Connecting...");
        ws.connect();
        System.out.println("Waiting for login...");
        boolean timeOut = !latch.await(5, TimeUnit.SECONDS);


//        String ricString = ""; //;+ ",\"Service\":\""; //  + "\"}}"; //
//        String requestJsonString = "{\"ID\":2," + ricString + ",\"View\":[\"MID\",\"BID\",\"ASK\",\"VALUE_DT1\",\"VALUE_TS1\"]}";
//        ws.sendText(requestJsonString);


        // Adapter
        // Websocket
        // Login
        // Request
        // onText...?


    }

}
