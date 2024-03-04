package net.finmath.smartcontract.marketdata;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class MarketDataImportTest {

	@Test
	void testParseSymbols() throws Exception {
		try {
			String sdcXML = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
			SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
			List<CalibrationDataItem.Spec> marketdataItems = sdc.getMarketdataItemList();

			Assertions.assertEquals(marketdataItems.size(), 72);
		}
		catch(Exception e){
			Assertions.assertFalse(false);
		}
	}


	@Test
	void testImportMarketDataJson() throws Exception {
		try {
			final String jsonStr = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);

			List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr);
			int setSize = scenarioList.get(0).getDataPoints().size();
			Assertions.assertEquals(setSize, 71);
		}
		catch(Exception e){
			Assertions.assertFalse(false);
		}
	}
}
