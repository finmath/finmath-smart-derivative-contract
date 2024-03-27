package net.finmath.smartcontract.valuation.marketdata;


import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.functions.Consumer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.finmath.smartcontract.model.MarketDataList;
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
		//String connectionPropertiesFile = "<propertiesfile>";
		String connectionPropertiesFile = "./finmath-smart-derivative-contract/src/main/resources/refinitiv_connect.properties";
		Properties properties = new Properties();
		properties.load(LaunchAGenerator.class.getClassLoader().getResourceAsStream("refinitiv_connect.properties"));
		//properties.load(new FileInputStream(connectionPropertiesFile));


		/* Init Websockect Connection*/
		final WebSocketConnector connector = new WebSocketConnector(properties);
		final WebSocket socket = connector.getWebSocket();


		/* Market Data Adapter */
		final MarketDataGeneratorWebsocket emitter = new MarketDataGeneratorWebsocket(connector.getAuthJson(), connector.getPosition(), mdItemList);

		socket.addListener(emitter);
		socket.connect();

		/* Write Market Data to File */
		final Consumer<MarketDataList> marketDataWriter = new Consumer<>() {

			@Override
			public void accept(MarketDataList s) throws Throwable {

				System.out.println("Consumer MarketDataStorage: Stored Market Data at: ");
				System.out.println(socket.isOpen());
				//TODO replace file
				File file = new File("md_testset3.xml");
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