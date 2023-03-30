package net.finmath.smartcontract.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.ZonedDateTime;

@JsonTypeName("tradeDescriptor")
public class TradeDescriptor {
    @JsonProperty("firstCounterparty")
    private String firstCounterparty;
    @JsonProperty("secondCounterparty")
    private String secondCounterparty;
    @JsonProperty("marginBufferAmount")
    private float marginBufferAmount;
    @JsonProperty("terminationFeeAmount")
    private float terminationFeeAmount;
    @JsonProperty("notionalAmount")
    private float notionalAmount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("tradeDate")
    private ZonedDateTime tradeDate;
    @JsonProperty("effectiveDate")
    private ZonedDateTime effectiveDate;
    @JsonProperty("terminationDate")
    private ZonedDateTime terminationDate;
    @JsonProperty("fixedPayingParty")
    private String fixedPayingParty;
    @JsonProperty("fixedRate")
    private float fixedRate;
    @JsonProperty("fixedDayCountFraction")
    private String fixedDayCountFraction;
    @JsonProperty("floatingPayingParty")
    private String floatingPayingParty;
    @JsonProperty("floatingRateIndex")
    private String floatingRateIndex;
    @JsonProperty("floatingDayCountFraction")
    private String floatingDayCountFraction;
    @JsonProperty("floatingFixingDayOffset")
    private String floatingFixingDayOffset;
    @JsonProperty("floatingPaymentFrequency")
    private String floatingPaymentFrequency;


    // Getter Methods

    public String getFirstCounterparty() {
        return firstCounterparty;
    }

    public String getSecondCounterparty() {
        return secondCounterparty;
    }

    public float getMarginBufferAmount() {
        return marginBufferAmount;
    }

    public float getTerminationFeeAmount() {
        return terminationFeeAmount;
    }

    public float getNotionalAmount() {
        return notionalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public ZonedDateTime getTradeDate() {
        return tradeDate;
    }

    public ZonedDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public ZonedDateTime getTerminationDate() {
        return terminationDate;
    }

    public String getFixedPayingParty() {
        return fixedPayingParty;
    }

    public float getFixedRate() {
        return fixedRate;
    }

    public String getFixedDayCountFraction() {
        return fixedDayCountFraction;
    }

    public String getFloatingPayingParty() {
        return floatingPayingParty;
    }

    public String getFloatingRateIndex() {
        return floatingRateIndex;
    }

    public String getFloatingDayCountFraction() {
        return floatingDayCountFraction;
    }

    public String getFloatingFixingDayOffset() {
        return floatingFixingDayOffset;
    }

    public String getFloatingPaymentFrequency() {
        return floatingPaymentFrequency;
    }


    @Override
    public String toString() {
        return "TradeDescriptor{" +
               "firstCounterparty='" + firstCounterparty + '\'' +
               ", secondCounterparty='" + secondCounterparty + '\'' +
               ", marginBufferAmount=" + marginBufferAmount +
               ", terminationFeeAmount=" + terminationFeeAmount +
               ", notionalAmount=" + notionalAmount +
               ", currency='" + currency + '\'' +
               ", tradeDate=" + tradeDate +
               ", effectiveDate=" + effectiveDate +
               ", terminationDate=" + terminationDate +
               ", fixedPayingParty='" + fixedPayingParty + '\'' +
               ", fixedRate=" + fixedRate +
               ", fixedDayCountFraction='" + fixedDayCountFraction + '\'' +
               ", floatingPayingParty='" + floatingPayingParty + '\'' +
               ", floatingRateIndex='" + floatingRateIndex + '\'' +
               ", floatingDayCountFraction='" + floatingDayCountFraction +
               '\'' +
               ", floatingFixingDayOffset='" + floatingFixingDayOffset + '\'' +
               ", floatingPaymentFrequency='" + floatingPaymentFrequency +
               '\'' +
               '}';
    }
}