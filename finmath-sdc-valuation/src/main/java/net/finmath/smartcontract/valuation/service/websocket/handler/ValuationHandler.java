package net.finmath.smartcontract.valuation.service.websocket.handler;

import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.generators.MarketDataGeneratorWebsocket;
import net.finmath.smartcontract.valuation.marketdata.generators.WebSocketConnector;
import org.springframework.web.socket.*;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Simple ValuationHandler based on a live market data feed for a Websocket-Connection
 * <p>
 * Firste: As inbound message an xml will trigger a connect to market data adapter
 * Second: A valuation observable  will connect on the continous market data feed
 * Third: A consumer subscribes to the valuation observable and sending test messages over the websocket connection
 * -  For Demo Purposes may also implement for RandomMarketDataFeed
 * -   marketDataWebSocketAdapter should be a single instance shared across several handlers
 * -  parse subscription settings out of product XML (i.e. settlement frequency)
 * - Add other handlers
 *
 * @author Peter Kohl-Landgraf
 */

public class ValuationHandler implements WebSocketHandler {

	private MarketDataGeneratorWebsocket marketDataWebSocketAdapter;

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		String payload = null;
		if (message instanceof TextMessage) {
			payload = ((TextMessage) message).getPayload();
		} else {
			throw new IllegalStateException("Unexpected WebSocket message type: " + message);
		}
		String sdcXML = payload;//new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract2.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<CalibrationDataItem.Spec> mdItemList = sdc.getMarketdataItemList();

		/* Load connection properties*/
		String connectionPropertiesFile = "<your properties file>";
		Properties properties = new Properties();
		properties.load(new FileInputStream(connectionPropertiesFile));

		/* Init Websockect Connection*/
		final WebSocketConnector connector = new WebSocketConnector(properties);
		final WebSocket socket = connector.getWebSocket();
		/* Market Data Adapter*/
		this.marketDataWebSocketAdapter = new MarketDataGeneratorWebsocket(connector.getAuthJson(), connector.getPosition(), mdItemList);
		socket.addListener(marketDataWebSocketAdapter);
		socket.connect();
//        emitter.asObservable().throttleFirst(2, TimeUnit.SECONDS).subscribe(s->session.sendMessage(new TextMessage(s.serializeToJson().substring(2,24))));

		/* Print Market Values*/
		final Observable<ValueResult> observableValuation = marketDataWebSocketAdapter.asObservable().throttleLast(6, TimeUnit.SECONDS).map(marketData -> {
			MarginCalculator calculator = new MarginCalculator();
			return calculator.getValue(marketData.serializeToJson(), sdcXML);
		});

		observableValuation.subscribe(s -> session.sendMessage(new TextMessage(s.toString())));

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