package net.finmath.smartcontract.valuation.marketdata.curvecalibration;


import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleFromPeriods;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import net.finmath.time.daycount.DayCountConvention_ACT_360;

import java.time.LocalDate;

/**
 * A calibration spec provider for fras.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationSpecProviderFRA implements CalibrationSpecProvider {
	private final String tenorLabel;
	private final String maturityLabel;
	private final double fraRate;
	private final String startOffsetLabel;

	/**
	 * @param tenorLabel    The tenor label of the IBOR.
	 * @param maturityLabel The maturity label provided in months
	 * @param fraRate       The fra rate (use 0.05 for 5%).
	 */
	public CalibrationSpecProviderFRA(final String tenorLabel, final String maturityLabel, final double fraRate) {
		this.tenorLabel = tenorLabel;
		this.maturityLabel = maturityLabel;
		this.fraRate = fraRate;
		int nMonthTenor = Integer.parseInt(tenorLabel.replace("M", ""));
		int nMonthMaturity = Integer.parseInt(maturityLabel.replace("M", ""));
		int nMonthOffset = nMonthMaturity - nMonthTenor;
		this.startOffsetLabel = nMonthOffset + "M";
	}

	@Override
	public CalibratedCurves.CalibrationSpec getCalibrationSpec(final CalibrationContext ctx) {
		BusinessdayCalendar calendar = new BusinessdayCalendarExcludingTARGETHolidays();
		final Schedule scheduleInterfacePrelim = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, startOffsetLabel, tenorLabel, "tenor", "ACT/360", "first", "follow", calendar, -2, 0);
		// periodStartDate = referenceDate + spotOffsetDays (2BD) + startOffsetLabel (e.g. 2M) + dateRollConvention (follow)
		// fixingDate = periodStartDate + fixingOffset (-2BD)
		// period end = period start + tenorLabel (6M) -> this roll out is currently not supported by createScheduleFromConventions
		// paymentDate = periodStartDate BUT According to Ametrano/ Bianchetti (2013) p.22, the size of the convexity adjustment between market FRA and textbook FRA is negligible
		// We leave the payment date as it is, and are discounting the FRA legs by the forward rate
		LocalDate periodStartDate = scheduleInterfacePrelim.getPeriod(0).getPeriodStart();
		LocalDate paymentDate = scheduleInterfacePrelim.getPeriod(0).getPayment();
		LocalDate fixingDate = scheduleInterfacePrelim.getPeriod(0).getFixing();
		LocalDate periodEndDate = calendar.getAdjustedDate(periodStartDate, tenorLabel, BusinessdayCalendar.DateRollConvention.FOLLOWING);

		Period period = new Period(fixingDate, paymentDate, periodStartDate, periodEndDate);
		final Schedule scheduleInterfaceFinal = new ScheduleFromPeriods(ctx.getReferenceDate(), new DayCountConvention_ACT_360(), period);
		final double calibrationTime = scheduleInterfaceFinal.getFixing(scheduleInterfaceFinal.getNumberOfPeriods() - 1);

		final String curveName = String.format("forward-EUR-%1$s", tenorLabel);
		return new CalibratedCurves.CalibrationSpec("EUR-" + tenorLabel + maturityLabel, "FRA", scheduleInterfaceFinal, curveName, fraRate, "discount-EUR-OIS", null, "", 0.0, null, curveName, calibrationTime);
	}
}
