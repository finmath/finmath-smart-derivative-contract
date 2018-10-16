/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 16 Oct 2018
 */

package net.finmath.smartcontract.contract;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.finmath.smartcontract.contract.SmartDerivativeContractSchedule.EventTimes;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * @author Christian Fries
 *
 */
public class SmartDerivativeContractScheduleGenerator {

	public static class EventTimesImpl implements SmartDerivativeContractSchedule.EventTimes {
		private final LocalDateTime settementTime;
		private final LocalDateTime accountAccessAllowedStart;
		private final Duration accountAccessAllowedPeriod;
		private final LocalDateTime marginCheckTime;

		public EventTimesImpl(LocalDateTime settementTime, LocalDateTime accountAccessAllowedStart,
				Duration accountAccessAllowedPeriod, LocalDateTime marginCheckTime) {
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

	public static class SimpleSchedule implements SmartDerivativeContractSchedule {
		private final List<SmartDerivativeContractSchedule.EventTimes> eventTimes;

		public SimpleSchedule(List<EventTimes> eventTimes) {
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
	 * @param calendar
	 * @param startDate
	 * @param maturity
	 * @param settlementTime
	 * @param accountAccessAllowedDuration
	 * @return A new schedule corresponding to the given meta data.
	 */
	public static SmartDerivativeContractSchedule getScheduleForBusinessDays(String calendar, LocalDate startDate, LocalDate maturity,
			LocalTime settlementTime,
			Duration accountAccessAllowedDuration) {

		return getScheduleForBusinessDays(calendar, startDate, maturity, settlementTime, settlementTime.plusMinutes(1), accountAccessAllowedDuration, settlementTime.plusMinutes(1).plusSeconds(accountAccessAllowedDuration.getSeconds()).plusMinutes(1));
	}

	public static SmartDerivativeContractSchedule getScheduleForBusinessDays(String calendar, LocalDate startDate, LocalDate maturity,
			LocalTime settlementTime,
			LocalTime accountAccessAllowedStartTime,
			Duration accountAccessAllowedDuration,
			LocalTime marginCheckTime) {

		BusinessdayCalendar bdCalendar = new BusinessdayCalendarExcludingTARGETHolidays();

		List<EventTimes> eventTimesList = new ArrayList<EventTimes>();
		LocalDate settlementDate = startDate;
		while(!settlementDate.isAfter(maturity)) {
			SmartDerivativeContractSchedule.EventTimes event = new EventTimesImpl(
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
