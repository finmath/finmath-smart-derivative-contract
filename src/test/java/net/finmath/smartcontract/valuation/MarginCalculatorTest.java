package net.finmath.smartcontract.valuation;

import net.finmath.smartcontract.client.ValuationClient;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.ValuationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class MarginCalculatorTest {

	@Test
	void test() throws Exception {
		final String marketDataStart = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEnd = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset2.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/smartderivativecontract-sample-swap.xml").readAllBytes(), StandardCharsets.UTF_8);
//		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/vanilla-swap.xml").readAllBytes(), StandardCharsets.UTF_8);

		MarginCalculator marginCalculator = new MarginCalculator();
		ValuationResult valuationResult = marginCalculator.getValue(marketDataStart, marketDataEnd, product);

		Double value = valuationResult.getValue().doubleValue();

		Assertions.assertEquals(952409.716, value, 0.005, "Valuation");
		System.out.println(valuationResult);
	}
}
