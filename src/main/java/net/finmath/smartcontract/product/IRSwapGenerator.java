package net.finmath.smartcontract.product;

import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Scenario Generator provides static method for generating an analytic swap object
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class IRSwapGenerator {

	private IRSwapGenerator(){}

	public static Swap generateAnalyticSwapObject(final LocalDate startDate, final String maturityLabel, final double fixRate, final boolean isReceiveFix, final String forwardCurveName, final String discountCurveName) {

		final String frequencyLabel = forwardCurveName.contains("3M") ? "quarterly" : forwardCurveName.contains("6M") ? "semiannual" : forwardCurveName.contains("1M") ? "monthly" : "annual";

		final Schedule scheduleFloat = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityLabel, frequencyLabel, "act/360", "first", "following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final Schedule scheduleFix = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityLabel, "annual", "E30/360", "first", "following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final SwapLeg floatLeg = new SwapLeg(Optional.of(LocalDateTime.of(startDate, LocalTime.of(0, 0))), scheduleFloat, forwardCurveName, 0.0, discountCurveName);
		final SwapLeg fixLeg = new SwapLeg(Optional.of(LocalDateTime.of(startDate, LocalTime.of(0, 0))), scheduleFix, "", fixRate, discountCurveName);

		return isReceiveFix ? new Swap(fixLeg, floatLeg) : new Swap(floatLeg, fixLeg);
	}


}
