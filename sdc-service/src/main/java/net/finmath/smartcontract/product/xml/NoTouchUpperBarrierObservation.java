//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NoTouchUpperBarrierObservation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NoTouchUpperBarrierObservation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="triggerRate" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *           &lt;element name="quotedCurrencyPair" type="{http://www.fpml.org/FpML-5/confirmation}QuotedCurrencyPair"/&gt;
 *           &lt;element name="maximumObservedRate" type="{http://www.fpml.org/FpML-5/confirmation}ObservedRate" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="triggerPrice" type="{http://www.fpml.org/FpML-5/confirmation}PositiveMoney"/&gt;
 *           &lt;element name="maximumObservedPrice" type="{http://www.fpml.org/FpML-5/confirmation}ObservedPrice" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoTouchUpperBarrierObservation", propOrder = {
    "triggerRate",
    "quotedCurrencyPair",
    "maximumObservedRate",
    "triggerPrice",
    "maximumObservedPrice"
})
public class NoTouchUpperBarrierObservation {

    protected BigDecimal triggerRate;
    protected QuotedCurrencyPair quotedCurrencyPair;
    protected ObservedRate maximumObservedRate;
    protected PositiveMoney triggerPrice;
    protected ObservedPrice maximumObservedPrice;

    /**
     * Gets the value of the triggerRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTriggerRate() {
        return triggerRate;
    }

    /**
     * Sets the value of the triggerRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTriggerRate(BigDecimal value) {
        this.triggerRate = value;
    }

    /**
     * Gets the value of the quotedCurrencyPair property.
     * 
     * @return
     *     possible object is
     *     {@link QuotedCurrencyPair }
     *     
     */
    public QuotedCurrencyPair getQuotedCurrencyPair() {
        return quotedCurrencyPair;
    }

    /**
     * Sets the value of the quotedCurrencyPair property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuotedCurrencyPair }
     *     
     */
    public void setQuotedCurrencyPair(QuotedCurrencyPair value) {
        this.quotedCurrencyPair = value;
    }

    /**
     * Gets the value of the maximumObservedRate property.
     * 
     * @return
     *     possible object is
     *     {@link ObservedRate }
     *     
     */
    public ObservedRate getMaximumObservedRate() {
        return maximumObservedRate;
    }

    /**
     * Sets the value of the maximumObservedRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservedRate }
     *     
     */
    public void setMaximumObservedRate(ObservedRate value) {
        this.maximumObservedRate = value;
    }

    /**
     * Gets the value of the triggerPrice property.
     * 
     * @return
     *     possible object is
     *     {@link PositiveMoney }
     *     
     */
    public PositiveMoney getTriggerPrice() {
        return triggerPrice;
    }

    /**
     * Sets the value of the triggerPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositiveMoney }
     *     
     */
    public void setTriggerPrice(PositiveMoney value) {
        this.triggerPrice = value;
    }

    /**
     * Gets the value of the maximumObservedPrice property.
     * 
     * @return
     *     possible object is
     *     {@link ObservedPrice }
     *     
     */
    public ObservedPrice getMaximumObservedPrice() {
        return maximumObservedPrice;
    }

    /**
     * Sets the value of the maximumObservedPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservedPrice }
     *     
     */
    public void setMaximumObservedPrice(ObservedPrice value) {
        this.maximumObservedPrice = value;
    }

}