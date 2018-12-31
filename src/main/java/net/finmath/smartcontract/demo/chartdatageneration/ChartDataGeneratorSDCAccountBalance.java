package net.finmath.smartcontract.demo.chartdatageneration;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

import net.finmath.smartcontract.contract.SmartDerivativeContractSchedule;
import net.finmath.smartcontract.oracle.ValuationOraclePlainSwapHistoricScenarios;



/**
 *  This is a very simple dataset generator which generates ChartData (based on an ActionEvent) for a SDC Account Balance Simulation
 *
 * @author Peter Kohl-Landgraf
 *
 */
public class ChartDataGeneratorSDCAccountBalance implements ChartDataGenerator {

	private final Color colorPenalty = new Color(9, 128, 40);
	private final Color colorMarginBuffer = new Color(14, 199, 62);
	private final Color colorXSettlement = new Color(65, 127, 255);
	private final Color colorXPrecheck = new Color(255, 93, 86);

	private ValuationOraclePlainSwapHistoricScenarios oracle;
	private List<LocalDateTime> scenarioDates;
	private SmartDerivativeContractSchedule schedule;
	private double initialBalance;
	private processStates nextProcessState;
	private LocalDateTime initTime;

	enum processStates{
		initialisation,
		marginCheck,
		settlement,
		refill
	}

	public ChartDataGeneratorSDCAccountBalance(double initialBalance, ValuationOraclePlainSwapHistoricScenarios oracle, List<LocalDateTime> scenarioDates) {
		this.initialBalance=initialBalance;
		this.oracle = oracle;
		this.scenarioDates = scenarioDates;
		nextProcessState = processStates.initialisation;
	}


	public ChartData generatedChartData(final ActionEvent event) {


		double[][] data = new double[2][2];
		List<Color> accountColors = new ArrayList<>() ;
		String title = "Default";


		if (initTime==null){
			data = new double[][]{
					{initialBalance, initialBalance},
					{0, 0},
			};
			this.nextProcessState = processStates.marginCheck;
			this.initTime=LocalDateTime.now();

			ChartData chartData = new ChartData(DatasetUtilities.createCategoryDataset("Buffer ", "Bank", data));
			title = "Initialisation";
			accountColors.add(colorMarginBuffer);
			accountColors.add(colorMarginBuffer);
			chartData.addProperty(ChartData.propertyKey.chartTitle,title);
			chartData = chartData.addProperty(ChartData.propertyKey.colorListStackedBar, accountColors);
			nextProcessState = processStates.marginCheck;
			return chartData;
		}
		LocalDateTime eventTime =
				LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getWhen()), ZoneId.systemDefault());

		if (nextProcessState == processStates.marginCheck) /* Determine Booking Amount */ {
			//int index = (int) initTime.until(eventTime,SECONDS);
			System.out.println("Current Time Index  = " + eventTime.toString() + ": Margin Check");
			LocalDateTime scenarioLast = this.scenarioDates.get(0);
			LocalDateTime scenarioActual = this.scenarioDates.get(1);
			double valueLast = oracle.getValue(scenarioLast, scenarioLast);
			double valueActual = oracle.getValue(scenarioActual, scenarioActual);
			double value = valueActual - valueLast;
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data.length; j++) {
					//data[i][j] = data[i][j] * rand;
					if (value < 0) {
						data[0][0] = initialBalance + value;
						data[0][1] = initialBalance;
						data[1][0] = -value;
					}
					if (value > 0) {
						data[0][0] = initialBalance;
						data[0][1] = initialBalance - value;
						data[1][1] = value;
					}
				}
			}
			title = "1. Margin Check of Booking Amount";
			accountColors.add(colorMarginBuffer);
			accountColors.add(colorXPrecheck);
			nextProcessState = processStates.settlement;
		} else if (nextProcessState == processStates.settlement) { /*BOOKING*/
			System.out.println("Current Time = " + eventTime.toString() + ": Settlement");
			LocalDateTime scenarioLast = this.scenarioDates.get(0);
			LocalDateTime scenarioActual = this.scenarioDates.get(1);
			double valueLast = oracle.getValue(scenarioLast, scenarioLast);
			double valueActual = oracle.getValue(scenarioActual, scenarioActual);
			double value = valueActual - valueLast;
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data.length; j++) {
					if (value < 0) {
						data[0][0] = initialBalance + value;
						data[1][0] = 0;
						data[0][1] = initialBalance;
						data[1][1] = -value;
					}
					if (value > 0) {
						data[0][0] = initialBalance;
						data[1][0] = value;
						data[0][1] = initialBalance - value;
						data[1][1] = 0;
					}
				}
			}
			title = "2. Settlement of Amount X";
			accountColors.add(colorMarginBuffer);
			accountColors.add(colorXSettlement);
			nextProcessState = processStates.refill;

			//System.out.println("Current Time in Milliseconds = Reset");
		} else if (nextProcessState == processStates.refill) /*REfill*/ {
			System.out.println("Current Time = " + eventTime.toString() + ": Reset");
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data.length; j++) {
					data[0][0] = initialBalance;
					data[0][1] = initialBalance;
					data[1][0] = 0;
					data[1][1] = 0;
				}
			}
			title = "3. Refill or Withdraw of Excess Margin";
			accountColors.add(colorMarginBuffer);
			accountColors.add(colorMarginBuffer);
			scenarioDates.remove(0);
			nextProcessState = processStates.marginCheck;
		} else {
		}
		final CategoryDataset dataset =  DatasetUtilities.createCategoryDataset("Buffer ", "Bank", data);
		ChartData chartData = new ChartData(dataset);
		chartData.addProperty(ChartData.propertyKey.chartTitle,title);
		chartData = chartData.addProperty(ChartData.propertyKey.colorListStackedBar, accountColors);
		return chartData;
	}




}

