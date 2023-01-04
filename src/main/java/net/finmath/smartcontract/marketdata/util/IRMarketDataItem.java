package net.finmath.smartcontract.marketdata.util;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDatapoint;

import java.util.Objects;

@JsonPropertyOrder({"ric", "curve", "type", "tenor","value", "date","timestamp"})
public class IRMarketDataItem {
    private String ric;
    private String curve;
    private String type;
    private String tenor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IRMarketDataItem that = (IRMarketDataItem) o;
        return Objects.equals(ric, that.ric) && Objects.equals(curve, that.curve) && Objects.equals(type, that.type) && Objects.equals(tenor, that.tenor) && Objects.equals(date, that.date) && Objects.equals(timestamp, that.timestamp) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ric, curve, type, tenor, date, timestamp, value);
    }

    private String date;
    private String timestamp;
    private Double value;

    public IRMarketDataItem() {
    }

    public IRMarketDataItem(String ric, String curve, String type, String tenor, String date, String timestamp, Double value) {
        this.ric = ric;
        this.curve = curve;
        this.type = type;
        this.tenor = tenor;
        this.date = date;
        this.timestamp = timestamp;
        this.value = value;
    }

    public CalibrationDatapoint toCalibrationDataPoint(double quote){
        return new CalibrationDatapoint(curve,type,tenor,quote);
    }

    public String getRic() {
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public String getCurve() {
        return curve;
    }

    public void setCurve(String curve) {
        this.curve = curve;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
