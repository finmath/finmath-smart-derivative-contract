package net.finmath.smartcontract.reactive;

import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataSet;
import net.finmath.smartcontract.marketdata.util.CalibrationItemParser;
import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.valuation.MarginCalculator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Function;

public class ConditionalSettlementCalculator implements Function<CalibrationDataSet, MarginResult>, Serializable {

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
    public MarginResult apply(CalibrationDataSet actualmarketdata) {
        MarginResult defaultResult = new MarginResult();
        defaultResult.setValue(null);
        MarginResult finalResult = defaultResult;
        String marketDataAsJson = actualmarketdata.serializeToJson();
        try {
            if (previousmarketdata != null) {
                String actualTime = CalibrationItemParser.getScenariosFromJsonString(marketDataAsJson).get(0).getDate().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
                MarginResult marginResult = calculator.getValue(previousmarketdata, marketDataAsJson, sdcXML);
                if (Math.abs(marginResult.getValue().doubleValue()) > resultTriggerValue.doubleValue()) {
                    String previousTime = CalibrationItemParser.getScenariosFromJsonString(previousmarketdata).get(0).getDate().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
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



