/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import java.time.LocalDateTime;

import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * A dummy oracle which generates values as initalValue * Math.exp(r T).
 *
 * @author Christian Fries
 */
public class ContinouslyCompoundedBankAccountOracle implements StochasticValuationOracle {

	private final TimeDiscretization timeDiscretization;

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
	 *
	 * @param initialTime The date corresponding to the initial time of the oracle. Valuation prior this time is not provided.
	 */
	public ContinouslyCompoundedBankAccountOracle(LocalDateTime initialTime) {
		this(new TimeDiscretizationFromArray(0.0, 20.0, 1.0/365.0, TimeDiscretizationFromArray.ShortPeriodLocation.SHORT_PERIOD_AT_END),
				initialTime,
				1.0,
				0.02);
	}

	/**
	 * A dummy oracle which generates values as initalValue * Math.exp(r T).
	 *
	 * Using a given initial time and default parameters.
	 *
	 * @param initialTime The date corresponding to the initial time of the oracle. Valuation prior this time is not provided.
	 * @param initialValue The initial value.
	 * @param timeHorizon The time horizon in ACT/365 from initialTime.
	 * @param riskFreeRate The drift.
	 */
	public ContinouslyCompoundedBankAccountOracle(LocalDateTime initialTime, double initialValue, double timeHorizon, double riskFreeRate) {
		this(new TimeDiscretizationFromArray(0.0, timeHorizon, 1.0/365.0, TimeDiscretizationFromArray.ShortPeriodLocation.SHORT_PERIOD_AT_END),
				initialTime,
				initialValue,
				riskFreeRate);
	}

	public ContinouslyCompoundedBankAccountOracle(TimeDiscretization timeDiscretization, LocalDateTime initialTime,
			double initialValue, double riskFreeRate) {
		super();
		this.timeDiscretization = timeDiscretization;
		this.initialTime = initialTime;
		this.initialValue = initialValue;
		this.riskFreeRate = riskFreeRate;
	}

	@Override
	public RandomVariable getValue(LocalDateTime evaluationTime) {

		double time = FloatingpointDate.getFloatingPointDateFromDate(initialTime, evaluationTime);

		return new Scalar(initialValue * Math.exp(riskFreeRate * time));
	}
}
