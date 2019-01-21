package net.finmath.smartcontract.oracle;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationContextImpl;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationParserDataPoints;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationResult;
import net.finmath.smartcontract.simulation.curvecalibration.Calibrator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;


/**
 * An oracle for swap valuation which generates values using externally provided historical market data scenarios.
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */

public class ValuationOraclePlainSwapHistoricScenarios implements ValuationOracle {

	private final CurrencyUnit currency = Monetary.getCurrency("EUR");
	private List<IRMarketDataScenario> scenarioList;
	private Swap product;
	private LocalDate productStartDate;
	private double notionalAmount;

	/**
	 * Oracle will be instantiated based on a Swap product an market data scenario list
	 *
	 * @param product The underlying swap product.
	 * @param notionalAmount The notional of the product.
	 * @param scenarioList The list of market data scenarios to be used for valuation.
	 */
	public ValuationOraclePlainSwapHistoricScenarios(Swap product, double notionalAmount, List<IRMarketDataScenario> scenarioList){
		this.notionalAmount = notionalAmount;
		this.product = product;
		this.productStartDate = ((SwapLeg) this.product.getLegPayer()).getSchedule().getReferenceDate();
		this.scenarioList = scenarioList;
	}

	@Override
	public Double getValue(LocalDateTime evaluationDate, LocalDateTime marketDataTime) {
		Optional<IRMarketDataScenario> optionalScenario = scenarioList.stream().filter(scenario->scenario.getDate().equals(marketDataTime)).findAny();
		if (optionalScenario.isPresent()) {
			IRMarketDataScenario scenario = optionalScenario.get();
			CalibrationParserDataPoints parser = new CalibrationParserDataPoints();
			Calibrator calibrator = new Calibrator();
			try {
				Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(scenario.getDataAsCalibrationDataProintStream(parser), new CalibrationContextImpl(marketDataTime.toLocalDate(), 1E-6));
				AnalyticModel calibratedModel = optionalCalibrationResult.get().getCalibratedModel();

				double evaluationTime = 0.0;	// Time relative to models reference date (which agrees with evaluationDate).
				double valueWithCurves = product.getValue(evaluationTime, calibratedModel) * notionalAmount;
				calibratedModel = null;
				return valueWithCurves;
			}
			catch(Exception e){
				return null;
			}
		}
		else{
			return null;
		}
	}

	@Override
	public MonetaryAmount getAmount(LocalDateTime evaluationTime, LocalDateTime marketDataTime) {
		return Money.of(getValue(evaluationTime, marketDataTime), currency);
	}
}
