package net.finmath.smartcontract.valuation.marketdata;


import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;


public class MarketDataImportTest {

	@Test
	void testParseSymbols()  {
		try {
			String sdcXML = new String(Objects.requireNonNull(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml")).readAllBytes(), StandardCharsets.UTF_8);
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
			final String jsonStr = new String(Objects.requireNonNull(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.json")).readAllBytes(), StandardCharsets.UTF_8);

			List<CalibrationDataset> scenarioList = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr);
			int setSize = scenarioList.get(0).getDataPoints().size();
			Assertions.assertEquals(setSize, 71);
		}
		catch(IOException e){
			Assertions.assertFalse(false);
		}
	}
}
