package net.finmath.smartcontract.valuation.marketdata.utils;

import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;
import net.finmath.smartcontract.settlement.Settlement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataCheckTest {

	private static final Logger logger = LoggerFactory.getLogger(MarketDataCheckTest.class);

	private final List<String> files = List.of("marketdata_2008-05-02.xml", "marketdata_2008-05-05.xml", "marketdata_2008-05-06.xml", "marketdata_2008-05-07.xml", "marketdata_2008-05-08.xml", "marketdata_2008-05-09.xml", "marketdata_2008-06-18.xml", "marketdata_2008-06-19.xml", "marketdata_2008-06-20.xml", "marketdata_2008-06-23.xml", "marketdata_2008-06-24.xml", "marketdata_2008-06-25.xml", "marketdata_2008-06-26.xml", "marketdata_2008-06-27.xml", "marketdata_2008-06-30.xml",
			"marketdata_2008-07-01.xml", "marketdata_2008-07-02.xml", "marketdata_2008-09-09.xml", "marketdata_2008-09-10.xml", "marketdata_2008-10-10.xml", "marketdata_2008-10-13.xml", "marketdata_2008-10-14.xml", "marketdata_2008-10-15.xml");


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

		for(String fileName : files){

			String mdPath = "net/finmath/smartcontract/valuation/historicalMarketData/" +  fileName;
			final String md = new String(MarketDataCheckTest.class.getClassLoader().getResourceAsStream(mdPath).readAllBytes(), StandardCharsets.UTF_8);

			logger.info("checking file: {}", fileName);

			MarketDataList mdl = SDCXMLParser.unmarshalXml(md, MarketDataList.class);
			Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(fpml, Smartderivativecontract.class);

			MarketDataErrors errors = MarketDataCheck.checkMarketData(mdl, sdc);
			assertFalse(errors.hasErrors());
			assertEquals(0, errors.getMissingDataPoints().size());
		}
	}



}