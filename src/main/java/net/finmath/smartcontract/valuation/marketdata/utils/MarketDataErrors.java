package net.finmath.smartcontract.valuation.marketdata.utils;

import java.util.ArrayList;
import java.util.List;

public class MarketDataErrors {

	private final boolean hasErrors;
	private List<String> missingDataPoints = new ArrayList<>();
	private String errorMessage;

	public MarketDataErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public List<String> getMissingDataPoints() {
		return missingDataPoints;
	}

	public void setMissingDataPoints(List<String> missingDataPoints) {
		this.missingDataPoints = missingDataPoints;
	}

	public void addMissingData(String missingDataPoint) {
		this.missingDataPoints.add(missingDataPoint);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "MarketDataErrors{" +
				"hasErrors=" + hasErrors +
				", missingDataPoints=" + missingDataPoints +
				", errorMessage='" + errorMessage + '\'' +
				'}';
	}
}
