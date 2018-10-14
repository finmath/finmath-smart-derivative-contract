/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.assetderivativevaluation.BachelierModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * A dummy oracle which generates values using a geometric Brownian motion.
 *
 * @author Christian Fries
 */
public class BrownianMotionOracle implements StochasticValuationOracle {

	private final TimeDiscretizationInterface timeDiscretization;

	private final LocalDateTime initialTime;
	private final double initialValue;
	private final double riskFreeRate;
	private final double volatility;
	private final int numberOfPaths;

	private transient MonteCarloAssetModel simulation;
	private final Object simulationLazyInitLock = new Object();

	/**
	 * A dummy oracle which generates values using a geometric Brownian motion.
	 *
	 * Using default parameters.
	 *
	 * Caution: The object is initialized with LocalDateTime.now(). This will result in different
	 * Oracles each time the object is instantiated.
	 */
	public BrownianMotionOracle() {
		this(LocalDateTime.now());
	}

	/**
	 * A dummy oracle which generates values using a geometric Brownian motion.
	 *
	 * Using a given initial time and default parameters.
	 * 
	 * @param initialTime The date corresponding to the initial time of the oracle. Valuation prior this time is not provided.
	 */
	public BrownianMotionOracle(LocalDateTime initialTime) {
		this(initialTime,
				0.0 /* initialValue */,
				20.0 /* timeHorizon */,
				0.02 /* riskFreeRate */,
				0.10 /* volatility */,
				1000 /* numberOfPaths */);
	}

	/**
	 * A dummy oracle which generates values using a geometric Brownian motion.
	 *
	 * Using a given initial time and default parameters.
	 * 
	 * @param initialTime The date corresponding to the initial time of the oracle. Valuation prior this time is not provided.
	 * @param initialValue The initial value.
	 * @param timeHorizon The time horizon in ACT/365 from initialTime.
	 * @param riskFreeRate The drift.
	 * @param volatility The volatility.
	 * @param numberOfPaths The number of simulation path to generate.
	 */
	public BrownianMotionOracle(LocalDateTime initialTime, double initialValue, double timeHorizon, double riskFreeRate, double volatility, int numberOfPaths) {
		this(new TimeDiscretization(0.0, timeHorizon, 1.0/365.0, TimeDiscretization.ShortPeriodLocation.SHORT_PERIOD_AT_END),
				initialTime,
				initialValue,
				riskFreeRate,
				volatility,
				numberOfPaths);
	}

	public BrownianMotionOracle(TimeDiscretizationInterface timeDiscretization, LocalDateTime initialTime,
			double initialValue, double riskFreeRate, double volatility, int numberOfPaths) {
		super();
		this.timeDiscretization = timeDiscretization;
		this.initialTime = initialTime;
		this.initialValue = initialValue;
		this.riskFreeRate = riskFreeRate;
		this.volatility = volatility;
		this.numberOfPaths = numberOfPaths;
	}

	private void init() {
		final int numberOfFactors = 1;
		final int seed = 31415;

		simulation = new MonteCarloAssetModel(
				new BachelierModel(initialValue, riskFreeRate, volatility),
				new ProcessEulerScheme(new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed)));
	}

	@Override
	public Optional<RandomVariableInterface> getValue(LocalDateTime evaluationTime) {
		synchronized (simulationLazyInitLock) {
			if(simulation == null) {
				init();
			}
		}

		// TODO: We use rounding to a days here!
		double time = FloatingpointDate.getFloatingPointDateFromDate(initialTime.toLocalDate(), evaluationTime.toLocalDate());

		int timeIndexOfLastFixing = timeDiscretization.getTimeIndexNearestLessOrEqual(time);
		double timeOfLastFixing = timeDiscretization.getTime(timeIndexOfLastFixing);

		RandomVariableInterface value = null;
		try {
			value = simulation.getAssetValue(timeOfLastFixing, 0);
		}
		catch(CalculationException e) {
			Logger.getLogger("net.finmath.smartcontract").warning("Oracle valuation failed with " + e.getCause());
		}

		return Optional.ofNullable(value);
	}
}
