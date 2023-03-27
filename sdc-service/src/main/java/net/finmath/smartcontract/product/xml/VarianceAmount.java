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
 * Calculation of a Variance Amount.
 * 
 * <p>Java class for VarianceAmount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VarianceAmount"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}CalculatedAmount"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="variance" type="{http://www.fpml.org/FpML-5/confirmation}Variance"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VarianceAmount", propOrder = {
    "variance"
})
public class VarianceAmount
    extends CalculatedAmount
{

    @XmlElement(required = true)
    protected Variance variance;

    /**
     * Gets the value of the variance property.
     * 
     * @return
     *     possible object is
     *     {@link Variance }
     *     
     */
    public Variance getVariance() {
        return variance;
    }

    /**
     * Sets the value of the variance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Variance }
     *     
     */
    public void setVariance(Variance value) {
        this.variance = value;
    }

}
