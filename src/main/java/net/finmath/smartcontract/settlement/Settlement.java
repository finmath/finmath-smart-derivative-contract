package net.finmath.smartcontract.settlement;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.finmath.smartcontract.model.MarketDataList;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Describes the result of a single settlement as reported by the valuation oracle.
 * This data is intended to be archived (e.g. on the DLT).
 * <p>
 * The margin value is defined as the difference of two valuations: V(T1,M1) - V(T1,M0)
 * Here T1 is this settlements settlement time, M1 ist this settlements market data and M0 is the previous settlement market data.
 * <p>
 * For convenience the valuation oracle may also provide the value V(T2,M1) - the valuation at the next settlement time.
 *
 * @author Christian Fries
 */
//@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType(propOrder = {"tradeId", "settlementType", "currency", "marginValue",
		"marginLimits", "settlementTime", "settlementNPV", "settlementNPVPrevious",
		"settlementTimeNext", "settlementNPVNext", "marketData", "info"})
public class Settlement {

	public Settlement() {
	}

	public enum SettlementType {
		INITIAL,
		REGULAR,
		TERMINAL
	}

	private String tradeId;

	private SettlementType settlementType;

	private String currency;

	private BigDecimal marginValue;

	private List<BigDecimal> marginLimits;

	/// V(T1,M1)

	private ZonedDateTime settlementTime;

	private BigDecimal settlementNPV;

	/// V(T1,M0)
	private BigDecimal settlementNPVPrevious;

	/// V(T2,M1) - indicative

	private ZonedDateTime settlementTimeNext;

	private BigDecimal settlementNPVNext;

	private MarketDataList marketData;

	/*
	 * Custom additional information (e.g. risk figures or szenario values)
	 */
	private Map<String, Double> info;

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public SettlementType getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(SettlementType settlementType) {
		this.settlementType = settlementType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getMarginValue() {
		return marginValue;
	}

	public void setMarginValue(BigDecimal marginValue) {
		this.marginValue = marginValue;
	}

	public List<BigDecimal> getMarginLimits() {
		return marginLimits;
	}

	public void setMarginLimits(List<BigDecimal> marginLimits) {
		this.marginLimits = marginLimits;
	}

	@XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
	public ZonedDateTime getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(ZonedDateTime settlementTime) {
		this.settlementTime = settlementTime;
	}

	public MarketDataList getMarketData() {
		return marketData;
	}

	public void setMarketData(MarketDataList marketData) {
		this.marketData = marketData;
	}

	public Map<String, Double> getInfo() {
		return info;
	}

	public void setInfo(Map<String, Double> info) {
		this.info = info;
	}

	public BigDecimal getSettlementNPV() {
		return settlementNPV;
	}

	public void setSettlementNPV(BigDecimal settlementNPV) {
		this.settlementNPV = settlementNPV;
	}

	public BigDecimal getSettlementNPVPrevious() {
		return settlementNPVPrevious;
	}

	public void setSettlementNPVPrevious(BigDecimal settlementNPVPrevious) {
		this.settlementNPVPrevious = settlementNPVPrevious;
	}

	public Settlement(String tradeId, SettlementType settlementType, String currency, BigDecimal marginValue, List<BigDecimal> marginLimits, ZonedDateTime settlementTime, MarketDataList marketData, BigDecimal settlementNPV, BigDecimal settlementNPVPrevious, ZonedDateTime settlementTimeNext, BigDecimal settlementNPVNext) {
		this.tradeId = tradeId;
		this.settlementType = settlementType;
		this.currency = currency;
		this.marginValue = marginValue;
		this.marginLimits = marginLimits;
		this.settlementTime = settlementTime;
		this.marketData = marketData;
		this.settlementNPV = settlementNPV;
		this.settlementNPVPrevious = settlementNPVPrevious;
		this.settlementTimeNext = settlementTimeNext;
		this.settlementNPVNext = settlementNPVNext;
	}

	@XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
	public ZonedDateTime getSettlementTimeNext() {
		return settlementTimeNext;
	}

	public void setSettlementTimeNext(ZonedDateTime settlementTimeNext) {
		this.settlementTimeNext = settlementTimeNext;
	}

	public BigDecimal getSettlementNPVNext() {
		return settlementNPVNext;
	}

	public void setSettlementNPVNext(BigDecimal settlementNPVNext) {
		this.settlementNPVNext = settlementNPVNext;
	}
}

