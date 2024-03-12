package net.finmath.smartcontract.valuation.marketdata;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataList;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataFormatTests {

	@Test
	void testParseSymbols()  {
		try {
			String sdcXML = new String(Objects.requireNonNull(DataFormatTests.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml")).readAllBytes(), StandardCharsets.UTF_8);
			SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
			List<CalibrationDataItem.Spec> marketdataItems = sdc.getMarketdataItemList();

			Assertions.assertEquals(marketdataItems.size(), 72);
		}
		catch(IOException | SAXException | ParserConfigurationException exception){
			Assertions.assertFalse(false);
		}
	}


	@Test
	void testImportMarketDataJson(){
		try {
			final String jsonStr = new String(Objects.requireNonNull(DataFormatTests.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.json")).readAllBytes(), StandardCharsets.UTF_8);

			List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr);
			int setSize = scenarioList.get(0).getDataPoints().size();
			Assertions.assertEquals(setSize, 71);
		}
		catch(IOException e){
			Assertions.assertFalse(false);
		}
	}
	@Test
	void xmlGenerationTest() throws Exception{
		MarketDataPoint point = new MarketDataPoint("test","curve","swap","10Y",0.0, LocalDateTime.now());
		MarketDataList set = new MarketDataList();
		set.add(point);
		File file = new File("C:\\Temp\\file.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(set, file);
		jaxbMarshaller.marshal(set, System.out);

		String json = set.serializeToJson();
		System.out.println(json);

		Assertions.assertTrue(Thread.currentThread().isAlive());
	}

	 @Test
	 void readHistoricJsonScenariosIntoXML() throws Exception{
		 final LocalDate startDate = LocalDate.of(2007, 1, 1);
		 final LocalDate maturity = LocalDate.of(2012, 1, 3);
		 final String fileName = "timeseriesdatamap.json";
		 final List<CalibrationDataset> scenarioListRaw = CalibrationParserDataItems.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		 final List<CalibrationDataset> scenarioList = scenarioListRaw.stream().map(scenario -> scenario.getScaled(100)).toList();

		 final List<MarketDataList> ListOfMarketDataLists = scenarioList.stream().map(CalibrationDataset::toMarketDataList).toList();

		 Assertions.assertEquals(131,ListOfMarketDataLists.size());
	 }



}
