package net.finmath.smartcontract.settlement;

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
public class Settlement {

	private String tradeId;

	private String currency;

	private BigDecimal marginValue;

	private List<BigDecimal> marginLimits;

	/// V(T1,M1)

	private ZonedDateTime settlementTime;

	private String marketData;

	private BigDecimal settlementValue;

	/// V(T1,M0)

	private String marketDataPrevious;

	private BigDecimal settlementValuePrevious;

	/// V(T2,M1)

	private ZonedDateTime settlementTimeNext;

	private BigDecimal settlementValueNext;

	private Map<String, String> info;

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
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

	public ZonedDateTime getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(ZonedDateTime settlementTime) {
		this.settlementTime = settlementTime;
	}

	public String getMarketData() {
		return marketData;
	}

	public void setMarketData(String marketData) {
		this.marketData = marketData;
	}

	public BigDecimal getSettlementValue() {
		return settlementValue;
	}

	public void setSettlementValue(BigDecimal settlementValue) {
		this.settlementValue = settlementValue;
	}

	public String getMarketDataPrevious() {
		return marketDataPrevious;
	}

	public void setMarketDataPrevious(String marketDataPrevious) {
		this.marketDataPrevious = marketDataPrevious;
	}

	public BigDecimal getSettlementValuePrevious() {
		return settlementValuePrevious;
	}

	public void setSettlementValuePrevious(BigDecimal settlementValuePrevious) {
		this.settlementValuePrevious = settlementValuePrevious;
	}

	public ZonedDateTime getSettlementTimeNext() {
		return settlementTimeNext;
	}

	public void setSettlementTimeNext(ZonedDateTime settlementTimeNext) {
		this.settlementTimeNext = settlementTimeNext;
	}

	public BigDecimal getSettlementValueNext() {
		return settlementValueNext;
	}

	public void setSettlementValueNext(BigDecimal settlementValueNext) {
		this.settlementValueNext = settlementValueNext;
	}

	public Map<String, String> getInfo() {
		return info;
	}

	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
}

