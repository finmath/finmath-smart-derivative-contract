package net.finmath.smartcontract.oracle;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import net.finmath.marketdata.model.AnalyticModelInterface;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationContextImpl;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationParserDataPoints;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationResult;
import net.finmath.smartcontract.simulation.curvecalibration.Calibrator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import net.finmath.time.FloatingpointDate;


/**
 * An oracle for swap valuation which generates values using externally provided historical market data scenarios.
 *
 * @author Peter Kohl-Landgraf
 */

public class ValuationOraclePlainSwapHistoricScenarios implements ValuationOracle {

	List<IRMarketDataScenario> scenarioList;
	Swap product;
	LocalDate productStartDate;
	double notionalAmount;

	/**
	 * Oracle will be instantiated based on a Swap product an market data scenario list
	 *
	 * @param product The underlying swap product.
	 * @param notionalAmount The notional of the product.
	 * @param scenarioList The list of market data scenarios to be used for valuation.
	 */
	public ValuationOraclePlainSwapHistoricScenarios(Swap product, double notionalAmount, List<IRMarketDataScenario> scenarioList){
		this.notionalAmount = notionalAmount;
		this.product=product;
		this.productStartDate =  ((SwapLeg) this.product.getLegPayer()).getSchedule().getReferenceDate();
		this.scenarioList=scenarioList;
	}

	/**
	 * Returns evaluation time based on product start date and scenario date
	 *
	 * @param scenarioDate The scenario date.
	 * @return The time as double relative to the product start date.
	 */
	private double  getEvaluationTimeFromScenarioDate(LocalDateTime scenarioDate){
		return FloatingpointDate.getFloatingPointDateFromDate(LocalDateTime.of(productStartDate, LocalTime.of(0,0)), scenarioDate);
	}

	@Override
	public Optional<Double> getValue(LocalDateTime evaluationTime){
		Optional<IRMarketDataScenario> optionalScenario = scenarioList.stream().filter(scenario->scenario.getDate().equals(evaluationTime)).findAny();
		if ( optionalScenario.isPresent()) {
			IRMarketDataScenario scenario = optionalScenario.get();
			CalibrationParserDataPoints parser = new CalibrationParserDataPoints();
			Calibrator calibrator = new Calibrator();
			try {
				Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(scenario.getDataAsCalibrationDataProintStream(parser), new CalibrationContextImpl(LocalDate.now(), 1E-6));
				AnalyticModelInterface calibratedModel = optionalCalibrationResult.get().getCalibratedModel();

				double valueWithCurves = product.getValue(getEvaluationTimeFromScenarioDate(evaluationTime), calibratedModel)*notionalAmount;
				calibratedModel = null;
				return Optional.of(valueWithCurves);
			}
			catch(Exception e){
				return Optional.empty();
			}
		}
		else{
			return  Optional.empty();
		}
	}
}
