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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type that contains full details of a predefined fixed payout which may occur (or not) in a Barrier Option or Digital Option when a trigger event occurs (or not).
 * 
 * <p>Java class for FxOptionPayout complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxOptionPayout"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}NonNegativeMoney"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="payoutStyle" type="{http://www.fpml.org/FpML-5/confirmation}PayoutEnum"/&gt;
 *         &lt;element name="settlementInformation" type="{http://www.fpml.org/FpML-5/confirmation}SettlementInformation" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxOptionPayout", propOrder = {
    "payoutStyle",
    "settlementInformation"
})
public class FxOptionPayout
    extends NonNegativeMoney
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected PayoutEnum payoutStyle;
    protected SettlementInformation settlementInformation;

    /**
     * Gets the value of the payoutStyle property.
     * 
     * @return
     *     possible object is
     *     {@link PayoutEnum }
     *     
     */
    public PayoutEnum getPayoutStyle() {
        return payoutStyle;
    }

    /**
     * Sets the value of the payoutStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayoutEnum }
     *     
     */
    public void setPayoutStyle(PayoutEnum value) {
        this.payoutStyle = value;
    }

    /**
     * Gets the value of the settlementInformation property.
     * 
     * @return
     *     possible object is
     *     {@link SettlementInformation }
     *     
     */
    public SettlementInformation getSettlementInformation() {
        return settlementInformation;
    }

    /**
     * Sets the value of the settlementInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementInformation }
     *     
     */
    public void setSettlementInformation(SettlementInformation value) {
        this.settlementInformation = value;
    }

}
