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
 * The specification of the oil product to be delivered.
 * 
 * <p>Java class for OilProduct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OilProduct"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="type" type="{http://www.fpml.org/FpML-5/confirmation}OilProductType"/&gt;
 *         &lt;element name="grade" type="{http://www.fpml.org/FpML-5/confirmation}CommodityProductGrade"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OilProduct", propOrder = {
    "type",
    "grade"
})
public class OilProduct {

    @XmlElement(required = true)
    protected OilProductType type;
    @XmlElement(required = true)
    protected CommodityProductGrade grade;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link OilProductType }
     *     
     */
    public OilProductType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link OilProductType }
     *     
     */
    public void setType(OilProductType value) {
        this.type = value;
    }

    /**
     * Gets the value of the grade property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityProductGrade }
     *     
     */
    public CommodityProductGrade getGrade() {
        return grade;
    }

    /**
     * Sets the value of the grade property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityProductGrade }
     *     
     */
    public void setGrade(CommodityProductGrade value) {
        this.grade = value;
    }

}
