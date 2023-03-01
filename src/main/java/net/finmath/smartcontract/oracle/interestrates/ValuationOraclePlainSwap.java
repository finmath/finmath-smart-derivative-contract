package net.finmath.smartcontract.oracle.interestrates;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.smartcontract.marketdata.curvecalibration.*;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.oracle.ValuationOracle;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

/**
 * An oracle for swap valuation which generates values using externally provided historical market data scenarios.
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */

public class ValuationOraclePlainSwap implements ValuationOracle {

	private final CurrencyUnit currency = Monetary.getCurrency("EUR");
	private final List<CalibrationDataset> scenarioList;
	private final Swap product;
	private final LocalDate productStartDate;
	private final double notionalAmount;
	private final DoubleUnaryOperator rounding;

	/**
	 * Oracle will be instantiated based on a Swap product an market data scenario list
	 *
	 * @param product        The underlying swap product.
	 * @param notionalAmount The notional of the product.
	 * @param scenarioList   The list of market data scenarios to be used for valuation.
	 * @param rounding		An operator implementing the rounding.
	 */
	public ValuationOraclePlainSwap(final Swap product, final double notionalAmount, final List<CalibrationDataset> scenarioList, DoubleUnaryOperator rounding) {
		this.notionalAmount = notionalAmount;
		this.product = product;
		this.productStartDate = ((SwapLeg) this.product.getLegPayer()).getSchedule().getReferenceDate();
		this.scenarioList = scenarioList;
		this.rounding = rounding;
	}

	/**
	 * Oracle will be instantiated based on a Swap product an market data scenario list
	 *
	 * @param product        The underlying swap product.
	 * @param notionalAmount The notional of the product.
	 * @param scenarioList   The list of market data scenarios to be used for valuation.
	 */
	public ValuationOraclePlainSwap(final Swap product, final double notionalAmount, final List<CalibrationDataset> scenarioList) {
		this(product, notionalAmount, scenarioList, x -> Math.round(x*100)/100.0);
	}

	@Override
	public MonetaryAmount getAmount(final LocalDateTime evaluationTime, final LocalDateTime marketDataTime) {
		return Money.of(getValue(evaluationTime, marketDataTime), currency);
	}

	@Override
	public Double getValue(final LocalDateTime evaluationDate, final LocalDateTime marketDataTime) {
		final Optional<CalibrationDataset> optionalScenario = scenarioList.stream().filter(scenario -> scenario.getDate().equals(marketDataTime)).findAny();
		if (optionalScenario.isPresent()) {
			final CalibrationDataset scenario = optionalScenario.get();

			/** @// TODO: 2/21/2023 - IRMarketDataSet to provide the fixed part of a curve
			 *
			 *
			 * * double[] pastFixingTime = {scheduleFloat.getFixing(0)};
			 * 				 * double paymentOffset = scheduleFloat.getPayment(0);
			 * 				 * fixedCurve = ForwardCurveInterpolation.createForwardCurveFromForwards("fixed",pastFixingTime,pastFixingArray,paymentOffset);
			 */

			final CalibrationParserDataItems parser = new CalibrationParserDataItems();
			final Calibrator calibrator = new Calibrator();


			try {

				final Stream<CalibrationSpecProvider> allCalibrationItems = scenario.getDataAsCalibrationDataPointStream(parser);

				/*@Todo - should be part of IRMarketDataSet to provide relevant map
				1. how to convert fixing time into date
				2. what about when we are exactly on the fixing date
				3. what when we are exactly on the fixing date but before 11:00 am.
				 */
				Map<String,Double> pastFixingMap  = null;

				final Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(marketDataTime.toLocalDate(), 1E-6));
				AnalyticModel calibratedModel = optionalCalibrationResult.get().getCalibratedModel();

				/*InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) product.getLegReceiver();
				InterestRateSwapLegProductDescriptor legPayer = (InterestRateSwapLegProductDescriptor) product.getLegPayer();

				InterestRateSwapLegProductDescriptor floatingLeg = !legPayer.getForwardCurveName().equals("") ? legPayer : legReceiver;
				String forwardCurveID = floatingLeg.getForwardCurveName();
				Schedule schedule = floatingLeg.getLegScheduleDescriptor().getSchedule(evaluationDate.toLocalDate());
				double[] pastFixingTime = {schedule.getFixing(0)};
				double[] pastFixingArray = {0.0};
				double paymentOffset = schedule.getPayment(0);

				 ForwardCurveInterpolation fixedCurve = ForwardCurveInterpolation.createForwardCurveFromForwards("fixed",pastFixingTime,pastFixingArray,paymentOffset);
				 Curve forwardCurveWithFixings = new ForwardCurveWithFixings(calibratedModel.getForwardCurve(forwardCurveID), fixedCurve, schedule.getFixing(0), 0.0);
				 Curve[] finalCurves = {calibratedModel.getDiscountCurve(floatingLeg.getDiscountCurveName()), calibratedModel.getForwardCurve(forwardCurveID), forwardCurveWithFixings};
				 calibratedModel = new AnalyticModelFromCurvesAndVols(evaluationDate.toLocalDate(), finalCurves);*/

				final double evaluationTime = 0.0;    // Time relative to models reference date (which agrees with evaluationDate).
				final double valueWithCurves = product.getValue(evaluationTime, calibratedModel) * notionalAmount;


				return rounding.applyAsDouble(valueWithCurves);
			} catch (final Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
