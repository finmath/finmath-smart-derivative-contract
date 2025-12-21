package net.finmath.smartcontract.simulation;

import java.time.LocalDate;
import java.util.Arrays;


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
    
    // Assumes the fixing is stored in the first entry of quotes
    public double[] getQuotesWithoutFixing() {
    	 return Arrays.copyOfRange(this.quotes, 1, this.quotes.length);
    }
    
    // Assumes the fixing is stored in the first entry of quotes
    public double getFixing() {
    	return this.quotes[0];
    }
}


