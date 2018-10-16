package net.simulation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.oracle.ValuationOraclePlainSwapHistoricScenarios;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import net.finmath.smartcontract.simulation.scenariogeneration.IRScenarioGenerator;

public class HistoricalSimulationTest {

	@Test
	public void testHistoricalSimulation(){

		try {
			String startDate = "20070101";
			String endDate = "20120103";
			String fileName = "timeseriesdatamap.json";
			DateTimeFormatter providedDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
			final List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonFile(fileName,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(LocalDate.parse(startDate,providedDateFormat))).filter(S->S.getDate().toLocalDate().isBefore(LocalDate.parse(endDate,providedDateFormat))).collect(Collectors.toList());

			/*Generate Sample Product */
			double notional = 1.0E7;
			String MaturityKey = "5Y";
			String forwardCurveKey = "forward-EUR-6M";
			String discountCurveKey = "discount-EUR-OIS";
			LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();
			/* Product starts at Par */
			double fixRate = scenarioList.get(0).getCurveData("Euribor6M").getDataPointStreamForProductType("Swap-Rate").filter(e->e.getMaturity().equals(MaturityKey)).mapToDouble(e->e.getQuote()).findAny().getAsDouble() / 100.;
			Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate,MaturityKey,fixRate,true,forwardCurveKey,discountCurveKey);

			/* Start Valuation for filter historical scenarios */
			ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap,notional,scenarioList);

			List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario->scenario.getDate()).collect(Collectors.toList());

			scenarioDates.stream().forEach(scenario->{
				System.out.println("ScenarioDate: " + scenario + " Value of Swap : " + oracle.getValue(scenario));
			});
		}
		catch(Exception e){

		}

	}
}
