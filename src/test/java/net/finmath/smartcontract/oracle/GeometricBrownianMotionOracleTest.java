/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 6 Oct 2018
 */

package net.finmath.smartcontract.oracle;

import java.time.LocalDateTime;

import org.junit.Test;

/**
 * @author Christian Fries
 */
public class GeometricBrownianMotionOracleTest {

	@Test
	public void test() {
		LocalDateTime initialTime = LocalDateTime.of(2018, 8, 12, 12, 00);
		LocalDateTime finalTime = LocalDateTime.of(2028, 8, 12, 12, 00);
		int path = 0;

		StochasticValuationOracle stoachasticOracle = new GeometricBrownianMotionOracle(initialTime);

		ValuationOracle oracle = new ValuationOracleSamplePath(stoachasticOracle, path);

		for(LocalDateTime time = initialTime; time.isBefore(finalTime); time = time.plusDays(1)) {

			double value = oracle.getValue(time).get();
			System.out.println(time.toLocalDate() + "\t" + value);

		}
	}

}