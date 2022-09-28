package net.finmath.smartcontract.product;

import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Node;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SmartDerivativeContractDescriptor {

	private final LocalDateTime tradeDate;
	private final List<SDCXMLParser.Party> counterparties;
	private final Map<String, Double> marginAccountInitialByPartyID;
	private final Map<String, Double> penaltyFeeInitialByPartyID;
	private final String recervicePartyID;
	private final Node underlying;

	public SmartDerivativeContractDescriptor(LocalDateTime tradeDate, List<SDCXMLParser.Party> counterparties, Map<String, Double> marginAccountInitialByPartyID, Map<String, Double> penaltyFeeInitialByPartyID, String recervicePartyID, Node underlying) {
		this.tradeDate = tradeDate;
		this.counterparties = counterparties;
		this.marginAccountInitialByPartyID = marginAccountInitialByPartyID;
		this.penaltyFeeInitialByPartyID = penaltyFeeInitialByPartyID;
		this.recervicePartyID = recervicePartyID;
		this.underlying = underlying;

		Validate.isTrue(counterparties.size() == 2, "Number of counterparties must be 2.");
		Validate.isTrue(marginAccountInitialByPartyID.size() == 2, "Number of margin accounts values must be 2.");
		Validate.isTrue(penaltyFeeInitialByPartyID.size() == 2, "Number of penalty fee values must be 2.");
		Validate.notNull(underlying, "Underlying must not be null.");
	}

	public LocalDateTime getTradeDate() {
		return tradeDate;
	}

	public List<SDCXMLParser.Party> getCounterparties() {
		return counterparties;
	}

	public Double getMarginAccount(String partyID) {
		return marginAccountInitialByPartyID.get(partyID);
	}

	/**
	 * Get the penalty fee for the party.
	 *
	 * @param partyID The party ID as string.
	 * @return The penalty fee.
	 */
	public Double getPenaltyFee(String partyID) {
		return penaltyFeeInitialByPartyID.get(partyID);
	}

	/**
	 * Get the FPML XML node describing the underlying.
	 *
	 * @return The Node describing the underlying.
	 */
	public Node getUnderlying() {
		return underlying;
	}

	/**
	 * A positive value of the underlying represents a claim for the partyID returned by this method and a liability of the other party.
	 *
	 * @return The party ID of the receiver party.
	 */
	public String getUnderlyingReceiverPartyID() {
		return recervicePartyID;
;
	}
}
