package net.finmath.smartcontract.marketdata.curvecalibration;

import java.util.Objects;

public class CalibrationDataItem {

    public static class Spec{
        private final String key;
        private final String curveName;
        private final String productName;
        private final String maturity;

        public Spec(final String key, final String curveName, final String productName, final String maturity) {
            this.key=key;
            this.curveName = curveName;
            this.productName = productName;
            this.maturity = maturity;

        }


        public String getKey()  { return key;}

        public String getCurveName() {
            return curveName;
        }

        public String getProductName() {
            return productName;
        }

        public String getMaturity() {
            return maturity;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, curveName, productName, maturity);
        }


    }

    final CalibrationDataItem.Spec spec;
    final Double quote;
    final String date;
    final String timestamp;

    public CalibrationDataItem(String curve, String productName, String maturity, Double quote){
        spec = new Spec("",curve,productName,maturity);
        this.quote = quote;
        this.date = "";
        this.timestamp = "";

    }

    public CalibrationDataItem(final CalibrationDataItem.Spec spec, Double quote, String date, String timestamp){
        this.spec=spec;
        this.quote=quote;
        this.date = date;
        this.timestamp = timestamp;
    }


    public CalibrationDataItem getClonedScaled(double factor){
        return new CalibrationDataItem(spec,quote/factor, date, timestamp);
    }

    public CalibrationDataItem getClonedShifted(double amount){
        return new CalibrationDataItem(spec,quote+amount, date, timestamp);
    }

    public CalibrationDataItem.Spec getSpec() {
        return spec;
    }

    public String getCurveName() {
        return getSpec().getCurveName();
    }

    public String getProductName() {
        return getSpec().getProductName();
    }

    public String getMaturity() {
        return getSpec().getMaturity();
    }


    public Double getQuote() {
        return quote;
    }

    public String getDate() {
        return date;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
