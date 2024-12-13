package net.finmath.smartcontract.product;

import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * Generates an interest rate swap. This is used for testing and visualization.
 * Alternative way to generate the swap is via the parser.
 *
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class IRSwapGenerator {

	private IRSwapGenerator(){}

	public static Swap generateAnalyticSwapObject(final LocalDate startDate, final String maturityLabel, final double notional, final double fixRate, final boolean isReceiveFix, final String forwardCurveName, final String discountCurveName) {

		final String frequencyLabel = forwardCurveName.contains("3M") ? "quarterly" : forwardCurveName.contains("6M") ? "semiannual" : forwardCurveName.contains("1M") ? "monthly" : "annual";

		// Schedules
		final Schedule scheduleFloat = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityLabel, frequencyLabel, "act/360", "first", "following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
		final Schedule scheduleFix = ScheduleGenerator.createScheduleFromConventions(startDate, 2, "0D", maturityLabel, "annual", "E30/360", "first", "following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);

		// TODO Remove hardcoded effective date
		// TODO Test effective date from parser
		LocalDateTime cashFlowEffectiveDate = LocalDateTime.of(startDate, LocalTime.of(0, 0));
		boolean isNotionalExchanged = false;

		final double[] notionalsFloat = new double[scheduleFloat.getNumberOfPeriods()];
		final double[] spreadsFloat = new double[scheduleFloat.getNumberOfPeriods()];
		Arrays.fill(notionalsFloat, notional);
		Arrays.fill(spreadsFloat, 0.0);
		final SwapLeg floatLeg = new SwapLeg(Optional.of(cashFlowEffectiveDate), scheduleFloat, forwardCurveName, notionalsFloat, spreadsFloat, discountCurveName, isNotionalExchanged);

		final double[] notionalsFix = new double[scheduleFix.getNumberOfPeriods()];
		final double[] spreadsFix = new double[scheduleFix.getNumberOfPeriods()];
		Arrays.fill(notionalsFix, notional);
		Arrays.fill(spreadsFix, fixRate);
		final SwapLeg fixLeg = new SwapLeg(Optional.of(LocalDateTime.of(startDate, LocalTime.of(0, 0))), scheduleFix, "", fixRate, discountCurveName);

		return isReceiveFix ? new Swap(fixLeg, floatLeg) : new Swap(floatLeg, fixLeg);
	}
}
