package net.finmath.smartcontract.simulation.curvecalibration;

import java.util.Optional;
import java.util.stream.Stream;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModelFromCuvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveFromInterpolationPoints;
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
	public Optional<CalibrationResult> calibrateModel(Stream<CalibrationSpecProvider> providers, CalibrationContext ctx) throws CloneNotSupportedException {
		AnalyticModelFromCuvesAndVols model = new AnalyticModelFromCuvesAndVols(getCalibrationCurves(ctx));
		CalibratedCurves.CalibrationSpec[] specs = providers.map(c -> c.getCalibrationSpec(ctx)).toArray(CalibratedCurves.CalibrationSpec[]::new);

		try {
			return Optional.of(new CalibrationResult(new CalibratedCurves(specs, model, ctx.getAccuracy()), specs));
		} catch (SolverException e) {
			return Optional.empty();
		}
	}

	private Curve[] getCalibrationCurves(CalibrationContext ctx)
	{
		return new Curve[] { getOisDiscountCurve(ctx), getOisForwardCurve(ctx), get1MForwardCurve(ctx), get3MForwardCurve(ctx), get6MForwardCurve(ctx) };
	}

	private DiscountCurveInterpolation getOisDiscountCurve(CalibrationContext ctx) {
		return DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(DISCOUNT_EUR_OIS, ctx.getReferenceDate(), new double[]{0.0}, new double[]{1.0}, new boolean[]{false}, CurveFromInterpolationPoints.InterpolationMethod.LINEAR, CurveFromInterpolationPoints.ExtrapolationMethod.CONSTANT, CurveFromInterpolationPoints.InterpolationEntity.LOG_OF_VALUE);
	}

	private ForwardCurve getOisForwardCurve(CalibrationContext ctx) {
		return new ForwardCurveFromDiscountCurve("forward-EUR-OIS", DISCOUNT_EUR_OIS, ctx.getReferenceDate(), "3M");
	}

	private ForwardCurve get3MForwardCurve(CalibrationContext ctx) {
		return new ForwardCurveInterpolation("forward-EUR-3M", ctx.getReferenceDate(), "3M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveFromInterpolationPoints.InterpolationMethod.LINEAR, CurveFromInterpolationPoints.ExtrapolationMethod.CONSTANT, CurveFromInterpolationPoints.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, DISCOUNT_EUR_OIS);
	}

	private ForwardCurve get6MForwardCurve(CalibrationContext ctx) {
		return new ForwardCurveInterpolation("forward-EUR-6M", ctx.getReferenceDate(), "6M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveFromInterpolationPoints.InterpolationMethod.LINEAR, CurveFromInterpolationPoints.ExtrapolationMethod.CONSTANT, CurveFromInterpolationPoints.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, DISCOUNT_EUR_OIS);
	}

	private ForwardCurve get1MForwardCurve(CalibrationContext ctx) {
		return new ForwardCurveInterpolation("forward-EUR-1M", ctx.getReferenceDate(), "1M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveFromInterpolationPoints.InterpolationMethod.LINEAR, CurveFromInterpolationPoints.ExtrapolationMethod.CONSTANT, CurveFromInterpolationPoints.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, DISCOUNT_EUR_OIS);
	}
}
