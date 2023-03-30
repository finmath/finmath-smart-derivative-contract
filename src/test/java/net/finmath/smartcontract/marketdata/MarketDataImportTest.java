package net.finmath.smartcontract.marketdata;



import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.demo.VisualiserSDC;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class MarketDataImportTest {



	@Disabled("")
	@Test
	void testParseSymbols()  throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String sdcXML = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		List<CalibrationDataItem.Spec> marketdataItems = sdc.getMarketdataItemList();

		String json2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(marketdataItems);
		Files.write(Paths.get("C:\\Temp\\text.json"),json2.getBytes());
		Assertions.assertEquals(marketdataItems.size(), 71);



	}


	@Test
	void testImport() throws Exception {
		final String fileName = "references"+ File.separator + "timeseriesdatamap.json";
		final File startPoint = new File(VisualiserSDC.class.getResource(VisualiserSDC.class.getSimpleName()+".class").getPath());
		final File file = new File (startPoint.getParentFile().getParentFile().getParentFile().getParentFile().getParent()+File.separator+fileName);
		String jsonStr;
		try {
			jsonStr = new String((new FileInputStream(file)).readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw e;
		}
		List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr);
		final String resultJson = scenarioList.get(0).serializeToJson();
		Files.write(Paths.get("C:\\Temp\\result.json"),resultJson.getBytes());
		Assertions.assertNotEquals(null, resultJson);
	}
}
