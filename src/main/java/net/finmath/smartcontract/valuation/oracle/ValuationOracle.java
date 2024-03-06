/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.valuation.oracle;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * Interface for Oracles providing a valuation at a given time.
 *
 * @author Christian Fries
 */
public interface ValuationOracle {

	/**
	 * Provides the value of the Oracle at a given evaluation time using market data from a given time.
	 * <p>
	 * Note: The use of two different dates allows to model the accrual/discounting at a (market data given) accrual rate.
	 *
	 * @param evaluationTime The evaluation time.
	 * @param marketDataTime The market data time.
	 * @return The value.
	 */
	Double getValue(LocalDateTime evaluationTime, LocalDateTime marketDataTime);

	/**
	 * Provides the value of the Oracle at a given evaluation time.
	 * <p>
	 * Note: The use of two different dates allows to model the accrual/discounting at a (market data given) accrual rate.
	 *
	 * @param evaluationTime The evaluation time.
	 * @param marketDataTime The market data time.
	 * @return The amount.
	 */
	MonetaryAmount getAmount(LocalDateTime evaluationTime, LocalDateTime marketDataTime);

}
