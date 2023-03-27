//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type for defining option features.
 * 
 * <p>Java class for OptionFeatures complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OptionFeatures"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="asian" type="{http://www.fpml.org/FpML-5/confirmation}Asian" minOccurs="0"/&gt;
 *         &lt;element name="barrier" type="{http://www.fpml.org/FpML-5/confirmation}Barrier" minOccurs="0"/&gt;
 *         &lt;element name="knock" type="{http://www.fpml.org/FpML-5/confirmation}Knock" minOccurs="0"/&gt;
 *         &lt;element name="passThrough" type="{http://www.fpml.org/FpML-5/confirmation}PassThrough" minOccurs="0"/&gt;
 *         &lt;element name="dividendAdjustment" type="{http://www.fpml.org/FpML-5/confirmation}DividendAdjustment" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionFeatures", propOrder = {
    "asian",
    "barrier",
    "knock",
    "passThrough",
    "dividendAdjustment"
})
public class OptionFeatures {

    protected Asian asian;
    protected Barrier barrier;
    protected Knock knock;
    protected PassThrough passThrough;
    protected DividendAdjustment dividendAdjustment;

    /**
     * Gets the value of the asian property.
     * 
     * @return
     *     possible object is
     *     {@link Asian }
     *     
     */
    public Asian getAsian() {
        return asian;
    }

    /**
     * Sets the value of the asian property.
     * 
     * @param value
     *     allowed object is
     *     {@link Asian }
     *     
     */
    public void setAsian(Asian value) {
        this.asian = value;
    }

    /**
     * Gets the value of the barrier property.
     * 
     * @return
     *     possible object is
     *     {@link Barrier }
     *     
     */
    public Barrier getBarrier() {
        return barrier;
    }

    /**
     * Sets the value of the barrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Barrier }
     *     
     */
    public void setBarrier(Barrier value) {
        this.barrier = value;
    }

    /**
     * Gets the value of the knock property.
     * 
     * @return
     *     possible object is
     *     {@link Knock }
     *     
     */
    public Knock getKnock() {
        return knock;
    }

    /**
     * Sets the value of the knock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Knock }
     *     
     */
    public void setKnock(Knock value) {
        this.knock = value;
    }

    /**
     * Gets the value of the passThrough property.
     * 
     * @return
     *     possible object is
     *     {@link PassThrough }
     *     
     */
    public PassThrough getPassThrough() {
        return passThrough;
    }

    /**
     * Sets the value of the passThrough property.
     * 
     * @param value
     *     allowed object is
     *     {@link PassThrough }
     *     
     */
    public void setPassThrough(PassThrough value) {
        this.passThrough = value;
    }

    /**
     * Gets the value of the dividendAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link DividendAdjustment }
     *     
     */
    public DividendAdjustment getDividendAdjustment() {
        return dividendAdjustment;
    }

    /**
     * Sets the value of the dividendAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link DividendAdjustment }
     *     
     */
    public void setDividendAdjustment(DividendAdjustment value) {
        this.dividendAdjustment = value;
    }

}