package net.finmath.smartcontract.marketdata;

import net.finmath.smartcontract.service.Application;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertNotNull;


public class MarketDataImportTest {


	@Test
	void testImport() throws Exception {
		String jsonStr = FileUtils.readFileToString(new File(new File(Application.class.getClassLoader().getResource("md_testset1.json").getPath()).toURI()), StandardCharsets.UTF_8);
		List<IRMarketDataSet> scenarioList = IRMarketDataParser.getScenariosFromJsonString(jsonStr);
		assert(scenarioList.size()==1);

	}

}
