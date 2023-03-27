//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FxKnockoutCount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxKnockoutCount"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="conditionalFixings" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="settlementAtKnockout" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxKnockoutCount", propOrder = {
    "conditionalFixings",
    "settlementAtKnockout"
})
public class FxKnockoutCount {

    @XmlElement(required = true)
    protected BigInteger conditionalFixings;
    protected boolean settlementAtKnockout;

    /**
     * Gets the value of the conditionalFixings property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getConditionalFixings() {
        return conditionalFixings;
    }

    /**
     * Sets the value of the conditionalFixings property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setConditionalFixings(BigInteger value) {
        this.conditionalFixings = value;
    }

    /**
     * Gets the value of the settlementAtKnockout property.
     * 
     */
    public boolean isSettlementAtKnockout() {
        return settlementAtKnockout;
    }

    /**
     * Sets the value of the settlementAtKnockout property.
     * 
     */
    public void setSettlementAtKnockout(boolean value) {
        this.settlementAtKnockout = value;
    }

}