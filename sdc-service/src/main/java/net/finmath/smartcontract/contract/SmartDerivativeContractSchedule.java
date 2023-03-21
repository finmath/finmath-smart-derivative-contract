/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 16 Oct 2018
 */

package net.finmath.smartcontract.contract;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Minimal interface for the event times of a smart derivative contract.
 * The event times and events are as follows:
 *
 * <ul>
 * <li> \( t^{\text{settlement}}_{i} \) - the time at which the settlement is performed</li>
 * <li> \( t^{\text{marginCheck}}_{i} \) - the time at which the margin accounts are checked for validity</li>
 * <li> \( [ t^{\text{accountAccessStart}}_{i}, t^{\text{accountAccessEnd}}_{i} \) - the time interval at which the margin accounts may be accessed</li>
 * </ul>
 * <p>
 * The interface provides these times in a collection. Note that the time \( t^{\text{accountAccessEnd}}_{i} \) is provided
 * as an offset from \( t^{\text{accountAccessStart}}_{i} \).
 *
 * @author Christian Fries
 */
public interface SmartDerivativeContractSchedule {

	/**
	 * Interface for a smart derivative event time set consisting of
	 * settlement, account access (start and duration) and margin check.
	 *
	 * @author Christian Fries
	 */
	interface EventTimes {
		LocalDateTime getSettementTime();

		LocalDateTime getAccountAccessAllowedStart();

		Duration getAccountAccessAllowedPeriod();

		LocalDateTime getMarginCheckTime();
	}

	List<EventTimes> getEventTimes();
}
