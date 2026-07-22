package net.finmath.smartcontract.valuation.marketdata.generators;

import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.functions.Consumer;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MarketDataGeneratorLauncher {

	private static final Logger logger = LoggerFactory.getLogger(MarketDataGeneratorLauncher.class);

	private MarketDataGeneratorLauncher() {}

	public static MarketDataList instantiateMarketDataGeneratorWebsocket(Properties connectionProperties, SmartDerivativeContractDescriptor sdc) {
		AtomicReference<MarketDataList> marketDataList = new AtomicReference<>(new MarketDataList());
		AtomicBoolean finished = new AtomicBoolean(false);

		logger.info("launching MarketDataGeneratorWebsocket");
		List<CalibrationDataItem.Spec> mdItemList;
		WebSocketConnector connector;
		WebSocket socket;
		MarketDataGeneratorWebsocket emitter;
		try {
			mdItemList = sdc.getMarketdataItemList();

			// Init Websockect Connection
			connector = new WebSocketConnector(connectionProperties);
			socket = connector.getWebSocket();
			emitter = new MarketDataGeneratorWebsocket(connector.getAuthJson(), connector.getPosition(), mdItemList);
			socket.addListener(emitter);
			socket.connect();

			final Consumer<MarketDataList> marketDataWriter = s -> {
				logger.info("websocket open: {}", socket.isOpen());
				marketDataList.set(s);
				finished.set(true);

				emitter.closeStreamsAndLogoff(socket);
				socket.sendClose();
			};
			emitter.asObservable().take(1).subscribe(marketDataWriter);
			while (!finished.get()) {
				logger.info("Waiting for Market Data List to finish retrieving");
				Thread.sleep(1000);
			}
			return marketDataList.get();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
