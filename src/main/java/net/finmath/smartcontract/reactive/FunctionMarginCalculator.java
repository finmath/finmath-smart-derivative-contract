package net.finmath.smartcontract.reactive;

import net.finmath.smartcontract.marketdata.util.IRMarketDataParser;
import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class FunctionMarginCalculator implements Function<String, MarginResult>, Serializable {

    private BigDecimal resultTriggerValue;
    private String sdcXML;
    private String previousmarketdata = null;
    private final MarginCalculator calculator = new MarginCalculator();

    public FunctionMarginCalculator(String sdcXML, BigDecimal resultTriggerValue) {
        previousmarketdata = null;
        this.sdcXML = sdcXML;
        this.resultTriggerValue = resultTriggerValue;
    }

    @Override
    public MarginResult apply(String actualmarketdata) {
        MarginResult defaultResult = new MarginResult();
        defaultResult.setValue(null);
        MarginResult finalResult = defaultResult;
        try {
            if (previousmarketdata != null) {
                String actualTime = IRMarketDataParser.getScenariosFromJsonString(actualmarketdata).get(0).getDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                ValueResult valueResult = calculator.getValue(actualmarketdata, sdcXML);
                System.out.println("ActualTime: " + actualTime + " - Value: " + valueResult.getValue().doubleValue());
                MarginResult marginResult = calculator.getValue(previousmarketdata, actualmarketdata, sdcXML);
                if (Math.abs(marginResult.getValue().doubleValue()) > resultTriggerValue.doubleValue()) {
                    String previousTime = IRMarketDataParser.getScenariosFromJsonString(previousmarketdata).get(0).getDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    previousmarketdata = actualmarketdata;
                    System.out.print("PreviousTime: " + previousTime + " - ActualTime: " + actualTime + " - ");
                      finalResult = marginResult;
                }
            }
            else
                previousmarketdata = actualmarketdata;

        }
        catch (Exception e){
        }

        return finalResult;
    }
}



