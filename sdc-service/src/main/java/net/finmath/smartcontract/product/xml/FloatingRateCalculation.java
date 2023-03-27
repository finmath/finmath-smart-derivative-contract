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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining the floating rate and definitions relating to the calculation of floating rate amounts.
 * 
 * <p>Java class for FloatingRateCalculation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FloatingRateCalculation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}FloatingRate"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="initialRate" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="finalRateRounding" type="{http://www.fpml.org/FpML-5/confirmation}Rounding" minOccurs="0"/&gt;
 *         &lt;element name="averagingMethod" type="{http://www.fpml.org/FpML-5/confirmation}AveragingMethodEnum" minOccurs="0"/&gt;
 *         &lt;element name="negativeInterestRateTreatment" type="{http://www.fpml.org/FpML-5/confirmation}NegativeInterestRateTreatmentEnum" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FloatingRateCalculation", propOrder = {
    "initialRate",
    "finalRateRounding",
    "averagingMethod",
    "negativeInterestRateTreatment"
})
@XmlSeeAlso({
    InflationRateCalculation.class
})
public class FloatingRateCalculation
    extends FloatingRate
{

    protected BigDecimal initialRate;
    protected Rounding finalRateRounding;
    @XmlSchemaType(name = "token")
    protected AveragingMethodEnum averagingMethod;
    @XmlSchemaType(name = "token")
    protected NegativeInterestRateTreatmentEnum negativeInterestRateTreatment;

    /**
     * Gets the value of the initialRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInitialRate() {
        return initialRate;
    }

    /**
     * Sets the value of the initialRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInitialRate(BigDecimal value) {
        this.initialRate = value;
    }

    /**
     * Gets the value of the finalRateRounding property.
     * 
     * @return
     *     possible object is
     *     {@link Rounding }
     *     
     */
    public Rounding getFinalRateRounding() {
        return finalRateRounding;
    }

    /**
     * Sets the value of the finalRateRounding property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rounding }
     *     
     */
    public void setFinalRateRounding(Rounding value) {
        this.finalRateRounding = value;
    }

    /**
     * Gets the value of the averagingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link AveragingMethodEnum }
     *     
     */
    public AveragingMethodEnum getAveragingMethod() {
        return averagingMethod;
    }

    /**
     * Sets the value of the averagingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AveragingMethodEnum }
     *     
     */
    public void setAveragingMethod(AveragingMethodEnum value) {
        this.averagingMethod = value;
    }

    /**
     * Gets the value of the negativeInterestRateTreatment property.
     * 
     * @return
     *     possible object is
     *     {@link NegativeInterestRateTreatmentEnum }
     *     
     */
    public NegativeInterestRateTreatmentEnum getNegativeInterestRateTreatment() {
        return negativeInterestRateTreatment;
    }

    /**
     * Sets the value of the negativeInterestRateTreatment property.
     * 
     * @param value
     *     allowed object is
     *     {@link NegativeInterestRateTreatmentEnum }
     *     
     */
    public void setNegativeInterestRateTreatment(NegativeInterestRateTreatmentEnum value) {
        this.negativeInterestRateTreatment = value;
    }

}
