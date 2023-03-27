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
 * Used as a abstract type for defining accrual structures within loan instruments.
 * 
 * <p>Java class for AccrualOptionBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccrualOptionBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accrualOptionId" type="{http://www.fpml.org/FpML-5/confirmation}AccrualTypeId"/&gt;
 *         &lt;element name="dayCountFraction" type="{http://www.fpml.org/FpML-5/confirmation}DayCountFraction"/&gt;
 *         &lt;element name="paymentFrequency" type="{http://www.fpml.org/FpML-5/confirmation}Period" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccrualOptionBase", propOrder = {
    "accrualOptionId",
    "dayCountFraction",
    "paymentFrequency"
})
@XmlSeeAlso({
    FixedRateOptionBase.class,
    FloatingRateOptionBase.class,
    FeeRateOptionBase.class
})
public abstract class AccrualOptionBase {

    @XmlElement(required = true)
    protected AccrualTypeId accrualOptionId;
    @XmlElement(required = true)
    protected DayCountFraction dayCountFraction;
    protected Period paymentFrequency;

    /**
     * Gets the value of the accrualOptionId property.
     * 
     * @return
     *     possible object is
     *     {@link AccrualTypeId }
     *     
     */
    public AccrualTypeId getAccrualOptionId() {
        return accrualOptionId;
    }

    /**
     * Sets the value of the accrualOptionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccrualTypeId }
     *     
     */
    public void setAccrualOptionId(AccrualTypeId value) {
        this.accrualOptionId = value;
    }

    /**
     * Gets the value of the dayCountFraction property.
     * 
     * @return
     *     possible object is
     *     {@link DayCountFraction }
     *     
     */
    public DayCountFraction getDayCountFraction() {
        return dayCountFraction;
    }

    /**
     * Sets the value of the dayCountFraction property.
     * 
     * @param value
     *     allowed object is
     *     {@link DayCountFraction }
     *     
     */
    public void setDayCountFraction(DayCountFraction value) {
        this.dayCountFraction = value;
    }

    /**
     * Gets the value of the paymentFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getPaymentFrequency() {
        return paymentFrequency;
    }

    /**
     * Sets the value of the paymentFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setPaymentFrequency(Period value) {
        this.paymentFrequency = value;
    }

}
