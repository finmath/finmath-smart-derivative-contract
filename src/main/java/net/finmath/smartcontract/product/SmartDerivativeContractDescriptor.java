package net.finmath.smartcontract.product;

import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Node;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Descriptor for a smart derivative contract. Unified access to the SDC definition in an XML.
 *
 * @author Christian Fries
 * @see net.finmath.smartcontract.product.xml.SDCXMLParser
 */
public class SmartDerivativeContractDescriptor {

	private final String uniqueTradeIdentifier;
	private final LocalDateTime tradeDate;
	private final List<Party> counterparties;
	private final Map<String, Double> marginAccountInitialByPartyID;
	private final Map<String, Double> penaltyFeeInitialByPartyID;
	private final String recervicePartyID;
	private final Node underlying;
	private final List<CalibrationDataItem.Spec> marketdataItemList;

	/**
	 * Descriptor for a smart derivative contract counterparty. Unified access to a party definition in an XML.
	 */
	public static class Party {

		private final String id;
		private final String name;
		private final String href;
		private final String address;

		public Party(String id, String name, String href, String address) {
			this.id = id;
			this.name = name;
			this.href = href;
			this.address = address;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getHref() {
			return href;
		}

		public String getAddress() {
			return address;
		}

		@Override
		public String toString() {
			return "Party {" +
					"id='" + id + '\'' +
					", name='" + name + '\'' +
					", href='" + href + '\'' +
					", address='" + address + '\'' +
					'}';
		}
	}

	public SmartDerivativeContractDescriptor(String uniqueTradeIdentifier, LocalDateTime tradeDate, List<Party> counterparties, Map<String, Double> marginAccountInitialByPartyID, Map<String, Double> penaltyFeeInitialByPartyID, String recervicePartyID, Node underlying, List<CalibrationDataItem.Spec> marketdataItems) {
		this.uniqueTradeIdentifier = uniqueTradeIdentifier;
		this.tradeDate = tradeDate;
		this.counterparties = counterparties;
		this.marginAccountInitialByPartyID = marginAccountInitialByPartyID;
		this.penaltyFeeInitialByPartyID = penaltyFeeInitialByPartyID;
		this.recervicePartyID = recervicePartyID;
		this.marketdataItemList = marketdataItems;
		this.underlying = underlying;

		Validate.isTrue(counterparties.size() == 2, "Number of counterparties must be 2.");
		Validate.isTrue(marginAccountInitialByPartyID.size() == 2, "Number of margin accounts values must be 2.");
		Validate.isTrue(penaltyFeeInitialByPartyID.size() == 2, "Number of penalty fee values must be 2.");
		Validate.notNull(underlying, "Underlying must not be null.");
	}

	public String getUniqueTradeIdentifier() {
		return uniqueTradeIdentifier;
	}

	public LocalDateTime getTradeDate() {
		return tradeDate;
	}

	public List<Party> getCounterparties() {
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
	}

	public List<CalibrationDataItem.Spec> getMarketdataItemList() {return marketdataItemList;}
}
