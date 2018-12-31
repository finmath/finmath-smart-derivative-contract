/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 7 Oct 2018
 */

package net.finmath.smartcontract.contract;

import java.time.LocalDateTime;

/**
 * Observable smart derivative contract event.
 *
 * @author Christian Fries
 */
public class SmartDerivativeContractEvent {

	public enum EventsTypes {
		INIT,
		SETTLEMENT,
		ACCOUNTS_ACCESSIBLE_START,
		ACCOUNTS_ACCESSIBLE_END,
		CHECK_MARGIN,
		MATURED,
	}

	private EventsTypes eventType;
	private LocalDateTime eventTime;
	private Object eventData;

	public SmartDerivativeContractEvent(EventsTypes eventType, LocalDateTime eventTime, Object eventData) {
		super();
		this.eventType = eventType;
		this.eventTime = eventTime;
		this.eventData = eventData;
	}
	/**
	 * @return the eventType
	 */
	public EventsTypes getEventType() {
		return eventType;
	}
	/**
	 * @return the eventTime
	 */
	public LocalDateTime getEventTime() {
		return eventTime;
	}
	/**
	 * @return the eventData
	 */
	public Object getEventData() {
		return eventData;
	}
}
