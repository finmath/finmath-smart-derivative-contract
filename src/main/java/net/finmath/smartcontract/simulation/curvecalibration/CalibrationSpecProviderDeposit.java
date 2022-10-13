package net.finmath.smartcontract.simulation.curvecalibration;


import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * A calibration spec provider for deposits.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationSpecProviderDeposit implements CalibrationSpecProvider {
	private final String tenorLabel;
	private final String maturityLabel;
	private final double depositRate;

	/**
	 * @param tenorLabel    The tenor label of the IBOR.
	 * @param maturityLabel The maturity label (like 1Y, 2Y).
	 * @param depositRate   The fra rate (use 0.05 for 5%).
	 */
	public CalibrationSpecProviderDeposit(final String tenorLabel, final String maturityLabel, final double depositRate) {
		this.tenorLabel = tenorLabel;
		this.maturityLabel = maturityLabel;
		this.depositRate = depositRate;
	}

	@Override
	public CalibratedCurves.CalibrationSpec getCalibrationSpec(final CalibrationContext ctx) {
		final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, "tenor", "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final double calibrationTime = scheduleInterfaceRec.getFixing(scheduleInterfaceRec.getNumberOfPeriods() - 1);

		final String curveName = String.format("forward-EUR-%1$s", tenorLabel);

		return new CalibratedCurves.CalibrationSpec("EUR-" + tenorLabel + maturityLabel, "Deposit", scheduleInterfaceRec, curveName, depositRate, "discount-EUR-OIS", null, "", 0.0, "discount-EUR-OIS", curveName, calibrationTime);
	}
}
