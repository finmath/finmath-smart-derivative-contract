package net.finmath.smartcontract.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.ui.RefineryUtilities;

import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.demo.chartdatageneration.ChartDataGeneratorMarketValue;
import net.finmath.smartcontract.demo.chartdatageneration.ChartDataGeneratorSDCAccountBalance;
import net.finmath.smartcontract.demo.plotgeneration.PlotGenerator;
import net.finmath.smartcontract.demo.plotgeneration.StackedBarchartGenerator;
import net.finmath.smartcontract.demo.plotgeneration.TimeSeriesChartGenerator;
import net.finmath.smartcontract.oracle.ValuationOraclePlainSwapHistoricScenarios;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import net.finmath.smartcontract.simulation.scenariogeneration.IRScenarioGenerator;


/**
 *  Demo Launcher, generating historical Scenarios, building a DataGenerator, and starting Visualiser
 *
 * @author Peter Kohl-Landgraf
 */
public class DemoLauncher {

	public static void main(final String[] args) throws Exception {


		//

		LocalDate startDate = LocalDate.of(2007, 1, 1);
		LocalDate maturity = LocalDate.of(2012, 1, 3);
		String fileName = "timeseriesdatamap.json";
		DateTimeFormatter providedDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		final List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonFile(fileName,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());

		double notional = 1.0E7;
		String MaturityKey = "5Y";
		String forwardCurveKey = "forward-EUR-6M";
		String discountCurveKey = "discount-EUR-OIS";
		LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();

		double fixRate = scenarioList.get(0).getCurveData("Euribor6M").getDataPointStreamForProductType("Swap-Rate").filter(e->e.getMaturity().equals(MaturityKey)).mapToDouble(e->e.getQuote()).findAny().getAsDouble() / 100.;
		Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate,MaturityKey,fixRate,true,forwardCurveKey,discountCurveKey);

		ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap,notional,scenarioList);

		List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario->scenario.getDate()).collect(Collectors.toList());

		ChartDataGeneratorSDCAccountBalance chartDataGeneratorSDCAccountBalance = new ChartDataGeneratorSDCAccountBalance(30000,oracle,scenarioDates);
		//ChartDataGeneratorSDCAccountBalance chartDataGeneratorSDCAccountBalance2 = new ChartDataGeneratorSDCAccountBalance(30000,oracle,scenarioDates);
		ChartDataGeneratorMarketValue marketValues = new ChartDataGeneratorMarketValue(oracle,scenarioDates);
		StackedBarchartGenerator barchartGenerator = new StackedBarchartGenerator(chartDataGeneratorSDCAccountBalance);
		//StackedBarchartGenerator barchartGenerator2 = new StackedBarchartGenerator(chartDataGeneratorSDCAccountBalance2);
		TimeSeriesChartGenerator timeSeriesChartGenerator = new TimeSeriesChartGenerator(marketValues);
		List<PlotGenerator> generatorList = new ArrayList<>();
		generatorList.add(timeSeriesChartGenerator);
		generatorList.add(barchartGenerator);
		//generatorList.add(barchartGenerator2);

		Visualiser visualiser = new Visualiser("Smart Contract Simulation", generatorList);
		visualiser.pack();
		RefineryUtilities.centerFrameOnScreen(visualiser);
		visualiser.setVisible(true);
	}
}
