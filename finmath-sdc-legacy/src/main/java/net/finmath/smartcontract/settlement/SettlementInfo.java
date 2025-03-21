package net.finmath.smartcontract.settlement;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.math.BigDecimal;

@XmlRootElement(name="settlementInfo")
public class SettlementInfo {

	private String key;
	private BigDecimal value;

	public SettlementInfo() { }

	public SettlementInfo(String key, BigDecimal value) {
		this.key = key;
		this.value = value;
	}

	@XmlAttribute
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlValue
	@XmlJavaTypeAdapter(BigDecimalAdapter.class)
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}