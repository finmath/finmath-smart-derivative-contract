package net.finmath.smartcontract.valuation.marketdata.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDateTime;

@XmlRootElement
@XmlType(propOrder = {"id","curveKey","productKey","maturityKey","quoteValue","timeStamp"})
@JsonPropertyOrder({ "id","curveKey","productKey","maturityKey","quoteValue","timeStamp" })
public class MarketDataPoint {

	private LocalDateTime timeStamp;
	private String id;
	private String curveKey;
	private String productKey;

	private String maturityKey;
	private Double quoteValue;



	public MarketDataPoint(String id, String curveKey, String productKey, String maturityKey, Double quoteValue, LocalDateTime timeStamp) {
		this.id = id;
		this.curveKey = curveKey;
		this.productKey = productKey;
		this.maturityKey=maturityKey;
		this.quoteValue = quoteValue;
		this.timeStamp = timeStamp;
	}

	public MarketDataPoint(){

	}


	public String getId() {
		return id;
	}

	public String getCurveKey() {
		return curveKey;
	}

	public String getProductKey() {
		return productKey;
	}

	public String getMaturityKey() {
		return maturityKey;
	}

	public Double getQuoteValue() {
		return quoteValue;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCurveKey(String curveKey) {
		this.curveKey = curveKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public void setMaturityKey(String maturityKey) {
		this.maturityKey = maturityKey;
	}

	public void setQuoteValue(Double quoteValue) {
		this.quoteValue = quoteValue;
	}
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}


}
