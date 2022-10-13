package net.finmath.smartcontract.simulation.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * A calibration spec provider for swaps.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationSpecProviderSwap implements CalibrationSpecProvider {
	private final String tenorLabel;
	private final String frequencyLabel;
	private final String maturityLabel;
	private final double swapRate;

	/**
	 * @param tenorLabel     The tenor label of the IBOR.
	 * @param frequencyLabel The frequency label for the floating leg (fixed leg is assumed to be annual).
	 * @param maturityLabel  The maturity label (like 1Y, 2Y).
	 * @param swapRate       The par swap rate (use 0.05 for 5%).
	 */
	public CalibrationSpecProviderSwap(final String tenorLabel, final String frequencyLabel, final String maturityLabel, final double swapRate) {
		this.tenorLabel = tenorLabel;
		this.frequencyLabel = frequencyLabel;
		this.maturityLabel = maturityLabel;
		this.swapRate = swapRate;
	}

	@Override
	public CalibratedCurves.CalibrationSpec getCalibrationSpec(final CalibrationContext ctx) {
		final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, frequencyLabel, "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final Schedule scheduleInterfacePay = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, "annual", "E30/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final double calibrationTime = scheduleInterfaceRec.getFixing(scheduleInterfaceRec.getNumberOfPeriods() - 1);

		final String curveName = String.format("forward-EUR-%1$s", tenorLabel);

		return new CalibratedCurves.CalibrationSpec("EUR-" + tenorLabel + maturityLabel, "Swap", scheduleInterfaceRec, curveName, 0.0, "discount-EUR-OIS", scheduleInterfacePay, "", swapRate, "discount-EUR-OIS", curveName, calibrationTime);
	}
}
