package net.finmath.smartcontract.service.websocket.handler;

import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import net.finmath.smartcontract.marketdata.adapters.MarketDataWebSocketAdapter;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Simple ValuationHandler based on a live market data feed for a Websocket-Connection
 *
 * Firste: As inbound message an xml will trigger a connect to market data adapter
 * Second: A valuation observable  will connect on the continous market data feed
 * Third: A consumer subscribes to the valuation observable and sending test messages over the websocket connection
 * @Todo: For Demo Purposes may also implement for RandomMarketDataFeed
 * @Todo: marketDataWebSocketAdapter should be a single instance shared across several handlers
 * @Todo: parse subscription settings out of product XML (i.e. settlement frequency)
 * @Todo: Add other handlers
 *
 * @author Peter Kohl-Landgraf
 */

public class ValuationHandler implements WebSocketHandler {

    private MarketDataWebSocketAdapter marketDataWebSocketAdapter;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = null;
        if (message instanceof TextMessage) {
            payload = ((TextMessage) message).getPayload();
        }
        else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
        String sdcXML = payload;//new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/sdc2.xml").readAllBytes(), StandardCharsets.UTF_8);
        SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
        List<CalibrationDataItem.Spec> mdItemList = sdc.getMarketdataItemList();

        /* Load connection properties*/
        String connectionPropertiesFile = "Q:\\refinitiv_connect.properties";
        Properties properties = new Properties();
        properties.load(new FileInputStream(connectionPropertiesFile));

        /* Init Websockect Connection*/
        final WebSocketConnector connector = new WebSocketConnector(properties);
        final WebSocket socket = connector.getWebSocket();
        /* Market Data Adapter*/
        this.marketDataWebSocketAdapter = new MarketDataWebSocketAdapter(connector.getAuthJson(),connector.getPosition(), mdItemList );
        socket.addListener(marketDataWebSocketAdapter);
        socket.connect();
//        emitter.asObservable().throttleFirst(2, TimeUnit.SECONDS).subscribe(s->session.sendMessage(new TextMessage(s.serializeToJson().substring(2,24))));

        /* Print Market Values*/
        final Observable<ValueResult> observableValuation = marketDataWebSocketAdapter.asObservable().throttleLast(6,TimeUnit.SECONDS).map(marketData->{
            MarginCalculator calculator = new MarginCalculator();
            return calculator.getValue(marketData.serializeToJson(),sdcXML);
        });

       observableValuation.subscribe(s->session.sendMessage(new TextMessage(s.toString())));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }

}