package net.finmath.smartcontract.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.data.LocalDateTimeAdapter;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlType(propOrder = {"requestTimeStamp","points"})
public class MarketDataList {
	List<MarketDataPoint> points;
	LocalDateTime requestTimeStamp;

	public MarketDataList(){
		requestTimeStamp = LocalDateTime.now();
		points = new ArrayList<>();
	}

	public void setRequestTimeStamp(LocalDateTime timestamp) { this.requestTimeStamp = timestamp; }

	public void add(MarketDataPoint point){
		this.points.add(point);
	}

	public void setPoints(List<MarketDataPoint> points) {
		this.points = points;
	}

	public int getSize(){
		return this.points.size();
	}

	@XmlElement(name = "item")
	public List<MarketDataPoint> getPoints() {
		return points;
	}

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getRequestTimeStamp() {
		return requestTimeStamp;
	}

	public String serializeToJson() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.INDENT_OUTPUT, true)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		try {
			return mapper.writerFor(MarketDataList.class).writeValueAsString(this);
		}
		catch (Exception e){
			return "";
		}
	}

	public CalibrationDataset mapToCalibrationDataSet(){
		return null;
	}

	@Override
	public String toString() {
		return "MarketDataList{" +
				"points=" + points +
				", requestTimeStamp=" + requestTimeStamp +
				'}';
	}
}
