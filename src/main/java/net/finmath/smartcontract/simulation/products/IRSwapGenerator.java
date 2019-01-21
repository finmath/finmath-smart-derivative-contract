package net.finmath.smartcontract.simulation.products;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * Scenario Generator provides static method for generating an analytic swap object
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class IRSwapGenerator {

	public static final Swap generateAnalyticSwapObject(LocalDate startDate, String maturityLabel, double fixRate,boolean isReceiveFix, String forwardCurveName, String discountCurveName){

		String frequencyLabel = forwardCurveName.contains("3M") ? "quarterly" : forwardCurveName.contains("6M") ? "semiannual" : forwardCurveName.contains("1M") ? "monthly" : "annual";

		Schedule scheduleFloat = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityLabel, frequencyLabel, "act/360", "first", "following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		Schedule scheduleFix = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityLabel, "annual", "E30/360", "first", "following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		SwapLeg floatLeg = new SwapLeg(Optional.of(LocalDateTime.of(startDate, LocalTime.of(0,0))), scheduleFloat,forwardCurveName,0.0,discountCurveName);
		SwapLeg fixLeg = new SwapLeg(Optional.of(LocalDateTime.of(startDate, LocalTime.of(0,0))), scheduleFix,"",fixRate,discountCurveName);

		Swap swap = isReceiveFix ? new Swap(fixLeg,floatLeg) : new Swap(floatLeg,fixLeg);
		return swap;
	}


}
