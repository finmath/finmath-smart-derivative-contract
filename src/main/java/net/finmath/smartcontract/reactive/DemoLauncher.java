package net.finmath.smartcontract.reactive;


import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import net.finmath.smartcontract.marketdata.adapters.MarketDataWebSocketAdapter;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParser;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class DemoLauncher {


	public static void main(String[] args) throws Exception {

		String sdcXML = new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/sdc2.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<CalibrationDataItem.Spec> mdItemList = sdc.getMarketdataItemList();

		/* Load connection properties*/
		String connectionPropertiesFile = "Q:\\refinitiv_connect.properties";
		Properties properties = new Properties();
		properties.load(new FileInputStream(connectionPropertiesFile));

		/* Init Websockect Connection*/
		final WebSocketConnector connector = new WebSocketConnector(properties);
		final WebSocket socket = connector.getWebSocket();

		/* Market Data Adapter */
		final MarketDataWebSocketAdapter emitter = new MarketDataWebSocketAdapter(connector.getAuthJson(), connector.getPosition(), mdItemList);
		socket.addListener(emitter);
		socket.connect();

		/* Write Market Data to File */
		final Consumer<CalibrationDataset> marketDataWriter = new Consumer<CalibrationDataset>() {
			@Override
			public void accept(CalibrationDataset s) throws Throwable {
				String json = s.serializeToJson();
				String timeStamp = s.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
				System.out.println("Consumer MarketDataStorage: Stored Market Data at: " + timeStamp);
				Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
				Files.write(path, json.getBytes());
			}
		};
		//emitter.asObservable().throttleLast(10,TimeUnit.SECONDS).subscribe(marketDataWriter);

		Path dir = Paths.get("C:\\Temp\\marketdata\\");  // specify your directory

		Optional<Path> lastFilePath = Files.list(dir)    // here we get the stream with full directory listing
				.filter(f -> !Files.isDirectory(f))  // exclude subdirectories from listing
				.max(Comparator.comparingLong(f -> f.toFile().lastModified()));
		lastFilePath.get().toUri();
		String json = Files.readString(lastFilePath.get());
		CalibrationDataset lastStoredSet = CalibrationParserDataItems.getScenariosFromJsonString(json).get(0);

		final Consumer<CalibrationDataset> fixingHistoryCollector = new Consumer<CalibrationDataset>() {
			CalibrationDataset fixingCollectorDataSet = lastStoredSet;

			@Override
			public void accept(CalibrationDataset calibrationDataSet) throws Throwable {
				//if (fixingCollectorDataSet == null || (fixingCollectorDataSet.getFixingDataItems().size() != calibrationDataSet.getFixingDataItems().size())){
				fixingCollectorDataSet = fixingCollectorDataSet == null ? calibrationDataSet : calibrationDataSet.getClonedFixingsAdded(fixingCollectorDataSet.getFixingDataItems());
				String json = fixingCollectorDataSet.serializeToJson();
				String timeStamp = fixingCollectorDataSet.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
				System.out.println("Consumer MarketDataStorage: Stored Market Data at: " + timeStamp);
				Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
				Files.write(path, json.getBytes());
				//fixingCollectorDataSet = calibrationDataSet;
				//
			}
		};
		//emitter.asObservable().throttleFirst(60,TimeUnit.MINUTES).subscribe(fixingHistoryCollector);

		/* Print Market Values */
		Consumer<ValueResult> printValues = (ValueResult s) -> System.out.println("Consumer ValuationPrint: " + s.getValue().doubleValue());

		final Observable observableValuation = emitter.asObservable().throttleLast(5, TimeUnit.SECONDS).map(marketData -> {
			MarginCalculator calculator = new MarginCalculator();
			return calculator.getValue(marketData.serializeToJson(), sdcXML);
		});
		observableValuation.subscribe(printValues);

		/* Conditional Settlements */
        /*BigDecimal settlementTriggerValue = BigDecimal.valueOf(100.);
		ConditionalSettlementCalculator marginCalculator = new ConditionalSettlementCalculator(sdcXML,settlementTriggerValue);
		final Observable<MarginResult > conditionalSettlementEmitter = emitter.asObservable()
				.delay(1,TimeUnit.SECONDS) 						// Initial delay
				.throttleLast(5,TimeUnit.SECONDS)         // throttle a bit - backpressure
				.map(marketdata->marginCalculator.apply(marketdata)) 	// map to margin results
				.filter(result->result.getValue()!=null);			    // filter results


        conditionalSettlementEmitter.subscribe();*/

	}
}