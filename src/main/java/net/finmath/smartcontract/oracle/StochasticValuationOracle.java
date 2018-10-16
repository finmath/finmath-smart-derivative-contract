/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import java.time.LocalDateTime;
import java.util.Optional;

import net.finmath.stochastic.RandomVariableInterface;

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
	 *
	 * @return The value.
	 */
	RandomVariableInterface getValue(LocalDateTime evaluationTime);
}
