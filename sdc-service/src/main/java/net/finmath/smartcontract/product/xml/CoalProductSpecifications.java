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
 * The different options for specifying the quality attributes of the coal to be delivered.
 * 
 * <p>Java class for CoalProductSpecifications complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoalProductSpecifications"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="standardQuality" type="{http://www.fpml.org/FpML-5/confirmation}CoalStandardQuality"/&gt;
 *         &lt;element name="standardQualitySchedule" type="{http://www.fpml.org/FpML-5/confirmation}CoalStandardQualitySchedule"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoalProductSpecifications", propOrder = {
    "standardQuality",
    "standardQualitySchedule"
})
public class CoalProductSpecifications {

    protected CoalStandardQuality standardQuality;
    protected CoalStandardQualitySchedule standardQualitySchedule;

    /**
     * Gets the value of the standardQuality property.
     * 
     * @return
     *     possible object is
     *     {@link CoalStandardQuality }
     *     
     */
    public CoalStandardQuality getStandardQuality() {
        return standardQuality;
    }

    /**
     * Sets the value of the standardQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoalStandardQuality }
     *     
     */
    public void setStandardQuality(CoalStandardQuality value) {
        this.standardQuality = value;
    }

    /**
     * Gets the value of the standardQualitySchedule property.
     * 
     * @return
     *     possible object is
     *     {@link CoalStandardQualitySchedule }
     *     
     */
    public CoalStandardQualitySchedule getStandardQualitySchedule() {
        return standardQualitySchedule;
    }

    /**
     * Sets the value of the standardQualitySchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoalStandardQualitySchedule }
     *     
     */
    public void setStandardQualitySchedule(CoalStandardQualitySchedule value) {
        this.standardQualitySchedule = value;
    }

}