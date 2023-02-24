package net.finmath.smartcontract.reactive;


import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import net.finmath.smartcontract.marketdata.adapters.MarketDataWebSocketAdapter;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataSet;
import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class DemoLauncher {


	public static void main(String[] args) throws Exception {


		String date = "2023-02-23";
		String time = "10:14:23";

		LocalDateTime localDateTime = LocalDateTime.parse(date + "T" +time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

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
		final MarketDataWebSocketAdapter emitter = new MarketDataWebSocketAdapter(connector.getAuthJson(),connector.getPosition(), mdItemList );
		socket.addListener(emitter);
		socket.connect();

        /* Write Market Data to File */
        final Consumer<CalibrationDataSet> marketDataWriter = new Consumer<CalibrationDataSet>() {
            @Override
            public void accept(CalibrationDataSet s) throws Throwable {
                String json = s.serializeToJson();
                String timeStamp = s.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
                System.out.println("Consumer MarketDataStorage: Stored Market Data at: " + timeStamp);
                Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
                Files.write(path,json.getBytes());
            }
        };
        //emitter.asObservable().throttleLast(10,TimeUnit.SECONDS).subscribe(marketDataWriter);


		final Consumer<CalibrationDataSet> fixingHistoryCollector = new Consumer<CalibrationDataSet>() {
			CalibrationDataSet fixingCollectorDataSet;
			@Override
			public void accept(CalibrationDataSet calibrationDataSet) throws Throwable {
				if (fixingCollectorDataSet == null || (fixingCollectorDataSet.getFixingDataItems().size() != calibrationDataSet.getFixingDataItems().size())){
					fixingCollectorDataSet = fixingCollectorDataSet == null ? calibrationDataSet : calibrationDataSet.getClonedFixingsAdded(fixingCollectorDataSet.getFixingDataItems());
					String json = fixingCollectorDataSet.serializeToJson();
					String timeStamp = fixingCollectorDataSet.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
					System.out.println("Consumer MarketDataStorage: Stored Market Data at: " + timeStamp);
					Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
					Files.write(path,json.getBytes());
					fixingCollectorDataSet = calibrationDataSet;
				}
			}
		};
		emitter.asObservable().throttleFirst(10,TimeUnit.SECONDS).subscribe(fixingHistoryCollector);

        /* Print Market Values */
        //Consumer<ValueResult> printValues = (ValueResult s ) -> System.out.println("Consumer ValuationPrint: " +s.getValue().doubleValue() );

        /*final Observable observableValuation = emitter.asObservable().throttleLast(5,TimeUnit.SECONDS).map(marketData->{
            MarginCalculator calculator = new MarginCalculator();
            return calculator.getValue(marketData.serializeToJson(),sdcXML);
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


		while(socket.isOpen()){ // keep thread running as far is socket connection is open
			;
		}


	}
}