package net.finmath.smartcontract.marketdata;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import net.finmath.smartcontract.marketdata.adapters.CallableRetrieveMarketdata;
import net.finmath.smartcontract.marketdata.util.IRMarketDataItem;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.marketdata.util.IRMarketDataParser;
import net.finmath.smartcontract.marketdata.util.IRMarketDataSet;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class DemoLauncherObservable {


	public static void main(String[] args) throws Exception {


		/**
		 * Maybe try map ... operators to transform an observable to an obsevable
		 * https://reactivex.io/documentation/operators.html
		 */

		String sdcXML = new String(DemoLauncher.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<IRMarketDataItem> mdItemList = sdc.getMarketdataItemList();

		/* Load connection properties*/
		String connectionPropertiesFile = Thread.currentThread().getContextClassLoader().getResource("refinitiv_connect.properties").getPath();
		Properties properties = new Properties();
		properties.load(new FileInputStream(connectionPropertiesFile));

		/* Init Websockect Connection*/
		final WebSocketConnector connector = new WebSocketConnector(properties);
		final WebSocket socket = connector.getWebSocket();

		/* Init Adapter */
		final CallableRetrieveMarketdata adapter = new CallableRetrieveMarketdata(connector.getAuthJson(),connector.getPosition(), mdItemList );
		socket.addListener(adapter);
		socket.connect();

	//	CallableRetrieveMarketData callable = new CallableRetrieveMarketData(adapter,mdItemList);

		Function<List<String>,ValuationResult> valuationResultFunction = new Function<List<String>, ValuationResult>() {
			@Override
			public ValuationResult apply(List<String> marketDataSets) throws Throwable {
				MarginCalculator calculator = new MarginCalculator();
				if (marketDataSets.size()==1 ) {
					ValuationResult result = new ValuationResult();
					result.setValue(BigDecimal.ZERO);
					return result;
				}
				else
					return calculator.getValue(marketDataSets.get(0),marketDataSets.get(1),sdcXML);
			}
		};

		Function<String, List<String> > bufferPreviousAndNewMarketData = new Function<String, List<String>>(){

			String previousmarketdata = null;

			@Override
			public List<String> apply(String s) throws Throwable {
				List<String> list = new ArrayList<>();
				if(previousmarketdata != null) {
					list.add(previousmarketdata);
				}
				previousmarketdata = s;
				list.add(s);
				return list;
			}
		};


		Predicate<ValuationResult> predicate = new Predicate<ValuationResult>() {
			@Override
			public boolean test(ValuationResult valuationResult) throws Throwable {
				if ( Math.abs(valuationResult.getValue().doubleValue())> 1000)
					return true;
				else
					return false;
			}
		};

		Observable<ValuationResult> observable = Observable
				.fromCallable(adapter)
				.delay(5,TimeUnit.SECONDS).repeat()  // buffer
				.map(s->bufferPreviousAndNewMarketData.apply(s))
				.map(s->valuationResultFunction.apply(s))
				.filter(s->predicate.test(s));

		Consumer<ValuationResult> printvaluations2 = new Consumer<ValuationResult>() {
			@Override
			public void accept(ValuationResult s) throws Throwable {
				System.out.println(" - Settlement-Value: " +s.getValue().doubleValue());
			}
		};

		observable.subscribe(printvaluations2);

		Consumer<String> printValuations = new Consumer<String>() {
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


		/*Observable<ValuationResult> valuationResultObservable = observable.map(s->{
			MarginCalculator calculator = new MarginCalculator();
			ValuationResult result = calculator.getValue(s,sdcXML);
			return result;
		});*/

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

		while(socket.isOpen()){
			;
		}
	}

}    
