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
 * <p>Java class for Restructuring complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Restructuring"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="applicable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="restructuringType" type="{http://www.fpml.org/FpML-5/confirmation}RestructuringType" minOccurs="0"/&gt;
 *         &lt;element name="multipleHolderObligation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="multipleCreditEventNotices" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Restructuring", propOrder = {
    "applicable",
    "restructuringType",
    "multipleHolderObligation",
    "multipleCreditEventNotices"
})
public class Restructuring {

    protected boolean applicable;
    protected RestructuringType restructuringType;
    protected Boolean multipleHolderObligation;
    protected Boolean multipleCreditEventNotices;

    /**
     * Gets the value of the applicable property.
     * 
     */
    public boolean isApplicable() {
        return applicable;
    }

    /**
     * Sets the value of the applicable property.
     * 
     */
    public void setApplicable(boolean value) {
        this.applicable = value;
    }

    /**
     * Gets the value of the restructuringType property.
     * 
     * @return
     *     possible object is
     *     {@link RestructuringType }
     *     
     */
    public RestructuringType getRestructuringType() {
        return restructuringType;
    }

    /**
     * Sets the value of the restructuringType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestructuringType }
     *     
     */
    public void setRestructuringType(RestructuringType value) {
        this.restructuringType = value;
    }

    /**
     * Gets the value of the multipleHolderObligation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultipleHolderObligation() {
        return multipleHolderObligation;
    }

    /**
     * Sets the value of the multipleHolderObligation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultipleHolderObligation(Boolean value) {
        this.multipleHolderObligation = value;
    }

    /**
     * Gets the value of the multipleCreditEventNotices property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultipleCreditEventNotices() {
        return multipleCreditEventNotices;
    }

    /**
     * Sets the value of the multipleCreditEventNotices property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultipleCreditEventNotices(Boolean value) {
        this.multipleCreditEventNotices = value;
    }

}
