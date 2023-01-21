package net.finmath.smartcontract.reactive;


import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import net.finmath.smartcontract.marketdata.DemoLauncher;
import net.finmath.smartcontract.marketdata.adapters.MarketDataWebSocketAdapter;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.marketdata.util.IRMarketDataItem;
import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class LauncherValuationPipeline {


	public static void main(String[] args) throws Exception {


		String sdcXML = new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<IRMarketDataItem> mdItemList = sdc.getMarketdataItemList();

		/* Load connection properties*/
		String connectionPropertiesFile = "C:\\connect_.properties";
		Properties properties = new Properties();
		properties.load(new FileInputStream(connectionPropertiesFile));

		/* Init Websockect Connection*/
		final WebSocketConnector connector = new WebSocketConnector(properties);
		final WebSocket socket = connector.getWebSocket();

		/* Init Adapter */
		final MarketDataWebSocketAdapter emitter = new MarketDataWebSocketAdapter(connector.getAuthJson(),connector.getPosition(), mdItemList );
		socket.addListener(emitter);
		socket.connect();

        /* This is how a emitter is used as a Flux*/
        //emitter.asFlux().subscribe(System.out::println);


        BigDecimal settlementTriggerValue = BigDecimal.valueOf(2000);
		FunctionMarginCalculator marginCalculator = new FunctionMarginCalculator(sdcXML,settlementTriggerValue);

		//BigDecimal settlementTriggerValue = BigDecimal.valueOf(1000.);
		FilterMarginResultOrTime filter = new FilterMarginResultOrTime(null,null,settlementTriggerValue);

		Observable<MarginResult > observable = emitter.asObservable()
				.delay(1,TimeUnit.SECONDS) 						// Initial delay
				.throttleFirst(5,TimeUnit.SECONDS)		    // throttle a bit - backpressure
				.map(marketdata->marginCalculator.apply(marketdata)) 	// map to margin results
				.filter(result->result.getValue()!=null);			    // filter results

		Consumer<MarginResult> printvaluations2 = new Consumer<MarginResult>() {
			@Override
			public void accept(MarginResult s) throws Throwable {
				System.out.println(" - Settlement-Value: " +s.getValue().doubleValue());
			}
		};

		observable.subscribe(printvaluations2);


		/*Consumer<String> writeToFile = new Consumer<String>() {
			@Override
			public void accept(String s) throws Throwable {

					System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")) + ": " + s.substring(0,min(s.length(),2)));
				LocalDateTime time = LocalDateTime.now();
				Path path = Paths.get("C:\\Temp\\marketdata\\md_" + time.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".json");
				//Files.write(path,s.getBytes());
			}
		};*/



		while(socket.isOpen()){ // keep thread running as far is socket connection is open
			;
		}


	}



}


/**
 *
 /*Consumer<String> printValuations = new Consumer<String>() {
 String previousMarketData;
 MarginCalculator calculator = new MarginCalculator();

 @Override
 public void accept(String s) throws Throwable {
 double value;
 if (previousMarketData==null ) {
 ObjectMapper mapper = new ObjectMapper();
 JsonNode node = mapper.readTree(s);
 value = calculator.getValue(s, sdcXML).getValue().doubleValue();

 }
 else{
 value = calculator.getValue(previousMarketData,s,sdcXML).getValue().doubleValue();
 }
 System.out.println(value);
 previousMarketData = s;

 }
 };

 Consumer<List<String> > printOnly = new Consumer<List<String> >() {
 @Override
 public void accept(List<String> strings) throws Throwable {
 MarginCalculator calculator = new MarginCalculator();
 List<IRMarketDataSet> set = IRMarketDataParser.getScenariosFromJsonString(strings.get(0));
 System.out.println(set.get(0).getDate());
 List<IRMarketDataSet> set2 = IRMarketDataParser.getScenariosFromJsonString(strings.get(1));
 System.out.println(set2.get(0).getDate());
 double value = calculator.getValue(strings.get(0),strings.get(1),sdcXML).getValue().doubleValue();
 System.out.println(value);
 }
 };


 Consumer<String> writeToFile = new Consumer<String>() {
 @Override
 public void accept(String s) throws Throwable {
 System.out.println(s);
 LocalDateTime time = LocalDateTime.now();
 Path path = Paths.get("C:\\Temp\\marketdata\\md_" + time.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".json");
 Files.write(path,s.getBytes());
 }
 };

 //		observable.subscribe(printValuations);
 */
		/*
		Predicate<MarginResult> predicate = new Predicate<MarginResult>() {
			@Override
			public boolean test(MarginResult valuationResult) throws Throwable {
				if ( Math.abs(valuationResult.getValue().doubleValue())> 1000)
					return true;
				else
					return false;
			}
		};*/

		/*Observable<MarginResult> observable = Observable
				.fromCallable(adapter)
				.delay(5,TimeUnit.SECONDS).repeat()  // buffer
				.map(s->bufferPreviousAndNewMarketData.apply(s))
				.map(s->valuationResultFunction.apply(s))
				.filter(s->predicate.test(s));*/

		/*Predicate<List<String> > filterForTime = new Predicate<List<String>>() {
			LocalDateTime filterTime = LocalDate.now().atTime(17,00);
			@Override
			public boolean test(List<String> strings) throws Throwable {
				if (LocalTime.now().isAfter(LocalTime.of(17,00))) {
					filterTime = LocalDate.now().plusDays(1).atTime(17,00);
					return true;
				}
				else
					return false;
			}
		};*/
