/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import java.time.LocalDateTime;

/**
 * Interface for Oracles providing a valuation at a given time.
 *
 * @author Christian Fries
 */
public interface ValuationOracle {

	/**
	 * Provides that value of the Oracle at a given evaluation time.
	 *
	 * @param evaluationTime The evaluation time.
	 *
	 * @return The value.
	 */
	Double getValue(LocalDateTime evaluationTime);
}
