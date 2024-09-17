package net.finmath.smartcontract.valuation.implementation.reactive;

import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.Function;

//TODO check if needed, not used
public class ConditionalSettlementCalculator implements Function<CalibrationDataset, ValueResult>, Serializable {

	private BigDecimal resultTriggerValue;
	private String sdcXML;
	private String previousmarketdata = null;
	private final MarginCalculator calculator = new MarginCalculator();

	public ConditionalSettlementCalculator(String sdcXML, BigDecimal resultTriggerValue) {
		previousmarketdata = null;
		this.sdcXML = sdcXML;
		this.resultTriggerValue = resultTriggerValue;
	}

	@Override
	public ValueResult apply(CalibrationDataset actualmarketdata) {
		ValueResult defaultResult = new ValueResult();
		defaultResult.setValue(null);
		ValueResult finalResult = defaultResult;
		String marketDataAsJson = actualmarketdata.serializeToJson();
		try {
			if (previousmarketdata != null) {
				ValueResult marginResult = calculator.getValue(marketDataAsJson, sdcXML);
				previousmarketdata = marketDataAsJson;
				finalResult = marginResult;
			} else
				previousmarketdata = marketDataAsJson;

		} catch (Exception e) {
			System.out.println(e);
		}

		return finalResult;
	}
}



