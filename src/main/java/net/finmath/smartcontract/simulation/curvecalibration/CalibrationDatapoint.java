package net.finmath.smartcontract.simulation.curvecalibration;

/**
 * Contains a single data point used for the calibration of a financial model.
 */
public class CalibrationDatapoint {
    private String curveName;
    private String productName;
    private String maturity;
    private double quote;

    public CalibrationDatapoint(String curveName,String productName, String maturity, double quote) {
        this.curveName=curveName;
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
