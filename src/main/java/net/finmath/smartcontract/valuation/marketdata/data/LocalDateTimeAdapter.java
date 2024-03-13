package net.finmath.smartcontract.valuation.marketdata.data;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {


	@Override
	public String marshal(LocalDateTime v) {
		return v.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
	}

	@Override
	public LocalDateTime unmarshal(String str) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		return LocalDateTime.parse(str, formatter);
	}

}