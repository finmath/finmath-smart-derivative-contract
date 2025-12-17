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
    
    // Assumes the fixing is stored in the first entry of quotes
    // TODO google for optimization
    public double[] getQuotesWithoutFixing() {
    	double[] quotesWithoutFixing = new double[this.quotes.length - 1];
    	for (int i = 1; i < quotes.length; i++) {
    		quotesWithoutFixing[i - 1] = this.quotes[i]; 
    	}
    	return quotesWithoutFixing;
    }
    
    // Assumes the fixing is stored in the first entry of quotes
    public double getFixing() {
    	return this.quotes[0];
    }
}


