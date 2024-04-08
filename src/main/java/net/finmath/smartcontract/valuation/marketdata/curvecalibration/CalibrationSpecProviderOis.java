package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * A calibration spec provider for OIS swaps.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationSpecProviderOis implements CalibrationSpecProvider {
	private final String maturityLabel;
	private final String frequency;
	private final double swapRate;

	public CalibrationSpecProviderOis(final String maturityLabel, final String frequency, final double swapRate) {
		this.maturityLabel = maturityLabel;
		this.frequency = frequency;
		this.swapRate = swapRate;
	}

	@Override
	public CalibratedCurves.CalibrationSpec getCalibrationSpec(final CalibrationContext ctx) {
		final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, frequency, "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 1);
		final Schedule scheduleInterfacePay = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, frequency, "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 1);
		final double calibrationTime = scheduleInterfaceRec.getPayment(scheduleInterfaceRec.getNumberOfPeriods() - 1);

		return new CalibratedCurves.CalibrationSpec(String.format("EUR-OIS-%1$s", maturityLabel), "Swap", scheduleInterfaceRec, "forward-EUR-OIS", 0.0, Calibrator.DISCOUNT_EUR_OIS, scheduleInterfacePay, "", swapRate, Calibrator.DISCOUNT_EUR_OIS, Calibrator.DISCOUNT_EUR_OIS, calibrationTime);
	}
}
