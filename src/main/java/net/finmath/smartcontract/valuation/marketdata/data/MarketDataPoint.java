package net.finmath.smartcontract.valuation.marketdata.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDateTime;

@XmlRootElement
@XmlType(propOrder = {"id","value","timeStamp"})
@JsonPropertyOrder({ "id","value","timeStamp" })
public class MarketDataPoint {

	private LocalDateTime timeStamp;
	private String id;


	private Double value;



	public MarketDataPoint(String id, Double value, LocalDateTime timeStamp) {
		this.id = id;
		this.value = value;
		this.timeStamp = timeStamp;
	}

	public MarketDataPoint(){

	}


	public String getId() {
		return id;
	}



	public Double getValue() {
		return value;
	}

	public void setId(String id) {
		this.id = id;
	}



	public void setValue(Double value) {
		this.value = value;
	}
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}


}
