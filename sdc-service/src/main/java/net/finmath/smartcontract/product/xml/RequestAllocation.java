//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A message requesting that a trade be split among several accounts.
 * 
 * <p>Java class for RequestAllocation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestAllocation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}CorrectableRequestMessage"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="relatedParty" type="{http://www.fpml.org/FpML-5/confirmation}RelatedParty" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="blockTradeIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}TradeIdentifier"/&gt;
 *         &lt;element name="allocations" type="{http://www.fpml.org/FpML-5/confirmation}Allocations"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}PartiesAndAccounts.model"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestAllocation", propOrder = {
    "relatedParty",
    "blockTradeIdentifier",
    "allocations",
    "party",
    "account"
})
public class RequestAllocation
    extends CorrectableRequestMessage
{

    protected List<RelatedParty> relatedParty;
    @XmlElement(required = true)
    protected TradeIdentifier blockTradeIdentifier;
    @XmlElement(required = true)
    protected Allocations allocations;
    @XmlElement(required = true)
    protected List<Party> party;
    protected List<Account> account;

    /**
     * Gets the value of the relatedParty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the relatedParty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedParty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelatedParty }
     * 
     * 
     */
    public List<RelatedParty> getRelatedParty() {
        if (relatedParty == null) {
            relatedParty = new ArrayList<RelatedParty>();
        }
        return this.relatedParty;
    }

    /**
     * Gets the value of the blockTradeIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link TradeIdentifier }
     *     
     */
    public TradeIdentifier getBlockTradeIdentifier() {
        return blockTradeIdentifier;
    }

    /**
     * Sets the value of the blockTradeIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradeIdentifier }
     *     
     */
    public void setBlockTradeIdentifier(TradeIdentifier value) {
        this.blockTradeIdentifier = value;
    }

    /**
     * Gets the value of the allocations property.
     * 
     * @return
     *     possible object is
     *     {@link Allocations }
     *     
     */
    public Allocations getAllocations() {
        return allocations;
    }

    /**
     * Sets the value of the allocations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Allocations }
     *     
     */
    public void setAllocations(Allocations value) {
        this.allocations = value;
    }

    /**
     * Gets the value of the party property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the party property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Party }
     * 
     * 
     */
    public List<Party> getParty() {
        if (party == null) {
            party = new ArrayList<Party>();
        }
        return this.party;
    }

    /**
     * Gets the value of the account property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the account property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Account }
     * 
     * 
     */
    public List<Account> getAccount() {
        if (account == null) {
            account = new ArrayList<Account>();
        }
        return this.account;
    }

}
