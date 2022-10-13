/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import net.finmath.stochastic.RandomVariable;

import java.time.LocalDateTime;

/**
 * Interface for Oracles providing a valuation random variables at a given time.
 * This type of oracle can be used in simulations. The return value is a random variable (sample vector).
 *
 * @author Christian Fries
 */
public interface StochasticValuationOracle {

	/**
	 * Provides that value of the Oracle at a given evaluation time.
	 *
	 * @param evaluationTime The evaluation time.
	 * @param marketDataTime The market data time.
	 * @return The value.
	 */
	RandomVariable getValue(final LocalDateTime evaluationTime, final LocalDateTime marketDataTime);
}
