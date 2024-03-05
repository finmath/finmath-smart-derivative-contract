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

	private String marketData;

	private BigDecimal settlementValue;

	/// V(T1,M0)

	private BigDecimal settlementValuePrevious;

	/// V(T2,M1) - indicative

	private ZonedDateTime settlementTimeNext;

	private BigDecimal settlementValueNext;

	// Custom additional information (e.g. risk figures or szenario values)

	private Map<String, String> info;

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
}

