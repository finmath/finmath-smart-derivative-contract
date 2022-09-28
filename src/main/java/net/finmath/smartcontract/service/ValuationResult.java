package net.finmath.smartcontract.service;

import java.time.LocalDateTime;

/**
 * Simple DTO representing the result of a valuation.
 *
 * @author Christian Fries
 */
public class ValuationResult {
	private Double value;
	private String currency;
	private String valuationDate;

	public ValuationResult(Double value, String currency, String valuationDate) {
		this.value = value;
		this.currency = currency;
		this.valuationDate = valuationDate;
	}

	public Double getValue() {
		return value;
	}

	public String getCurrency() {
		return currency;
	}

	public String getValuationDate() {
		return valuationDate;
	}
}
