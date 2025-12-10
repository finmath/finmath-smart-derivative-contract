package net.finmath.smartcontract.simulation;

import java.time.LocalDate;


public class MarketDataSnapshot {
    private final LocalDate valuationDate;
    private double[] quotes;

    public MarketDataSnapshot(LocalDate valuationDate, double[] quotes) {
        this.valuationDate = valuationDate;
        this.quotes = quotes;
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public double[] getQuotes() {
        return quotes;
    }
}


