package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

import static net.finmath.smartcontract.valuation.marketdata.curvecalibration.Calibrator.DISCOUNT_EUR_OIS;

/**
 * A calibration spec provider for Overnight (O/N) rates, e.g. €STR or SOFR
 * This calibration spec should only be used for the overnight rates at which banks borrow and lend funds to each other for a single day
 * The overnight rate is posted "ex post" by the central bank based on transactions observed on the previous day
 * It is not an Overnight-index-swap or similar "trade-able" instrument
 * Theoretically it is a Deposit instrument, but since the rate published today is the rate observed yesterday,
 * the use of this rate as a 1-day calibration item serves only as a proxy of the overnight discount rate from today to tomorrow.
 *
 * @author Raphael Prandtl
 */
public class CalibrationSpecProviderOvernightRate implements CalibrationSpecProvider {
	private final String maturityLabel;
	private final String frequency;
	private final double overnightRate;

	public CalibrationSpecProviderOvernightRate(final String maturityLabel, final String frequency, final double overnightRate) {
		this.maturityLabel = maturityLabel;
		this.frequency = frequency;
		this.overnightRate = overnightRate;
	}

	@Override
	public CalibratedCurves.CalibrationSpec getCalibrationSpec(final CalibrationContext ctx) {
		final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 0, "0D", maturityLabel, frequency, "act/360", "first", "follow", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final double calibrationTime = scheduleInterfaceRec.getPayment(scheduleInterfaceRec.getNumberOfPeriods() - 1);

		return new CalibratedCurves.CalibrationSpec(String.format("EUR-OIS-%1$s", maturityLabel), "Deposit", scheduleInterfaceRec, "forward-EUR-OIS", overnightRate, DISCOUNT_EUR_OIS, null, "", 0.0, DISCOUNT_EUR_OIS, DISCOUNT_EUR_OIS, calibrationTime);
	}
}
