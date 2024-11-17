/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 7 Oct 2018
 */

package net.finmath.smartcontract;

import net.finmath.smartcontract.valuation.oracle.SmartDerivativeContractSettlementOracle;
import net.finmath.smartcontract.valuation.oracle.StochasticValuationOracle;
import net.finmath.smartcontract.valuation.oracle.ValuationOracle;
import net.finmath.smartcontract.valuation.oracle.ValuationOracleSamplePath;
import net.finmath.smartcontract.valuation.oracle.simulated.GeometricBrownianMotionOracle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * @author Christian Fries
 */
public class SmartDerivativeContractMarginingTest {

	@Test
	public void test() {
		final LocalDateTime initialTime = LocalDateTime.of(2018, 8, 12, 12, 00);
		final LocalDateTime finalTime = LocalDateTime.of(2028, 8, 12, 12, 00);
		final int path = 0;

		final StochasticValuationOracle stoachasticOracleForValuation = new GeometricBrownianMotionOracle(initialTime);
		//		StochasticValuationOracle stoachasticOracleForValuation = new BrownianMotionOracle(initialTime);
		final ValuationOracle valuationOracle = new ValuationOracleSamplePath(stoachasticOracleForValuation, path);

		final SmartDerivativeContractSettlementOracle smartDerivativeContractMargening = new SmartDerivativeContractSettlementOracle(valuationOracle);

		LocalDateTime previousTime = initialTime;
		for (LocalDateTime time = initialTime; time.isBefore(finalTime); time = time.plusDays(1)) {
			final double value = smartDerivativeContractMargening.getMargin(previousTime, time).get("value");
			System.out.println(time.toLocalDate() + "\t" + value);
			previousTime = time;
		}
	}
}
