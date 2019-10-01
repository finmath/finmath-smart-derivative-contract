package net.finmath.smartcontract.simulation.curvecalibration;

import java.util.Optional;
import java.util.stream.Stream;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveInterpolation;
import net.finmath.marketdata.model.curves.DiscountCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveFromDiscountCurve;
import net.finmath.marketdata.model.curves.ForwardCurveInterpolation;
import net.finmath.optimizer.SolverException;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * An object calibrating models from a stream of calibration spec providers
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class Calibrator {

	public static final String DISCOUNT_EUR_OIS = "discount-EUR-OIS";

	/**
	 * @param providers Stream providing calibration specs (calibration instruments)
	 * @param ctx The context providing reference date and accuracy.
	 * @return If the calibration problem can be solved the optional wraps a AnalyticModel implementation with the calibrated model; if the problem is not solvable with respect to the given accuracy, the optional will be empty.
	 * @throws CloneNotSupportedException Thrown if model calibration fails.
	 * @see net.finmath.marketdata.model.AnalyticModel
	 */
	public Optional<CalibrationResult> calibrateModel(final Stream<CalibrationSpecProvider> providers, final CalibrationContext ctx) throws CloneNotSupportedException {
		final AnalyticModelFromCurvesAndVols model = new AnalyticModelFromCurvesAndVols(getCalibrationCurves(ctx));
		final CalibratedCurves.CalibrationSpec[] specs = providers.map(c -> c.getCalibrationSpec(ctx)).toArray(CalibratedCurves.CalibrationSpec[]::new);

		try {
			return Optional.of(new CalibrationResult(new CalibratedCurves(specs, model, ctx.getAccuracy()), specs));
		} catch (final SolverException e) {
			return Optional.empty();
		}
	}

	private Curve[] getCalibrationCurves(final CalibrationContext ctx)
	{
		return new Curve[] { getOisDiscountCurve(ctx), getOisForwardCurve(ctx), get1MForwardCurve(ctx), get3MForwardCurve(ctx), get6MForwardCurve(ctx) };
	}

	private DiscountCurveInterpolation getOisDiscountCurve(final CalibrationContext ctx) {
		return DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(DISCOUNT_EUR_OIS, ctx.getReferenceDate(), new double[]{0.0}, new double[]{1.0}, new boolean[]{false}, CurveInterpolation.InterpolationMethod.LINEAR, CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.LOG_OF_VALUE);
	}

	private ForwardCurve getOisForwardCurve(final CalibrationContext ctx) {
		return new ForwardCurveFromDiscountCurve("forward-EUR-OIS", DISCOUNT_EUR_OIS, ctx.getReferenceDate(), "3M");
	}

	private ForwardCurve get3MForwardCurve(final CalibrationContext ctx) {
		return new ForwardCurveInterpolation("forward-EUR-3M", ctx.getReferenceDate(), "3M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveInterpolation.InterpolationMethod.LINEAR, CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, DISCOUNT_EUR_OIS);
	}

	private ForwardCurve get6MForwardCurve(final CalibrationContext ctx) {
		return new ForwardCurveInterpolation("forward-EUR-6M", ctx.getReferenceDate(), "6M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveInterpolation.InterpolationMethod.LINEAR, CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, DISCOUNT_EUR_OIS);
	}

	private ForwardCurve get1MForwardCurve(final CalibrationContext ctx) {
		return new ForwardCurveInterpolation("forward-EUR-1M", ctx.getReferenceDate(), "1M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveInterpolation.InterpolationMethod.LINEAR, CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, DISCOUNT_EUR_OIS);
	}
}
