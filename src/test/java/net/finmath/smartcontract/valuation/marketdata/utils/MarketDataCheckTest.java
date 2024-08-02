package net.finmath.smartcontract.valuation.marketdata.utils;

import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;
import net.finmath.smartcontract.settlement.Settlement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataCheckTest {

	@Test
	void checkMarketData_negativeCase() throws IOException {
		final String fpml = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String md = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/historicalMarketData/marketdata_2008-09-01.xml").readAllBytes(), StandardCharsets.UTF_8);

		MarketDataList mdl = SDCXMLParser.unmarshalXml(md, MarketDataList.class);
		Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(fpml, Smartderivativecontract.class);

		MarketDataErrors errors = MarketDataCheck.checkMarketData(mdl, sdc);
		assertTrue(errors.hasErrors());
		assertEquals(14, errors.getMissingDataPoints().size());
	}

	@Disabled("check format, two fixings are not included in product xml")
	@Test
	void checkMarketData_positiveCase_newFormat() throws IOException {
		final String fpml = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String md = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/settlement_testset_initial.xml").readAllBytes(), StandardCharsets.UTF_8);

		Settlement settlement = SDCXMLParser.unmarshalXml(md, Settlement.class);
		Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(fpml, Smartderivativecontract.class);

		MarketDataErrors errors = MarketDataCheck.checkMarketData(settlement.getMarketData(), sdc);
		assertFalse(errors.hasErrors());
		assertEquals(0, errors.getMissingDataPoints().size());
	}

	@Test
	void checkMarketData_positiveCase_historical() throws IOException {
		final String fpml = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_simulated_historical_marketdata.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String md = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/historicalMarketData/marketdata_2008-09-01.xml").readAllBytes(), StandardCharsets.UTF_8);

		MarketDataList mdl = SDCXMLParser.unmarshalXml(md, MarketDataList.class);
		Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(fpml, Smartderivativecontract.class);

		MarketDataErrors errors = MarketDataCheck.checkMarketData(mdl, sdc);
		assertFalse(errors.hasErrors());
		assertEquals(0, errors.getMissingDataPoints().size());
	}
}