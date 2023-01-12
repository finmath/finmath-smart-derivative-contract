package net.finmath.smartcontract.reactive;

import io.reactivex.rxjava3.functions.Predicate;
import net.finmath.smartcontract.model.MarginResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;

public class FilterMarginResultOrTime implements Predicate<MarginResult> {
    private LocalDateTime settlementTriggerTime;
    private Period period;
    private BigDecimal settlementTriggerValue;

    public FilterMarginResultOrTime(LocalDateTime settlementTriggerTime, Period settlementPeriod, BigDecimal settlementTriggerValue) {
        this.settlementTriggerTime = settlementTriggerTime;
        this.settlementTriggerValue = settlementTriggerValue;
        Period period = settlementPeriod;
    }

    @Override
    public boolean test(MarginResult marginResult) throws Throwable {

        /* If settlement trigger value is set and settlement value is null then return false */
        if (settlementTriggerValue != null && marginResult.getValue()==null)
            return false;

        /* If settlement trigger value is set and settlement value exceeds treshold */
        if ( settlementTriggerValue != null && Math.abs(marginResult.getValue().doubleValue()) > settlementTriggerValue.doubleValue())
            return true;

        /* Otherwise if whe are after next settlement time */
        if ( settlementTriggerTime != null && LocalDateTime.now().isAfter(settlementTriggerTime)){
            settlementTriggerTime.plus(period);
            return true;
        }

        /* All other return false*/
        return false;

    }
}
