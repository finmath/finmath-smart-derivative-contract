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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type that is used for including the currency exchange rates used to cross between the traded currencies for non-base currency FX contracts.
 * 
 * <p>Java class for CrossRate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CrossRate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}QuotedCurrencyPair"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rate" type="{http://www.fpml.org/FpML-5/confirmation}PositiveDecimal"/&gt;
 *         &lt;sequence minOccurs="0"&gt;
 *           &lt;element name="spotRate" type="{http://www.fpml.org/FpML-5/confirmation}PositiveDecimal"/&gt;
 *           &lt;element name="forwardPoints" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrossRate", propOrder = {
    "rate",
    "spotRate",
    "forwardPoints"
})
public class CrossRate
    extends QuotedCurrencyPair
{

    @XmlElement(required = true)
    protected BigDecimal rate;
    protected BigDecimal spotRate;
    protected BigDecimal forwardPoints;

    /**
     * Gets the value of the rate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Sets the value of the rate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRate(BigDecimal value) {
        this.rate = value;
    }

    /**
     * Gets the value of the spotRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpotRate() {
        return spotRate;
    }

    /**
     * Sets the value of the spotRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpotRate(BigDecimal value) {
        this.spotRate = value;
    }

    /**
     * Gets the value of the forwardPoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getForwardPoints() {
        return forwardPoints;
    }

    /**
     * Sets the value of the forwardPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setForwardPoints(BigDecimal value) {
        this.forwardPoints = value;
    }

}