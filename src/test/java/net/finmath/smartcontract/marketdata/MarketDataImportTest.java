package net.finmath.smartcontract.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.service.Application;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationDatapoint;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;


public class MarketDataImportTest {

	@Test
	void testImport() throws Exception {
		final String jsonStr = Files.readString(Path.of(new File(Application.class.getClassLoader().getResource("md_testset1.json").getPath()).toURI()), StandardCharsets.UTF_8);
		List<IRMarketDataSet> scenarioList = IRMarketDataParser.getScenariosFromJsonString(jsonStr);
		Assertions.assertEquals(1, scenarioList.size());
	}
}
