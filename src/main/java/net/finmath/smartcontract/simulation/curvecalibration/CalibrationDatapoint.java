package net.finmath.smartcontract.simulation.curvecalibration;

/**
 * Contains a single data point used for the calibration of a financial model.
 */
public class CalibrationDatapoint {
	private final String curveName;
	private final String productName;
	private final String maturity;
	private final double quote;

	public CalibrationDatapoint(final String curveName, final String productName, final String maturity, final double quote) {
		this.curveName = curveName;
		this.productName = productName;
		this.maturity = maturity;
		this.quote = quote;
	}

	public String getCurveName() {
		return curveName;
	}

	public String getProductName() {
		return productName;
	}

	public String getMaturity() {
		return maturity;
	}

	public double getQuote() {
		return quote;
	}
}
