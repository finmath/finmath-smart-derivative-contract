/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 7 Oct 2018
 */

package net.finmath.smartcontract;

import java.time.LocalDateTime;

import org.junit.Test;

import net.finmath.smartcontract.contract.SmartDerivativeContractMargining;
import net.finmath.smartcontract.oracle.ContinouslyCompoundedBankAccountOracle;
import net.finmath.smartcontract.oracle.GeometricBrownianMotionOracle;
import net.finmath.smartcontract.oracle.StochasticValuationOracle;
import net.finmath.smartcontract.oracle.ValuationOracle;
import net.finmath.smartcontract.oracle.ValuationOracleSamplePath;

/**
 * @author Christian Fries
 *
 */
public class SmartDerivativeContractMarginingTest {

	@Test
	public void test() {
		LocalDateTime initialTime = LocalDateTime.of(2018, 8, 12, 12, 00);
		LocalDateTime finalTime = LocalDateTime.of(2028, 8, 12, 12, 00);
		int path = 0;

		StochasticValuationOracle stoachasticOracleForValuation = new GeometricBrownianMotionOracle(initialTime);
		ValuationOracle valuationOracle = new ValuationOracleSamplePath(stoachasticOracleForValuation, path);

		StochasticValuationOracle stoachasticOracleForCollateral = new ContinouslyCompoundedBankAccountOracle(initialTime);
		ValuationOracle collateralOracle = new ValuationOracleSamplePath(stoachasticOracleForCollateral, path);
		
		SmartDerivativeContractMargining smartDerivativeContractMargening = new SmartDerivativeContractMargining(valuationOracle, collateralOracle);

		LocalDateTime previousTime = initialTime;
		for(LocalDateTime time = initialTime; time.isBefore(finalTime); time = time.plusDays(1)) {

			double value = smartDerivativeContractMargening.getMargin(previousTime, time).get();
			System.out.println(time.toLocalDate() + "\t" + value);
			previousTime = time;
		}
	}

}
