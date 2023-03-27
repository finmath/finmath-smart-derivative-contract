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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type describing variance bounds, which are used to exclude money price values outside of the specified range In a Up Conditional Swap Underlyer price must be equal to or higher than Lower Barrier In a Down Conditional Swap Underlyer price must be equal to or lower than Upper Barrier In a Corridor Conditional Swap Underlyer price must be equal to or higher than Lower Barrier and must be equal to or lower than Upper Barrier.
 * 
 * <p>Java class for BoundedVariance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BoundedVariance"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="realisedVarianceMethod" type="{http://www.fpml.org/FpML-5/confirmation}RealisedVarianceMethodEnum"/&gt;
 *         &lt;element name="daysInRangeAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="upperBarrier" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeDecimal" minOccurs="0"/&gt;
 *         &lt;element name="lowerBarrier" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeDecimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundedVariance", propOrder = {
    "realisedVarianceMethod",
    "daysInRangeAdjustment",
    "upperBarrier",
    "lowerBarrier"
})
public class BoundedVariance {

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected RealisedVarianceMethodEnum realisedVarianceMethod;
    protected boolean daysInRangeAdjustment;
    protected BigDecimal upperBarrier;
    protected BigDecimal lowerBarrier;

    /**
     * Gets the value of the realisedVarianceMethod property.
     * 
     * @return
     *     possible object is
     *     {@link RealisedVarianceMethodEnum }
     *     
     */
    public RealisedVarianceMethodEnum getRealisedVarianceMethod() {
        return realisedVarianceMethod;
    }

    /**
     * Sets the value of the realisedVarianceMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link RealisedVarianceMethodEnum }
     *     
     */
    public void setRealisedVarianceMethod(RealisedVarianceMethodEnum value) {
        this.realisedVarianceMethod = value;
    }

    /**
     * Gets the value of the daysInRangeAdjustment property.
     * 
     */
    public boolean isDaysInRangeAdjustment() {
        return daysInRangeAdjustment;
    }

    /**
     * Sets the value of the daysInRangeAdjustment property.
     * 
     */
    public void setDaysInRangeAdjustment(boolean value) {
        this.daysInRangeAdjustment = value;
    }

    /**
     * Gets the value of the upperBarrier property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getUpperBarrier() {
        return upperBarrier;
    }

    /**
     * Sets the value of the upperBarrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setUpperBarrier(BigDecimal value) {
        this.upperBarrier = value;
    }

    /**
     * Gets the value of the lowerBarrier property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLowerBarrier() {
        return lowerBarrier;
    }

    /**
     * Sets the value of the lowerBarrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLowerBarrier(BigDecimal value) {
        this.lowerBarrier = value;
    }

}
