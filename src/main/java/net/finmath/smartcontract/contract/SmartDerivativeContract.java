/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 7 Oct 2018
 */

package net.finmath.smartcontract.contract;

/**
 * @author Christian Fries
 *
 */
public class SmartDerivativeContract {

	public enum EventsTypes {
		INIT,
		SETTLEMENT,
		ACCOUNTS_ACCESSIBLE_START,
		ACCOUNTS_ACCESSIBLE_END,
		CHECK_MARGIN,
		MATURED,
	}

	/**
	 *
	 */
	public SmartDerivativeContract() {
		// TODO Auto-generated constructor stub
	}

}
