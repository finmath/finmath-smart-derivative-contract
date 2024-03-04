package net.finmath.smartcontract.valuation.reactive;

import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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
				String actualTime = CalibrationParserDataItems.getScenariosFromJsonString(marketDataAsJson).get(0).getDate().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
				ValueResult marginResult = calculator.getValue(marketDataAsJson, sdcXML);
				//if (Math.abs(marginResult.getValue().doubleValue()) > resultTriggerValue.doubleValue()) {
				String previousTime = CalibrationParserDataItems.getScenariosFromJsonString(previousmarketdata).get(0).getDate().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
				previousmarketdata = marketDataAsJson;
				finalResult = marginResult;
//                    System.out.println("ConditionalMarginCalculator: PreviousTime: " + previousTime + " - ActualTime: " + actualTime + " - SettlementValue: " + marginResult.getValue().doubleValue() );
				//}
			} else
				previousmarketdata = marketDataAsJson;

		} catch (Exception e) {
			System.out.println(e);
		}

		return finalResult;
	}
}



