package net.finmath.smartcontract.valuation.marketdata;


import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.functions.Consumer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataList;
import net.finmath.smartcontract.valuation.marketdata.generators.*;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class LaunchAGenerator {


	public static void main(String[] args) throws Exception {

		String sdcXML = new String(LaunchAGenerator.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<CalibrationDataItem.Spec> mdItemList = sdc.getMarketdataItemList();

		/* Load connection properties*/
		String connectionPropertiesFile = "<your properties file>";
		Properties properties = new Properties();
		properties.load(new FileInputStream(connectionPropertiesFile));


		/* Init Websockect Connection*/
		final WebSocketConnector connector = new WebSocketConnector(properties);
		final WebSocket socket = connector.getWebSocket();


		/* Market Data Adapter */
		final MarketDataGeneratorWebsocket emitter = new MarketDataGeneratorWebsocket(connector.getAuthJson(), connector.getPosition(), mdItemList);

		socket.addListener(emitter);
		socket.connect();

		/* Write Market Data to File */
		final Consumer<MarketDataList> marketDataWriter = new Consumer<MarketDataList>() {

			@Override
			public void accept(MarketDataList s) throws Throwable {

				System.out.println("Consumer MarketDataStorage: Stored Market Data at: ");
				System.out.println(socket.isOpen());
				File file = new File("C:\\Temp\\file.xml");
				JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.marshal(s, file);
				emitter.closeStreamsAndLogoff(socket);
				socket.sendClose();

			}
		};


		emitter.asObservable().take(1).subscribe(marketDataWriter);


	}
}