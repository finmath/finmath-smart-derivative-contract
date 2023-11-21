package net.finmath.smartcontract.marketdata;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class MarketDataImportTest {

	@Disabled("")
	@Test
	void testParseSymbols() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String sdcXML = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<CalibrationDataItem.Spec> marketdataItems = sdc.getMarketdataItemList();

		String json2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(marketdataItems);
		Files.write(Paths.get("C:\\Temp\\text.json"), json2.getBytes());
		Assertions.assertEquals(marketdataItems.size(), 71);


	}


	@Test
	void testImport() throws Exception {
		final String jsonStr = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr);
		final String resultJson = scenarioList.get(0).serializeToJson();
		Files.write(Paths.get("C:\\Temp\\result.json"), resultJson.getBytes());
		Assertions.assertNotEquals(null, resultJson);
	}
}
