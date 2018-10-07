/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import java.time.LocalDateTime;
import java.util.Optional;

import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.stochastic.Scalar;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * A dummy oracle which generates values as initalValue * Math.exp(r T).
 *
 * @author Christian Fries
 */
public class ContinouslyCompoundedBankAccountOracle implements StochasticValuationOracle {

	private final TimeDiscretizationInterface timeDiscretization;

	private final LocalDateTime initialTime;
	private final double initialValue;
	private final double riskFreeRate;

	/**
	 * A dummy oracle which generates values as initalValue * Math.exp(r T).
	 *
	 * Caution: The object is initialized with LocalDateTime.now(). This will result in different
	 * Oracles each time the object is instantiated.
	 */
	public ContinouslyCompoundedBankAccountOracle() {
		this(LocalDateTime.now());
	}

	/**
	 * A dummy oracle which generates values as initalValue * Math.exp(r T).
	 *
	 * Using a given initial time and default parameters.
	 */
	public ContinouslyCompoundedBankAccountOracle(LocalDateTime initialTime) {
		this(new TimeDiscretization(0.0, 20.0, 0.001, TimeDiscretization.ShortPeriodLocation.SHORT_PERIOD_AT_END),
				initialTime,
				1.0,
				0.02);
	}

	public ContinouslyCompoundedBankAccountOracle(TimeDiscretizationInterface timeDiscretization, LocalDateTime initialTime,
			double initialValue, double riskFreeRate) {
		super();
		this.timeDiscretization = timeDiscretization;
		this.initialTime = initialTime;
		this.initialValue = initialValue;
		this.riskFreeRate = riskFreeRate;
	}

	@Override
	public Optional<RandomVariableInterface> getValue(LocalDateTime evaluationTime) {

		// TODO: We use rounding to a days here!
		double time = FloatingpointDate.getFloatingPointDateFromDate(initialTime.toLocalDate(), evaluationTime.toLocalDate());

		return Optional.of(new Scalar(initialValue * Math.exp(riskFreeRate * time)));
	}
}
