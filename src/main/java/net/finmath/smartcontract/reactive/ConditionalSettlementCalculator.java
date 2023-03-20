package net.finmath.smartcontract.reactive;

import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class ConditionalSettlementCalculator implements Function<CalibrationDataset, MarginResult>, Serializable {

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
    public MarginResult apply(CalibrationDataset actualmarketdata) {
        MarginResult defaultResult = new MarginResult();
        defaultResult.setValue(null);
        MarginResult finalResult = defaultResult;
        String marketDataAsJson = actualmarketdata.serializeToJson();
        try {
            if (previousmarketdata != null) {
                String actualTime = CalibrationParserDataItems.getScenariosFromJsonString(marketDataAsJson).get(0).getDate().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
                MarginResult marginResult = calculator.getValue(previousmarketdata, marketDataAsJson, sdcXML);
                if (Math.abs(marginResult.getValue().doubleValue()) > resultTriggerValue.doubleValue()) {
                    String previousTime = CalibrationParserDataItems.getScenariosFromJsonString(previousmarketdata).get(0).getDate().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
                    previousmarketdata = marketDataAsJson;
                    finalResult = marginResult;
                    System.out.println("ConditionalMarginCalculator: PreviousTime: " + previousTime + " - ActualTime: " + actualTime + " - SettlementValue: " + marginResult.getValue().doubleValue() );
                }
            }
            else
                previousmarketdata = marketDataAsJson;

        }
        catch (Exception e){
            System.out.println(e);
        }

        return finalResult;
    }
}



