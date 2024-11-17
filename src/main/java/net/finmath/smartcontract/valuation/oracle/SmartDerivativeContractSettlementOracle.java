/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 7 Oct 2018
 */

package net.finmath.smartcontract.valuation.oracle;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The margin agreement of a smart derivative contract.
 * The agreement consists of
 * <ul>
 * <li>a valuation oracle, implementing <code>net.finmath.smartcontract.oracle.ValuationOracle</code></li>
 * </ul>
 * <p>
 * The accrual of the collateral is assumed to e consistent with the valuation, hence, the accrued collateral
 * can be determined from calling <code>getValue(marginPeriodEnd, marginPeriodStart)</code>.
 *
 * @author Christian Fries
 * @see ValuationOracle
 */
public class SmartDerivativeContractSettlementOracle {

	private final ValuationOracle derivativeValuationOracle;

	public SmartDerivativeContractSettlementOracle(final ValuationOracle derivativeValuationOracle) {
		super();
		this.derivativeValuationOracle = derivativeValuationOracle;
	}

	/**
	 * Get the margin of the contract based on the valuation oracles.
	 *
	 * @param marginPeriodStart Period start time of the margin period.
	 * @param marginPeriodEnd   Period end time of the margin period.
	 * @return The margin.
	 */
	public Map<String, Double> getMargin(final LocalDateTime marginPeriodStart, final LocalDateTime marginPeriodEnd) {
		final Map<String, Double> valueDerivativeCurrent = derivativeValuationOracle.getValues(marginPeriodEnd, marginPeriodEnd);
		final Map<String, Double> valueDerivativePrevious = derivativeValuationOracle.getValues(marginPeriodEnd, marginPeriodStart);

		Map<String, Double> margin = valueDerivativeCurrent.keySet().stream().collect(Collectors.toMap(
				key -> key,
				key -> valueDerivativeCurrent.get(key) - valueDerivativePrevious.get(key)
		));

		return margin;
	}
}
