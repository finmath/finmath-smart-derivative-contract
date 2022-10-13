package net.finmath.smartcontract.demo.legacy;

import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.demo.legacy.chartdatageneration.ChartDataGeneratorMarketValue;
import net.finmath.smartcontract.demo.legacy.chartdatageneration.ChartDataGeneratorSDCAccountBalance;
import net.finmath.smartcontract.demo.legacy.plotgeneration.PlotGenerator;
import net.finmath.smartcontract.demo.legacy.plotgeneration.StackedBarchartGenerator;
import net.finmath.smartcontract.demo.legacy.plotgeneration.TimeSeriesChartGenerator;
import net.finmath.smartcontract.oracle.interestrates.ValuationOraclePlainSwap;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Demo Launcher, generating historical Scenarios, building a DataGenerator, and starting Visualiser
 *
 * @author Peter Kohl-Landgraf
 */
public class DemoLauncher {

	public static void main(final String[] args) throws Exception {

		final LocalDate startDate = LocalDate.of(2007, 1, 1);
		final LocalDate maturity = LocalDate.of(2012, 1, 3);
		final String fileName = "timeseriesdatamap.json";
		final List<IRMarketDataSet> scenarioListRaw = IRMarketDataParser.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		final List<IRMarketDataSet> scenarioList = scenarioListRaw.stream().map(scenario->scenario.getScaled(100)).collect(Collectors.toList());


		final double notional = 1.0E7;
		final String maturityKey = "5Y";
		final String forwardCurveKey = "forward-EUR-6M";
		final String discountCurveKey = "discount-EUR-OIS";
		final LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();

		final double fixRate = scenarioList.get(0).getDataPoints().stream()
				.filter(datapoint->datapoint.getCurveName().equals("Euribor6M") &&
						datapoint.getProductName().equals("Swap-Rate") &&
						datapoint.getMaturity().equals("5Y")).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();

		final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, maturityKey, fixRate, true, forwardCurveKey, discountCurveKey);

		final ValuationOraclePlainSwap oracle = new ValuationOraclePlainSwap(swap, notional, scenarioList);

		final List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario -> scenario.getDate()).collect(Collectors.toList());

		final ChartDataGeneratorSDCAccountBalance chartDataGeneratorSDCAccountBalance = new ChartDataGeneratorSDCAccountBalance(30000, oracle, scenarioDates);
		//ChartDataGeneratorSDCAccountBalance chartDataGeneratorSDCAccountBalance2 = new ChartDataGeneratorSDCAccountBalance(30000,oracle,scenarioDates);
		final ChartDataGeneratorMarketValue marketValues = new ChartDataGeneratorMarketValue(oracle, scenarioDates);
		final StackedBarchartGenerator barchartGenerator = new StackedBarchartGenerator(chartDataGeneratorSDCAccountBalance);
		//StackedBarchartGenerator barchartGenerator2 = new StackedBarchartGenerator(chartDataGeneratorSDCAccountBalance2);
		final TimeSeriesChartGenerator timeSeriesChartGenerator = new TimeSeriesChartGenerator(marketValues);
		final List<PlotGenerator> generatorList = new ArrayList<>();
		generatorList.add(timeSeriesChartGenerator);
		generatorList.add(barchartGenerator);
		//generatorList.add(barchartGenerator2);

		final Visualiser visualiser = new Visualiser("Smart Contract Simulation", generatorList);
		visualiser.pack();
//		RefineryUtilities.centerFrameOnScreen(visualiser);
		visualiser.setVisible(true);
	}
}
