package net.finmath.smartcontract.valuation.implementation.reactive;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import net.finmath.smartcontract.valuation.marketdata.adapters.LiveFeedAdapter;
import net.finmath.smartcontract.valuation.marketdata.adapters.MarketDataRandomFeedAdapter;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.model.MarketDataSet;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class DemoLauncher {


	public static void main(String[] args) throws Exception {

		String sdcXML = new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<CalibrationDataItem.Spec> mdItemList = sdc.getMarketdataItemList();

		/* Load connection properties*/
		String connectionPropertiesFile = "Q:\\refinitiv_connect.properties";
		Properties properties = new Properties();
		properties.load(new FileInputStream(connectionPropertiesFile));
		InputStream marketDataMessageStream = DemoLauncher.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/nf_md_20230711-123529.json");
		final String marketDataMessageContents = new String(Objects.requireNonNull(marketDataMessageStream).readAllBytes(), StandardCharsets.UTF_8);
		MarketDataSet marketData = new ObjectMapper().registerModule(new JavaTimeModule())
				.readValue(marketDataMessageContents, MarketDataSet.class);


		/* Init Websockect Connection*/
		//final WebSocketConnector connector = new WebSocketConnector(properties);
		//final WebSocket socket = connector.getWebSocket();


		/* Market Data Adapter */
		//final LiveFeedAdapter<CalibrationDataset> emitter = new MarketDataWebSocketAdapter(connector.getAuthJson(), connector.getPosition(), mdItemList);
		final LiveFeedAdapter<CalibrationDataset> emitter = new MarketDataRandomFeedAdapter(Period.ofDays(1), new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8));
		//final LiveFeedAdapter<MarketDataSet> emitter2 = new MarketDataRandomFeedAdapter2(LocalDateTime.now(),1,marketData);

		//socket.addListener(emitter);
		//socket.connect();

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

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.INDENT_OUTPUT, true)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		final Consumer<MarketDataSet> marketDataWriter2 = new Consumer<MarketDataSet>() {
			@Override
			public void accept(MarketDataSet s) throws Throwable {
				//s.values(s.getValues().stream().map(x -> this.overnightFixingPostProcessing(x, isOvernightFixing)).toList());
				File targetFile = new File("C:\\Temp\\marketdata\\x.json");
				mapper.writerFor(MarketDataSet.class).writeValue(targetFile, s);

				//Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
				//Files.write(path, json.getBytes());
			}
		};


		Disposable d = emitter.asObservable().throttleLast(5, TimeUnit.SECONDS).subscribe(marketDataWriter);//.subscribe(s->emitter.writeDataset("C:\\Temp\\marketdata\\",s,false));
		d.dispose();
		//emitter.writeDataset("C:\\Temp\\marketdata\\",emitter.asObservable().blockingFirst(),false);
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
		/*Consumer<ValueResult> printValues = (ValueResult s) -> System.out.println("Consumer ValuationPrint: " + s.getValue().doubleValue());

		final Observable observableValuation = emitter.asObservable().throttleLast(5, TimeUnit.SECONDS).map(marketData -> {
			MarginCalculator calculator = new MarginCalculator();
			return calculator.getValue(marketData.serializeToJson(), sdcXML);
		});
		observableValuation.subscribe(printValues);*/

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