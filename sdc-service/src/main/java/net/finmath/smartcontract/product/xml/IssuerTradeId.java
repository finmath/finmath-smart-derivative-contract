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
 * A complex type for a two part identifier such as a USI.
 * 
 * <p>Java class for IssuerTradeId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IssuerTradeId"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}IssuerTradeId.model"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IssuerTradeId", propOrder = {
    "issuer",
    "tradeId"
})
public class IssuerTradeId {

    @XmlElement(required = true)
    protected IssuerId issuer;
    @XmlElement(required = true)
    protected TradeId tradeId;

    /**
     * Gets the value of the issuer property.
     * 
     * @return
     *     possible object is
     *     {@link IssuerId }
     *     
     */
    public IssuerId getIssuer() {
        return issuer;
    }

    /**
     * Sets the value of the issuer property.
     * 
     * @param value
     *     allowed object is
     *     {@link IssuerId }
     *     
     */
    public void setIssuer(IssuerId value) {
        this.issuer = value;
    }

    /**
     * Gets the value of the tradeId property.
     * 
     * @return
     *     possible object is
     *     {@link TradeId }
     *     
     */
    public TradeId getTradeId() {
        return tradeId;
    }

    /**
     * Sets the value of the tradeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradeId }
     *     
     */
    public void setTradeId(TradeId value) {
        this.tradeId = value;
    }

}