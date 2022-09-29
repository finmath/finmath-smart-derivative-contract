package net.simulation;

import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.oracle.historical.ValuationOraclePlainSwapHistoricScenarios;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricalSimulationTest {

	@Test
	public void testHistoricalSimulation() {

		try {

			final LocalDate startDate = LocalDate.of(2007, 1, 1);
			final LocalDate maturity = LocalDate.of(2012, 1, 3);
			final String fileName = "timeseriesdatamap.json";
			final List<IRMarketDataSet> scenarioListRaw = IRMarketDataParser.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
			final List<IRMarketDataSet> scenarioList = scenarioListRaw.stream().map(scenario->scenario.getScaled(100)).collect(Collectors.toList());


			/*Generate Sample Product */
			final double notional = 1.0E7;
			final String MaturityKey = "5Y";
			final String forwardCurveKey = "forward-EUR-6M";
			final String discountCurveKey = "discount-EUR-OIS";
			final LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();
			/* Product starts at Par */
			final double fixRate = scenarioList.get(0).getDataPoints().stream()
					.filter(datapoint->datapoint.getCurveName().equals("Euribor6M") &&
							datapoint.getProductName().equals("Swap-Rate") &&
							datapoint.getMaturity().equals("5Y")).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();

			final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, MaturityKey, fixRate, true, forwardCurveKey, discountCurveKey);

			/* Start Valuation for filter historical scenarios */
			final ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap, notional, scenarioList);

			final List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario -> scenario.getDate()).collect(Collectors.toList());

			scenarioDates.stream().forEach(scenario -> {
				System.out.println("ScenarioDate: " + scenario + " Value of Swap : " + oracle.getValue(scenario, scenario));
			});
		} catch (final Exception e) {
			System.out.println(e);

		}

	}
}
