/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.valuation.oracle;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * A valuation oracle constructed from a simulation providing a stochastic valuation oracle
 * by extracting a given sample path.
 *
 * @author Christian Fries
 */
public class ValuationOracleSamplePath implements ValuationOracle {

	private final CurrencyUnit currency = Monetary.getCurrency("EUR");
	private final StochasticValuationOracle stochasticValuationOracle;
	private final int path;

	/**
	 * Create a valuation oracle from a simulation providing a stochastic valuation oracle
	 * by extracting a given sample path.
	 *
	 * @param stochasticValuationOracle A given stochastic oracle.
	 * @param path                      The sample path to extract from the stochastic oracle.
	 */
	public ValuationOracleSamplePath(final StochasticValuationOracle stochasticValuationOracle, final int path) {
		this.stochasticValuationOracle = stochasticValuationOracle;
		this.path = path;
	}

	@Override
	public Double getValue(final LocalDateTime evaluationTime, final LocalDateTime marketDataTime) {
		return stochasticValuationOracle.getValue(evaluationTime, marketDataTime).get(path);
	}

	@Override
	public Map<String, Double> getValues(LocalDateTime evaluationTime, LocalDateTime marketDataTime) {
		return Map.of("value", getValue(evaluationTime, marketDataTime));
	}

	@Override
	public MonetaryAmount getAmount(final LocalDateTime evaluationTime, final LocalDateTime marketDataTime) {
		return Money.of(getValue(evaluationTime, marketDataTime), currency);
	}
}
