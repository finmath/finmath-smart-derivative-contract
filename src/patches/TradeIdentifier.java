package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A type defining a trade identifier issued by the indicated party.
 *
 * <p>Java class for TradeIdentifier complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TradeIdentifier"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}IssuerTradeId.model"/&gt;
 *           &lt;sequence&gt;
 *             &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}PartyAndAccountReferences.model"/&gt;
 *             &lt;choice maxOccurs="unbounded"&gt;
 *               &lt;element name="tradeId" type="{http://www.fpml.org/FpML-5/confirmation}TradeId"/&gt;
 *               &lt;element name="versionedTradeId" type="{http://www.fpml.org/FpML-5/confirmation}VersionedTradeId"/&gt;
 *             &lt;/choice&gt;
 *           &lt;/sequence&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * <p>
 * <p>
 * This PATCH is required, because the JAXB maven pugin has an an issue with the propOder - see https://stackoverflow.com/questions/16189531/jaxb-property-order
 * This is due to the definition of TradeIdentifier - https://www.fpml.org/spec/fpml-5-2-4-tr-1/html/confirmation/schemaDocumentation/schemas/fpml-doc-5-2_xsd/complexTypes/PartyTradeIdentifier.html
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TradeIdentifier", propOrder = {
		"issuer",
		"partyReference",
		"tradeId",
		"accountReference",
		"tradeIdOrVersionedTradeId"
})
@XmlSeeAlso({
		PartyTradeIdentifier.class,
		TradeIdentifierExtended.class
})
public class TradeIdentifier {

	protected IssuerId issuer;
	protected TradeId tradeId;
	protected PartyReference partyReference;
	protected AccountReference accountReference;
	@XmlElements({
			@XmlElement(name = "tradeId", type = TradeId.class),
			@XmlElement(name = "versionedTradeId", type = VersionedTradeId.class)
	})
	protected List<Object> tradeIdOrVersionedTradeId;
	@XmlAttribute(name = "id")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;

	/**
	 * Gets the value of the issuer property.
	 *
	 * @return possible object is
	 * {@link IssuerId }
	 */
	public IssuerId getIssuer() {
		return issuer;
	}

	/**
	 * Sets the value of the issuer property.
	 *
	 * @param value allowed object is
	 *              {@link IssuerId }
	 */
	public void setIssuer(IssuerId value) {
		this.issuer = value;
	}

	/**
	 * Gets the value of the tradeId property.
	 *
	 * @return possible object is
	 * {@link TradeId }
	 */
	public TradeId getTradeId() {
		return tradeId;
	}

	/**
	 * Sets the value of the tradeId property.
	 *
	 * @param value allowed object is
	 *              {@link TradeId }
	 */
	public void setTradeId(TradeId value) {
		this.tradeId = value;
	}

	/**
	 * Gets the value of the partyReference property.
	 *
	 * @return possible object is
	 * {@link PartyReference }
	 */
	public PartyReference getPartyReference() {
		return partyReference;
	}

	/**
	 * Sets the value of the partyReference property.
	 *
	 * @param value allowed object is
	 *              {@link PartyReference }
	 */
	public void setPartyReference(PartyReference value) {
		this.partyReference = value;
	}

	/**
	 * Gets the value of the accountReference property.
	 *
	 * @return possible object is
	 * {@link AccountReference }
	 */
	public AccountReference getAccountReference() {
		return accountReference;
	}

	/**
	 * Sets the value of the accountReference property.
	 *
	 * @param value allowed object is
	 *              {@link AccountReference }
	 */
	public void setAccountReference(AccountReference value) {
		this.accountReference = value;
	}

	/**
	 * Gets the value of the tradeIdOrVersionedTradeId property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the Jakarta XML Binding object.
	 * This is why there is not a <CODE>set</CODE> method for the tradeIdOrVersionedTradeId property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getTradeIdOrVersionedTradeId().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TradeId }
	 * {@link VersionedTradeId }
	 */
	public List<Object> getTradeIdOrVersionedTradeId() {
		if (tradeIdOrVersionedTradeId == null) {
			tradeIdOrVersionedTradeId = new ArrayList<Object>();
		}
		return this.tradeIdOrVersionedTradeId;
	}

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setId(String value) {
		this.id = value;
	}

}
