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
 * A type defining the specification of settlement terms, occuring when the settlement currency is different to the notional currency of the trade.
 * 
 * <p>Java class for SettlementProvision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SettlementProvision"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="settlementCurrency" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;element name="nonDeliverableSettlement" type="{http://www.fpml.org/FpML-5/confirmation}NonDeliverableSettlement" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SettlementProvision", propOrder = {
    "settlementCurrency",
    "nonDeliverableSettlement"
})
public class SettlementProvision {

    @XmlElement(required = true)
    protected Currency settlementCurrency;
    protected NonDeliverableSettlement nonDeliverableSettlement;

    /**
     * Gets the value of the settlementCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getSettlementCurrency() {
        return settlementCurrency;
    }

    /**
     * Sets the value of the settlementCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setSettlementCurrency(Currency value) {
        this.settlementCurrency = value;
    }

    /**
     * Gets the value of the nonDeliverableSettlement property.
     * 
     * @return
     *     possible object is
     *     {@link NonDeliverableSettlement }
     *     
     */
    public NonDeliverableSettlement getNonDeliverableSettlement() {
        return nonDeliverableSettlement;
    }

    /**
     * Sets the value of the nonDeliverableSettlement property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonDeliverableSettlement }
     *     
     */
    public void setNonDeliverableSettlement(NonDeliverableSettlement value) {
        this.nonDeliverableSettlement = value;
    }

}