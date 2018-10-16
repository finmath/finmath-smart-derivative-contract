/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 7 Oct 2018
 */

package net.finmath.smartcontract.contract;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import net.finmath.smartcontract.oracle.ValuationOracle;

/**
 * The margin agreement of a smart derivative contract.
 * The agreement consists of
 * <ul>
 * <li>a valuation oracle, implementing <code>net.finmath.smartcontract.oracle.ValuationOracle</code></li>
 * <li>a collateral accrual account, implementing <code>net.finmath.smartcontract.oracle.ValuationOracle</code></li>
 * </ul>
 *
 * @author Christian Fries
 * @see net.finmath.smartcontract.oracle.ValuationOracle
 */
public class SmartDerivativeContractMargining {

	private final ValuationOracle derivativeValuationOracle;
	private final ValuationOracle collateralValuationOracle;

	public SmartDerivativeContractMargining(ValuationOracle derivativeValuationOracle, ValuationOracle collateralValuationOracle) {
		super();
		this.derivativeValuationOracle = derivativeValuationOracle;
		this.collateralValuationOracle = collateralValuationOracle;
	}

	/**
	 * Get the margin of the contract based on the valuation oracles.
	 *
	 * @param marginPeriodStart Period start time of the margin period.
	 * @param marginPeriodEnd Period end time of the margin period.
	 * @return The margin.
	 */
	public Double getMargin(LocalDateTime marginPeriodStart, LocalDateTime marginPeriodEnd) {

		try {
			double valueDerivativeCurrent = derivativeValuationOracle.getValue(marginPeriodEnd);
			double valueDerivativePrevious = derivativeValuationOracle.getValue(marginPeriodStart);

			double valueCollateralCurrent = collateralValuationOracle.getValue(marginPeriodEnd);
			double valueCollateralPrevious = collateralValuationOracle.getValue(marginPeriodStart);

			double valuationChange = valueDerivativeCurrent - valueDerivativePrevious;
			double collateralChange = valueDerivativePrevious * (valueCollateralCurrent/valueCollateralPrevious - 1.0);

			double margin = valuationChange - collateralChange;

			return margin;
		}
		catch(NoSuchElementException e) {
			return null;
		}
	}

}
