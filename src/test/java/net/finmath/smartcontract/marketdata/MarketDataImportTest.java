package net.finmath.smartcontract.marketdata;

import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class MarketDataImportTest {

	@Test
	void testImport() throws Exception {
		final String jsonStr = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		List<IRMarketDataSet> scenarioList = IRMarketDataParser.getScenariosFromJsonString(jsonStr);
		Assertions.assertEquals(1, scenarioList.size());
	}
}
