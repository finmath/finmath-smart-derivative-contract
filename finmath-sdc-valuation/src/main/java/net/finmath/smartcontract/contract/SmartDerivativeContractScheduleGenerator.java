/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 16 Oct 2018
 */

package net.finmath.smartcontract.contract;

import net.finmath.smartcontract.contract.SmartDerivativeContractSchedule.EventTimes;
import net.finmath.time.businessdaycalendar.AbstractBusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates schedules for smart derivative contracts.
 * The schedule consists of settlement, account access and margin checks.
 *
 * @author Christian Fries
 */
public class SmartDerivativeContractScheduleGenerator {

	private SmartDerivativeContractScheduleGenerator(){}

	/**
	 * Simple POJO implementation of <code>SmartDerivativeContractSchedule.EventTimes</code>.
	 *
	 * @author Christian Fries
	 */
	public static class EventTimesImpl implements SmartDerivativeContractSchedule.EventTimes {
		private final LocalDateTime settementTime;
		private final LocalDateTime accountAccessAllowedStart;
		private final Duration accountAccessAllowedPeriod;
		private final LocalDateTime marginCheckTime;

		public EventTimesImpl(final LocalDateTime settementTime, final LocalDateTime accountAccessAllowedStart,
							  final Duration accountAccessAllowedPeriod, final LocalDateTime marginCheckTime) {
			super();
			this.settementTime = settementTime;
			this.accountAccessAllowedStart = accountAccessAllowedStart;
			this.accountAccessAllowedPeriod = accountAccessAllowedPeriod;
			this.marginCheckTime = marginCheckTime;
		}

		/**
		 * @return the settementTime
		 */
		public LocalDateTime getSettementTime() {
			return settementTime;
		}

		/**
		 * @return the accountAccessAllowedStart
		 */
		public LocalDateTime getAccountAccessAllowedStart() {
			return accountAccessAllowedStart;
		}

		/**
		 * @return the accountAccessAllowedPeriod
		 */
		public Duration getAccountAccessAllowedPeriod() {
			return accountAccessAllowedPeriod;
		}

		/**
		 * @return the marginCheckTime
		 */
		public LocalDateTime getMarginCheckTime() {
			return marginCheckTime;
		}
	}

	/**
	 * Simple list based implementation of <code>SmartDerivativeContractSchedule</code>.
	 *
	 * @author Christian Fries
	 */
	public static class SimpleSchedule implements SmartDerivativeContractSchedule {
		private final List<SmartDerivativeContractSchedule.EventTimes> eventTimes;

		public SimpleSchedule(final List<EventTimes> eventTimes) {
			super();
			this.eventTimes = eventTimes;
		}

		@Override
		public List<EventTimes> getEventTimes() {
			return eventTimes;
		}
	}

	/**
	 * Create a daily event schedule, where
	 * accountAccessStart is one minute after settlementTime,
	 * accountAccessEnd is accountAccessAllowedDuration after accountAccessStart,
	 * marginCheckTime is one minute after accountAccessEnd.
	 *
	 * @param calendar                     A businessday calendar (e.g.: TARGET2)
	 * @param startDate                    The start date.
	 * @param maturity                     The maturity date.
	 * @param settlementTime               The settlement time.
	 * @param accountAccessAllowedDuration The duration for account adjustment after settlement.
	 * @return A new schedule corresponding to the given meta data.
	 */
	public static SmartDerivativeContractSchedule getScheduleForBusinessDays(final String calendar, final LocalDate startDate, final LocalDate maturity,
																			 final LocalTime settlementTime,
																			 final Duration accountAccessAllowedDuration) {

		return getScheduleForBusinessDays(calendar, startDate, maturity, settlementTime, settlementTime.plusMinutes(1), accountAccessAllowedDuration, settlementTime.plusMinutes(1).plusSeconds(accountAccessAllowedDuration.getSeconds()).plusMinutes(1));
	}

	public static SmartDerivativeContractSchedule getScheduleForBusinessDays(final String calendar, final LocalDate startDate, final LocalDate maturity,
																			 final LocalTime settlementTime,
																			 final LocalTime accountAccessAllowedStartTime,
																			 final Duration accountAccessAllowedDuration,
																			 final LocalTime marginCheckTime) {

		final AbstractBusinessdayCalendar bdCalendar = new BusinessdayCalendarExcludingTARGETHolidays();

		final List<EventTimes> eventTimesList = new ArrayList<>();
		LocalDate settlementDate = startDate;
		while (!settlementDate.isAfter(maturity)) {
			final SmartDerivativeContractSchedule.EventTimes event = new EventTimesImpl(
					settlementDate.atTime(settlementTime),
					settlementDate.atTime(accountAccessAllowedStartTime),
					accountAccessAllowedDuration,
					settlementDate.atTime(marginCheckTime));
			eventTimesList.add(event);

			settlementDate = bdCalendar.getRolledDate(settlementDate, 1);
		}

		return new SimpleSchedule(eventTimesList);
	}
}
