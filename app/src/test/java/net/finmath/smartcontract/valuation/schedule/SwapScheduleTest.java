package net.finmath.smartcontract.valuation.schedule;

import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.flowschedule.FlowScheduleSwap;
import net.finmath.smartcontract.product.flowschedule.FlowScheduleSwapLeg;
import net.finmath.smartcontract.product.flowschedule.FlowScheduleSwapLegPeriod;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;
import net.finmath.smartcontract.valuation.implementation.FlowScheduleCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Test class to check if the sum of the decomposed swap, i.e. a swap with N periods
 * decomposed into N single period swaps, has the same value as the original swap.
 */
public class SwapScheduleTest {

	@Test
	void testSwapScheduleGeneration() throws Exception {
		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_settlement_oracle_refinement.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String productData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_oracle_refinement.xml").readAllBytes(), StandardCharsets.UTF_8);

		FlowScheduleCalculator flowScheduleCalculator = new FlowScheduleCalculator();
		FlowScheduleSwap flowScheduleSwap = flowScheduleCalculator.getFlowScheduleSwap(productData, marketData);
		List<FlowScheduleSwapLeg> flowScheduleSwapLegs = flowScheduleSwap.getFlowScheduleSwapLegs();
		double valueFlowScheduleCalculator = 0.0;
		for (FlowScheduleSwapLeg flowScheduleSwapLeg : flowScheduleSwapLegs) {
			List<FlowScheduleSwapLegPeriod> flowScheduleSwapLegPeriods = flowScheduleSwapLeg.getFlowScheduleSwapLegPeriods();
			for (FlowScheduleSwapLegPeriod flowScheduleSwapLegPeriod : flowScheduleSwapLegPeriods) {
				valueFlowScheduleCalculator += flowScheduleSwapLegPeriod.getNpv();
			}
		}

		MarginCalculator marginCalculator = new MarginCalculator();
		ValueResult valuationResult = marginCalculator.getValue(marketData, productData);
		double valueMarginCalculator = valuationResult.getValue().doubleValue();
		Assertions.assertEquals(valueFlowScheduleCalculator, valueMarginCalculator,0.01, "Sum of single periods: " + valueFlowScheduleCalculator + "; Value margin calculator: " + valueMarginCalculator);

	}



}
