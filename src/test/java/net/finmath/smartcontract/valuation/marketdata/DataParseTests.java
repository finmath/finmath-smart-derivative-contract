package net.finmath.smartcontract.valuation.marketdata;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.JAXBTests;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataList;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataParseTests {






	@Test
	void testParseSymbols()  {
		try {
			String sdcXML = new String(Objects.requireNonNull(DataParseTests.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml")).readAllBytes(), StandardCharsets.UTF_8);
			SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
			List<CalibrationDataItem.Spec> marketdataItems = sdc.getMarketdataItemList();

			Assertions.assertEquals(marketdataItems.size(), 72);
		}
		catch(IOException | SAXException | ParserConfigurationException exception){
			Assertions.assertFalse(false);
		}
	}


	@Test
	void xmlGenerationTest() throws Exception{
		MarketDataPoint point = new MarketDataPoint("test",1.0, LocalDateTime.now());
		MarketDataList set = new MarketDataList();
		set.add(point);
		JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(set, writer);
		String xmlStr = writer.toString();

		String json = set.serializeToJson();


		Assertions.assertTrue(!xmlStr.isEmpty() && !json.isEmpty());
	}

	 @Test
	 void readHistoricJsonScenariosIntoMarketDataListObjects() throws Exception{
		 final LocalDate startDate = LocalDate.of(2007, 1, 1);
		 final LocalDate maturity = LocalDate.of(2012, 1, 3);
		 final String fileName = "timeseriesdatamap.json";
		 final List<CalibrationDataset> scenarioListRaw = CalibrationParserDataItems.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		 final List<CalibrationDataset> scenarioList = scenarioListRaw.stream().map(scenario -> scenario.getScaled(100)).toList();
		 final String productData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		 SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);
		 List<CalibrationDataItem.Spec> specList = productDescriptor.getMarketdataItemList();

		 Set<CalibrationDataItem> items  = scenarioList.get(0).getDataPoints().stream().map(point -> {
				 CalibrationDataItem.Spec spec_with_ric = specList.stream().filter(spec -> spec.getMaturity().equals(point.getMaturity()) && spec.getProductName().equals(point.getProductName()) && spec.getCurveName().equals(point.getCurveName())).findAny().orElse(null);
		 		 return spec_with_ric == null ?  null : new CalibrationDataItem(spec_with_ric, point.getQuote(), point.getDateTime());
			 }).filter(Objects::nonNull).collect(Collectors.toSet());




			 final List<MarketDataList> ListOfMarketDataLists = scenarioList.stream().map(CalibrationDataset::toMarketDataList).toList();
		 JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
		 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		 jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		 for(int i=0; i<scenarioList.size();i++) {
			// FileWriter fileWriter = new FileWriter("C:\\Temp\\marketdata_.xml");
			 //jaxbMarshaller.marshal(md, fileWriter);
		 }
		 Assertions.assertEquals(131,ListOfMarketDataLists.size());

		 /*Need  */
	 }

	@Test
	void testXMLToCalibrationSet() throws Exception{
		final String productData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		List<CalibrationDataItem.Spec> specList = productDescriptor.getMarketdataItemList();
		CalibrationDataset set = CalibrationParserDataItems.getCalibrationDataSetFromXML(marketData,specList);
		Assertions.assertEquals(specList.size(), set.getDataPoints().size()+2); //Fixings are missing in this marketdataset

	}


//@Disabled
	@Test
	@Disabled
	void changeSymbols() throws Exception{
		//String sdcXML = new String(DataParseTests.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		 String sdcXML = new String(JAXBTests.class.getClassLoader().getResourceAsStream("generators/eur_euribor_y_s_with_fixings.xml").readAllBytes(), StandardCharsets.UTF_8);


		String path = JAXBTests.class.getClassLoader().getResource("net.finmath.smartcontract.product.xml/smartderivativecontract_with_rics.xml").getPath();
		File file = new File(path);

		JAXBContext jaxbContext = JAXBContext.newInstance(Smartderivativecontract.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();


		Smartderivativecontract sdc = (Smartderivativecontract) jaxbUnmarshaller.unmarshal(file);


		Smartderivativecontract.Settlement.Marketdata.Marketdataitems items = sdc.getSettlement().getMarketdata().getMarketdataitems();
		List<Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item> list = items.getItem();
		Map<String,String> dict = new HashMap<>();
		list.stream().forEach(it->{
			String symbol = (it.getCurve().toString().replaceAll("Euribor6M","EUB6").replaceAll("ESTR","ESTR")+it.getType().toString().replaceAll("Fixing","FIX").replaceAll("Deposit","DEP").replaceAll("Forward-Rate-Agreement","FRA").replaceAll("Swap-Rate","SWP")+it.getTenor().toString()).replaceAll("\\[","").replaceAll("\\]","");
			dict.put(it.getSymbol().toString().replaceAll("\\[","").replaceAll("\\]",""),symbol);
		});


		/*Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		marshaller.marshal(sdc, outputStream);
		String xmlString = outputStream.toString().replaceAll("<fpml:dataDocument fpmlVersion=\"5-9\">", "<dataDocument fpmlVersion=\"5-9\" xmlns=\"http://www.fpml.org/FpML-5/confirmation\">").replaceAll("fpml:", "");
		*/

		for(Map.Entry entry : dict.entrySet())
			sdcXML = sdcXML.replaceAll(entry.getKey().toString(), entry.getValue().toString());


		FileWriter writer = new FileWriter("xml_adj.xml");
		writer.write(sdcXML);
		writer.close();
	}


}
