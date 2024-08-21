package net.finmath.smartcontract.valuation.marketdata;

import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.valuation.marketdata.generators.MarketDataGeneratorWebsocket;
import net.finmath.smartcontract.valuation.marketdata.generators.WebSocketConnector;
import org.junit.jupiter.api.*;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeneratorTests {


	Properties properties;
	List<CalibrationDataItem.Spec> mdItemList; // List of Calibration Item Definitions

	WebSocketConnector webSocketConnector;

	@BeforeAll
	@Disabled
	void init() throws Exception{
		//String connectionPropertiesFile = "<your properties file>";
		//properties = new Properties();
		//properties.load(new FileInputStream(connectionPropertiesFile));
		//String sdcXML = new String(LaunchAGenerator.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		//SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		//mdItemList = sdc.getMarketdataItemList();
	}

	@Test
	@Disabled
	void testMarketDataGeneration(){
		try {
			final MarketDataGeneratorWebsocket generator = new MarketDataGeneratorWebsocket(webSocketConnector.getAuthJson(), webSocketConnector.getPosition(), mdItemList);

			WebSocket webSocket = webSocketConnector.getWebSocket();
			webSocket.addListener(generator);
			webSocket.connect();


			final Consumer<MarketDataList> marketDataConsumer = s -> {
				StringWriter writer = new StringWriter();
				JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.marshal(s, writer);
				String outputXML  = writer.toString();
				Assertions.assertTrue(true);
				generator.closeStreamsAndLogoff(webSocket);
			};

			Disposable d = generator.asObservable().take(1).subscribe(marketDataConsumer);
			//generator.closeStreamsAndLogoff(webSocket);
			//webSocket.disconnect();



		}
		catch(Exception e){
			Assertions.fail();
		}
	}

}
