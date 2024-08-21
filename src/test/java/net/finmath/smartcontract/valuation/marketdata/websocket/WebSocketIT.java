package net.finmath.smartcontract.valuation.marketdata.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import net.finmath.smartcontract.valuation.marketdata.generators.WebSocketConnector;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

}
