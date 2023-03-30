package net.finmath.smartcontract.simulation.scenariogeneration;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationDatapoint;

@JsonPropertyOrder({"scenarioDate", "curveKey", "productKey", "maturityKey", "value"})
public class MarketDataItem {

	String scenarioDate;
	String curveKey;
	String productKey;
	String maturityKey;
	Double value;

	public MarketDataItem(String scenarioDate, String curveKey, String productKey, String maturityKey, Double value) {
		this.scenarioDate = scenarioDate;
		this.curveKey = curveKey;
		this.productKey = productKey;
		this.maturityKey = maturityKey;
		this.value = value;
	}

	public CalibrationDatapoint toCalibrationDataPoint() {
		return new CalibrationDatapoint(curveKey, productKey, maturityKey, value);
	}

	public MarketDataItem() {
	}

	public String getScenarioDate() {
		return scenarioDate;
	}

	public void setScenarioDate(String scenarioDate) {
		this.scenarioDate = scenarioDate;
	}

	public String getCurveKey() {
		return curveKey;
	}

	public void setCurveKey(String curveKey) {
		this.curveKey = curveKey;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getMaturityKey() {
		return maturityKey;
	}

	public void setMaturityKey(String maturityKey) {
		this.maturityKey = maturityKey;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
