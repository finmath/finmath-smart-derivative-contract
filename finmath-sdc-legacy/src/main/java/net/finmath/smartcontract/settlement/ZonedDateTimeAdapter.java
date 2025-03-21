package net.finmath.smartcontract.settlement;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {

	@Override
	public String marshal(ZonedDateTime v) {
		return v.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
	}

	@Override
	public ZonedDateTime unmarshal(String str) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		LocalDateTime ldt = LocalDateTime.parse(str, formatter);
		return ZonedDateTime.of(ldt, ZoneId.systemDefault());
	}
}