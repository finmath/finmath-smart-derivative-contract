package net.simulation;

import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.valuation.oracle.interestrates.ValuationOraclePlainSwap;
import net.finmath.smartcontract.product.IRSwapGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class HistoricalSimulationTest {

	@Test
	void testHistoricalSimulation() {

		try {

			final LocalDate startDate = LocalDate.of(2007, 1, 1);
			final LocalDate maturity = LocalDate.of(2012, 1, 3);
			final String fileName = "timeseriesdatamap.json";
			final List<CalibrationDataset> scenarioListRaw = CalibrationParserDataItems.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
			final List<CalibrationDataset> scenarioList = scenarioListRaw.stream().map(scenario -> scenario.getScaled(100)).collect(Collectors.toList());


			/*Generate Sample Product */
			final double notional = 1.0E7;
			final String MaturityKey = "5Y";
			final String forwardCurveKey = "forward-EUR-6M";
			final String discountCurveKey = "discount-EUR-OIS";
			final LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();
			/* Product starts at Par */
			final double fixRate = scenarioList.get(0).getDataPoints().stream()
					.filter(datapoint -> datapoint.getSpec().getCurveName().equals("Euribor6M") &&
							datapoint.getSpec().getProductName().equals("Swap-Rate") &&
							datapoint.getSpec().getMaturity().equals("5Y")).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();

			final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, MaturityKey, notional, fixRate, true, forwardCurveKey, discountCurveKey);

			/* Start Valuation for filter historical scenarios */
			final ValuationOraclePlainSwap oracle = new ValuationOraclePlainSwap(Map.of("value",swap), scenarioList);

			final List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario -> scenario.getDate()).collect(Collectors.toList());

			scenarioDates.stream().forEach(scenario -> {
				System.out.println("ScenarioDate: " + scenario + " Value of Swap : " + oracle.getValue(scenario, scenario));
			});
		} catch (final Exception e) {
			System.out.println(e);

		}

	}
}
