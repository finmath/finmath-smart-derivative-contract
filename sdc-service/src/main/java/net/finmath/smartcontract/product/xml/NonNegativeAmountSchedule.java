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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining a currency amount or a currency amount schedule.
 * 
 * <p>Java class for NonNegativeAmountSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NonNegativeAmountSchedule"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}NonNegativeSchedule"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="currency" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NonNegativeAmountSchedule", propOrder = {
    "currency"
})
@XmlSeeAlso({
    FxCounterCurrencyAmount.class
})
public class NonNegativeAmountSchedule
    extends NonNegativeSchedule
{

    @XmlElement(required = true)
    protected Currency currency;

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency(Currency value) {
        this.currency = value;
    }

}
