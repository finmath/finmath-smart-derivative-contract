package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.*;
import net.finmath.optimizer.SolverException;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.apache.commons.lang3.ArrayUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An object calibrating models from a stream of calibration spec providers
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class Calibrator {

	public static final String DISCOUNT_EUR_OIS = "discount-EUR-OIS";

	private final List<CalibrationDataItem> fixings;
	private final LocalDateTime referenceDateTime;
	private CalibratedCurves calibratedCurves;

	public Calibrator(List<CalibrationDataItem> fixings, CalibrationContext ctx) {
		this.fixings = fixings;
		this.referenceDateTime = ctx.getReferenceDateTime();
	}

	protected CalibratedCurves getCalibratedCurves() {
		return calibratedCurves;
	}

	/**
	 * @param providers Stream providing calibration specs (calibration instruments)
	 * @param ctx       The context providing reference date and accuracy.
	 * @return If the calibration problem can be solved the optional wraps a AnalyticModel implementation with the calibrated model; if the problem is not solvable with respect to the given accuracy, the optional will be empty.
	 * @throws CloneNotSupportedException Thrown if model calibration fails.
	 * @see net.finmath.marketdata.model.AnalyticModel
	 */
	public Optional<CalibrationResult> calibrateModel(final Stream<CalibrationSpecProvider> providers, final CalibrationContext ctx) throws CloneNotSupportedException {

		final AnalyticModelFromCurvesAndVols model = new AnalyticModelFromCurvesAndVols(getCalibrationCurves(ctx));
		CalibratedCurves.CalibrationSpec[] specs =
				providers.map(c -> c.getCalibrationSpec(ctx)).toArray(CalibratedCurves.CalibrationSpec[]::new);

		try {
			calibratedCurves = new CalibratedCurves(specs, model, ctx.getAccuracy());
			return Optional.of(new CalibrationResult(calibratedCurves, specs));
		} catch (final SolverException e) {
			return Optional.empty();
		}
	}

	private Curve[] getCalibrationCurves(final CalibrationContext ctx) {
		return new Curve[]{getOisDiscountCurve(ctx), getOisForwardCurve(ctx), get1MForwardCurve(ctx), get3MForwardCurve(
				ctx), get6MForwardCurve(ctx)};
	}

	private DiscountCurveInterpolation getOisDiscountCurve(final CalibrationContext ctx) {
		ArrayList<Double> fixingValuesList = new ArrayList<>();
		ArrayList<Double> fixingTimesList = new ArrayList<>();
		ArrayList<Double> dfList = new ArrayList<>();
		ArrayList<Double> dfTimesList = new ArrayList<>();
		// We build the curve w.r.t to the reference Date AND Time, i.e. the referenceDateTime represents the time point 0.0 of the curve
		// Only the fixing dates of the historical fixings are relevant, i.e. we ignore the fixing time within a day and measure the previous fixings relative to the referenceDateTime in whole days (no day fractions)
		// The calibration items and the swap schedule use LocalDate exclusively, i.e. the curve points and schedule dates are also measured in whole days relative to the referenceDateTime / time 0.0
		fixings.stream().filter(x -> x.getCurveName().equals("ESTR"))
				.sorted(Comparator.comparing(CalibrationDataItem::getDate).reversed())
				.forEach(x -> {
					double time = FloatingpointDate.getFloatingPointDateFromDate(
							referenceDateTime,
							x.dateTime.with(referenceDateTime.toLocalTime()));
					if (time < 0) {
						fixingTimesList.add(time);
						fixingValuesList.add(365.0 * Math.log(1 + x.quote / 360.0));
						//conversion from 1-day ESTR (ACT/360) to zero-rate (ACT/ACT)
						//see https://quant.stackexchange.com/questions/73522/how-does-bloomberg-calculate-the-discount-rate-from-eur-estr-curve
					}
				});
		// Add initial zero entries for calculations
		fixingTimesList.add(0, 0.0);
		fixingValuesList.add(0, 0.0);
		// Add time zero discount factor
		dfTimesList.add(0, 0.0);
		dfList.add(0, 1.0);
		IntStream.range(1, fixingTimesList.size()).forEach(i -> {
			double df = dfList.get(i - 1) * Math.exp(-fixingValuesList.get(i) * (fixingTimesList.get(i) - fixingTimesList.get(i - 1)));
			dfList.add(df);
			dfTimesList.add(fixingTimesList.get(i));
		});
		boolean[] isParameters = ArrayUtils.toPrimitive(
				IntStream.range(0, dfTimesList.size()).boxed().map(x -> false).toList().toArray(Boolean[]::new));
		double[] dfTimes = dfTimesList.stream().mapToDouble(Double::doubleValue).toArray();
		double[] dfValues = dfList.stream().mapToDouble(Double::doubleValue).toArray();
		return DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(DISCOUNT_EUR_OIS,
				ctx.getReferenceDate(), dfTimes, dfValues, isParameters, CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.LOG_OF_VALUE);

	}

	private ForwardCurve getOisForwardCurve(final CalibrationContext ctx) {
		return new ForwardCurveFromDiscountCurve("forward-EUR-OIS",
				DISCOUNT_EUR_OIS,
				DISCOUNT_EUR_OIS,
				ctx.getReferenceDate(),
				"1D",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				365.0 / 360.0, 0.0); // includes ACT/360 to ACT/ACT conversion factor
	}

	private ForwardCurve get3MForwardCurve(final CalibrationContext ctx) {
		double[] fixingTimes = fixings.stream().filter(x -> x.getCurveName().equals("Euribor3M")).map(x -> x.dateTime)
				.map(x -> FloatingpointDate.getFloatingPointDateFromDate(referenceDateTime, x.with(referenceDateTime.toLocalTime())))
				.mapToDouble(Double::doubleValue).sorted().toArray();
		if (fixingTimes.length == 0) { //if there are no fixings return empty curve
			return new ForwardCurveInterpolation("forward-EUR-3M",
					ctx.getReferenceDate(),
					"3M",
					new BusinessdayCalendarExcludingTARGETHolidays(),
					BusinessdayCalendar.DateRollConvention.FOLLOWING,
					CurveInterpolation.InterpolationMethod.LINEAR,
					CurveInterpolation.ExtrapolationMethod.CONSTANT,
					CurveInterpolation.InterpolationEntity.VALUE,
					ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
					DISCOUNT_EUR_OIS);
		}
		double[] fixingValues = fixings.stream().filter(x -> x.getCurveName().equals("Euribor3M"))
				.sorted(Comparator.comparing(CalibrationDataItem::getDate)).map(CalibrationDataItem::getQuote)
				.mapToDouble(Double::doubleValue).toArray();
		ForwardCurve fixedPart = ForwardCurveInterpolation.createForwardCurveFromForwards("fixed-EUR-3M",
				ctx.getReferenceDate(),
				"3M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS, null, fixingTimes, fixingValues);
		ForwardCurve forwardPart = new ForwardCurveInterpolation("forward-EUR-3M",
				ctx.getReferenceDate(),
				"3M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS);
		// this is a dirty-ish fix: if the extrema of the fixed part lay exactly on the time specified by the data point,
		// some weird jumpiness occurs... TODO: maybe there's a smarter solution
		return new ForwardCurveWithFixings(forwardPart, fixedPart,
				Arrays.stream(fixingTimes).min().orElseThrow() - 1.0 / 365.0,
				Arrays.stream(fixingTimes).max().orElseThrow() + 1.0 / 365.0);
	}

	private ForwardCurve get6MForwardCurve(final CalibrationContext ctx) {
		double[] fixingTimes = fixings.stream().filter(x -> x.getCurveName().equals("Euribor6M")).map(x -> x.dateTime)
				.map(x -> FloatingpointDate.getFloatingPointDateFromDate(referenceDateTime, x.with(referenceDateTime.toLocalTime())))
				.mapToDouble(Double::doubleValue).sorted().toArray();
		if (fixingTimes.length == 0) { //if there are no fixings return empty curve
			return new ForwardCurveInterpolation("forward-EUR-6M",
					ctx.getReferenceDate(),
					"6M",
					new BusinessdayCalendarExcludingTARGETHolidays(),
					BusinessdayCalendar.DateRollConvention.FOLLOWING,
					CurveInterpolation.InterpolationMethod.LINEAR,
					CurveInterpolation.ExtrapolationMethod.CONSTANT,
					CurveInterpolation.InterpolationEntity.VALUE,
					ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
					DISCOUNT_EUR_OIS);
		}
		double[] fixingValues = fixings.stream().filter(x -> x.getCurveName().equals("Euribor6M"))
				.sorted(Comparator.comparing(CalibrationDataItem::getDate)).map(CalibrationDataItem::getQuote)
				.mapToDouble(Double::doubleValue).toArray();
		ForwardCurve fixedPart = ForwardCurveInterpolation.createForwardCurveFromForwards("fixed-EUR-6M",
				ctx.getReferenceDate(),
				"6M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS, null, fixingTimes, fixingValues);
		ForwardCurve forwardPart = new ForwardCurveInterpolation("forward-EUR-6M",
				ctx.getReferenceDate(),
				"6M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS);
		// this is a dirty-ish fix: if the extrema of the fixed part lay exactly on the time specified by the data point,
		// some weird jumpiness occurs... TODO: mayb there's a smarter solution
		return new ForwardCurveWithFixings(forwardPart, fixedPart,
				Arrays.stream(fixingTimes).min().orElseThrow() - 1.0 / 365.0,
				Arrays.stream(fixingTimes).max().orElseThrow() + 1.0 / 365.0);
	}

	private ForwardCurve get1MForwardCurve(final CalibrationContext ctx) {
		double[] fixingTimes = fixings.stream().filter(x -> x.getCurveName().equals("Euribor1M")).map(x -> x.dateTime)
				.map(x -> FloatingpointDate.getFloatingPointDateFromDate(referenceDateTime, x.with(referenceDateTime.toLocalTime())))
				.mapToDouble(Double::doubleValue).sorted().toArray();
		if (fixingTimes.length == 0) { //if there are no fixings return empty curve
			return new ForwardCurveInterpolation("forward-EUR-1M",
					ctx.getReferenceDate(),
					"1M",
					new BusinessdayCalendarExcludingTARGETHolidays(),
					BusinessdayCalendar.DateRollConvention.FOLLOWING,
					CurveInterpolation.InterpolationMethod.LINEAR,
					CurveInterpolation.ExtrapolationMethod.CONSTANT,
					CurveInterpolation.InterpolationEntity.VALUE,
					ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
					DISCOUNT_EUR_OIS);
		}
		double[] fixingValues = fixings.stream().filter(x -> x.getCurveName().equals("Euribor1M"))
				.sorted(Comparator.comparing(CalibrationDataItem::getDate)).map(CalibrationDataItem::getQuote)
				.mapToDouble(Double::doubleValue).toArray();
		ForwardCurve fixedPart = ForwardCurveInterpolation.createForwardCurveFromForwards("fixed-EUR-1M",
				ctx.getReferenceDate(),
				"1M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS, null, fixingTimes, fixingValues);
		ForwardCurve forwardPart = new ForwardCurveInterpolation("forward-EUR-1M",
				ctx.getReferenceDate(),
				"1M",
				new BusinessdayCalendarExcludingTARGETHolidays(),
				BusinessdayCalendar.DateRollConvention.FOLLOWING,
				CurveInterpolation.InterpolationMethod.LINEAR,
				CurveInterpolation.ExtrapolationMethod.CONSTANT,
				CurveInterpolation.InterpolationEntity.VALUE,
				ForwardCurveInterpolation.InterpolationEntityForward.FORWARD,
				DISCOUNT_EUR_OIS);
		// this is a dirty-ish fix: if the extrema of the fixed part lay exactly on the time specified by the data point,
		// some weird jumpiness occurs... TODO: mayb there's a smarter solution
		return new ForwardCurveWithFixings(forwardPart, fixedPart,
				Arrays.stream(fixingTimes).min().orElseThrow() - 1.0 / 365.0,
				Arrays.stream(fixingTimes).max().orElseThrow() + 1.0 / 365.0);
	}
}
