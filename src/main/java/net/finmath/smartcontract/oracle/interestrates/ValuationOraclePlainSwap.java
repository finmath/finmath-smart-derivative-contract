package net.finmath.smartcontract.oracle.interestrates;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.ForwardCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurveWithFixings;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.smartcontract.marketdata.curvecalibration.*;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.oracle.ValuationOracle;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.daycount.DayCountConvention;
import net.finmath.time.daycount.DayCountConventionFactory;
import net.finmath.time.daycount.DayCountConvention_30E_360;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
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
			final LocalDate referenceDate = marketDataTime.toLocalDate();

			final CalibrationParserDataItems parser = new CalibrationParserDataItems();
			final Calibrator calibrator = new Calibrator();


			try {

				final Stream<CalibrationSpecProvider> allCalibrationItems = scenario.getDataAsCalibrationDataPointStream(parser);


				final Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(referenceDate, 1E-9));
				AnalyticModel calibratedModel = optionalCalibrationResult.get().getCalibratedModel();

				/* Check the product */
				SwapLeg legReceiver = (SwapLeg) product.getLegReceiver();
				SwapLeg legPayer = (SwapLeg) product.getLegPayer();
				SwapLeg floatingLeg = !legPayer.getForwardCurveName().equals("") ? legPayer : legReceiver;
				String forwardCurveID = floatingLeg.getForwardCurveName();
				System.out.println("curveid: "+ forwardCurveID);
				Schedule schedule = floatingLeg.getSchedule();


				Set<CalibrationDataItem> pastFixings = scenario.getFixingDataItems();

				// @Todo what if we have no past fixing provided
				// @Todo what when we are exactly on the fixing date but before 11:00 am.
				String discountCurveID = floatingLeg.getDiscountCurveName();
				ForwardCurveInterpolation fixedCurve = this.getCurvePastFixings("fixedCurve",referenceDate,calibratedModel,discountCurveID,pastFixings);//ForwardCurveInterpolation.createForwardCurveFromForwards("pastFixingCurve", pastFixingTimeArray, pastFixingArray, paymentOffset);
				Curve forwardCurveWithFixings = new ForwardCurveWithFixings(calibratedModel.getForwardCurve(forwardCurveID), fixedCurve, schedule.getFixing(0), 0.0);
				Curve[] finalCurves = {calibratedModel.getDiscountCurve(floatingLeg.getDiscountCurveName()), calibratedModel.getForwardCurve(forwardCurveID), forwardCurveWithFixings};
				calibratedModel = new AnalyticModelFromCurvesAndVols(referenceDate, finalCurves);

				double fixedRate = forwardCurveWithFixings.getValue(-0.12);

				final double evaluationTime = 0.0;    // Time relative to models reference date (which agrees with evaluationDate).
				final double valueWithCurves = product.getValue(evaluationTime, calibratedModel) * notionalAmount;


				return rounding.applyAsDouble(valueWithCurves);
			} catch (final Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		} else {
			return null;
		}
	}


	private ForwardCurveInterpolation getCurvePastFixings(final String curveID, LocalDate referenceDate, AnalyticModel model, String discountCurveName, final Set<CalibrationDataItem> pastFixings){
		Map<Double, Double> fixingMap = new LinkedHashMap<>();
		pastFixings.stream().forEach(item->fixingMap.put(FloatingpointDate.getFloatingPointDateFromDate(referenceDate, item.getDate()),item.getQuote()));
		double[] pastFixingTimes = fixingMap.keySet().stream().mapToDouble(time->time).toArray();
		double[] pastFixingsValues = Arrays.stream(pastFixingTimes).map(time->fixingMap.get(time)).toArray();
		ForwardCurveInterpolation.InterpolationEntityForward interpolationEntityForward = ForwardCurveInterpolation.InterpolationEntityForward.FORWARD;
		ForwardCurveInterpolation fixedCurve = ForwardCurveInterpolation.createForwardCurveFromForwards(curveID,referenceDate,"offsetcode",interpolationEntityForward,discountCurveName,model,pastFixingTimes,pastFixingsValues);
		return fixedCurve;
	}
}

				/*if (schedule.getFixing(0)<0) {  /* In case we have a past fixing, add a fixed forward curve to calibration model
					Set<CalibrationDataItem> pastFixings = scenario.getFixingDataItems();
					LocalDate pastFixingDate = schedule.getPeriods().get(0).getFixing();
					double period = FloatingpointDate.getFloatingPointDateFromDate(evaluationDate.toLocalDate(), pastFixingDate);
					double[] pastFixingTimeArray = {schedule.getFixing(0)};
					double[] pastFixingArray = new double[1];
					try {
						pastFixingArray[0] = pastFixings.stream().filter(fixing -> fixing.getDate().equals(pastFixingDate)).findAny().orElseThrow().getQuote();
					} catch (Exception e) {
						pastFixingArray[0] = 0.0;
					}
					double paymentOffset = schedule.getPayment(0);
				//} */