package net.finmath.smartcontract.product.flowschedule;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;

import java.util.List;


/**
 * Object for structuring the flow schedule of a swap, that is the underlying of a Smart Derivative Contract, as an XML string.
 * Contains information from the parent SDC and a list of {@link FlowScheduleSwapLeg},
 * each representing the flow schedule of a swap leg.
 *
 * @author  Raphael Prandtl
 */
@XmlRootElement(name = "flowScheduleSwap")
@XmlType(name = "", propOrder = {
		"dltTradeId",
		"dltAddress",
		"uniqueTradeIdentifier",
		"settlementCurrency",
		"tradeType",
		"parties",
		"receiverPartyID",
		"flowScheduleSwapLegs"
})
public class FlowScheduleSwap {
	String dltTradeId;
	String dltAddress;
	String uniqueTradeIdentifier;
	String settlementCurrency;
	String tradeType;
	Smartderivativecontract.Parties parties;
	String receiverPartyID;
	List<FlowScheduleSwapLeg> flowScheduleSwapLegs;


	@XmlElement(name = "dltTradeId")
	public String getDltTradeId() {
		return dltTradeId;
	}

	public void setDltTradeId(final String dltTradeId) {
		this.dltTradeId = dltTradeId;
	}

	@XmlElement(name = "dltAddress")
	public String getDltAddress() {
		return dltAddress;
	}

	public void setDltAddress(final String dltAddress) {
		this.dltAddress = dltAddress;
	}


	@XmlElement(name = "uniqueTradeIdentifier")
	public String getUniqueTradeIdentifier() {
		return uniqueTradeIdentifier;
	}

	public void setUniqueTradeIdentifier(final String uniqueTradeIdentifier) {
		this.uniqueTradeIdentifier = uniqueTradeIdentifier;
	}

	@XmlElement(name = "settlementCurrency")
	public String getSettlementCurrency() {
		return settlementCurrency;
	}

	public void setSettlementCurrency(final String settlementCurrency) {
		this.settlementCurrency = settlementCurrency;
	}

	@XmlElement(name = "tradeType")
	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(final String tradeType) {
		this.tradeType = tradeType;
	}

	@XmlElement(name = "parties")
	public Smartderivativecontract.Parties getParties() {
		return parties;
	}

	public void setParties(final Smartderivativecontract.Parties parties) {
		this.parties = parties;
	}

	@XmlElement(name = "receiverPartyID")
	public String getReceiverPartyID() {
		return receiverPartyID;
	}

	public void setReceiverPartyID(final String receiverPartyID) {
		this.receiverPartyID = receiverPartyID;
	}

	@XmlElementWrapper(name = "flowScheduleSwapLegs")
	@XmlElement(name = "flowScheduleSwapLeg")
	public List<FlowScheduleSwapLeg> getFlowScheduleSwapLegs() {
		return flowScheduleSwapLegs;
	}

	public void setFlowScheduleSwapLegs(List<FlowScheduleSwapLeg> flowScheduleSwapLegs) {
		this.flowScheduleSwapLegs = flowScheduleSwapLegs;
	}

}
