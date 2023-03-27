//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining a hub or other reference for a physically settled commodity trade.
 * 
 * <p>Java class for CommodityHub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityHub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}PartyAndAccountReferences.model"/&gt;
 *         &lt;element name="hubCode" type="{http://www.fpml.org/FpML-5/confirmation}CommodityHubCode"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommodityHub", propOrder = {
    "partyReference",
    "accountReference",
    "hubCode"
})
public class CommodityHub {

    @XmlElement(required = true)
    protected PartyReference partyReference;
    protected AccountReference accountReference;
    @XmlElement(required = true)
    protected CommodityHubCode hubCode;

    /**
     * Gets the value of the partyReference property.
     * 
     * @return
     *     possible object is
     *     {@link PartyReference }
     *     
     */
    public PartyReference getPartyReference() {
        return partyReference;
    }

    /**
     * Sets the value of the partyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyReference }
     *     
     */
    public void setPartyReference(PartyReference value) {
        this.partyReference = value;
    }

    /**
     * Gets the value of the accountReference property.
     * 
     * @return
     *     possible object is
     *     {@link AccountReference }
     *     
     */
    public AccountReference getAccountReference() {
        return accountReference;
    }

    /**
     * Sets the value of the accountReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountReference }
     *     
     */
    public void setAccountReference(AccountReference value) {
        this.accountReference = value;
    }

    /**
     * Gets the value of the hubCode property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityHubCode }
     *     
     */
    public CommodityHubCode getHubCode() {
        return hubCode;
    }

    /**
     * Sets the value of the hubCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityHubCode }
     *     
     */
    public void setHubCode(CommodityHubCode value) {
        this.hubCode = value;
    }

}