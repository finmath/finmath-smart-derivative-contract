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
 * A type for defining the multiple exercise provisions of an American style commodity option.
 * 
 * <p>Java class for CommodityMultipleExercise complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityMultipleExercise"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="integralMultipleQuantity" type="{http://www.fpml.org/FpML-5/confirmation}CommodityNotionalQuantity" minOccurs="0"/&gt;
 *         &lt;element name="minimumNotionalQuantity" type="{http://www.fpml.org/FpML-5/confirmation}CommodityNotionalQuantity"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommodityMultipleExercise", propOrder = {
    "integralMultipleQuantity",
    "minimumNotionalQuantity"
})
public class CommodityMultipleExercise {

    protected CommodityNotionalQuantity integralMultipleQuantity;
    @XmlElement(required = true)
    protected CommodityNotionalQuantity minimumNotionalQuantity;

    /**
     * Gets the value of the integralMultipleQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityNotionalQuantity }
     *     
     */
    public CommodityNotionalQuantity getIntegralMultipleQuantity() {
        return integralMultipleQuantity;
    }

    /**
     * Sets the value of the integralMultipleQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityNotionalQuantity }
     *     
     */
    public void setIntegralMultipleQuantity(CommodityNotionalQuantity value) {
        this.integralMultipleQuantity = value;
    }

    /**
     * Gets the value of the minimumNotionalQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityNotionalQuantity }
     *     
     */
    public CommodityNotionalQuantity getMinimumNotionalQuantity() {
        return minimumNotionalQuantity;
    }

    /**
     * Sets the value of the minimumNotionalQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityNotionalQuantity }
     *     
     */
    public void setMinimumNotionalQuantity(CommodityNotionalQuantity value) {
        this.minimumNotionalQuantity = value;
    }

}