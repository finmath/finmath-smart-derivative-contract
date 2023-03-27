//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * The Expiration Dates of the trade relative to the Calculation Periods.
 * 
 * <p>Java class for CommodityRelativeExpirationDates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityRelativeExpirationDates"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="expireRelativeToEvent" type="{http://www.fpml.org/FpML-5/confirmation}CommodityExpireRelativeToEvent"/&gt;
 *         &lt;element name="expirationDateOffset" type="{http://www.fpml.org/FpML-5/confirmation}DateOffset"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}BusinessCentersOrReference.model" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommodityRelativeExpirationDates", propOrder = {
    "expireRelativeToEvent",
    "expirationDateOffset",
    "businessCentersReference",
    "businessCenters"
})
public class CommodityRelativeExpirationDates {

    @XmlElement(required = true)
    protected CommodityExpireRelativeToEvent expireRelativeToEvent;
    @XmlElement(required = true)
    protected DateOffset expirationDateOffset;
    protected BusinessCentersReference businessCentersReference;
    protected BusinessCenters businessCenters;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the expireRelativeToEvent property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityExpireRelativeToEvent }
     *     
     */
    public CommodityExpireRelativeToEvent getExpireRelativeToEvent() {
        return expireRelativeToEvent;
    }

    /**
     * Sets the value of the expireRelativeToEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityExpireRelativeToEvent }
     *     
     */
    public void setExpireRelativeToEvent(CommodityExpireRelativeToEvent value) {
        this.expireRelativeToEvent = value;
    }

    /**
     * Gets the value of the expirationDateOffset property.
     * 
     * @return
     *     possible object is
     *     {@link DateOffset }
     *     
     */
    public DateOffset getExpirationDateOffset() {
        return expirationDateOffset;
    }

    /**
     * Sets the value of the expirationDateOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOffset }
     *     
     */
    public void setExpirationDateOffset(DateOffset value) {
        this.expirationDateOffset = value;
    }

    /**
     * Gets the value of the businessCentersReference property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCentersReference }
     *     
     */
    public BusinessCentersReference getBusinessCentersReference() {
        return businessCentersReference;
    }

    /**
     * Sets the value of the businessCentersReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCentersReference }
     *     
     */
    public void setBusinessCentersReference(BusinessCentersReference value) {
        this.businessCentersReference = value;
    }

    /**
     * Gets the value of the businessCenters property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCenters }
     *     
     */
    public BusinessCenters getBusinessCenters() {
        return businessCenters;
    }

    /**
     * Sets the value of the businessCenters property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCenters }
     *     
     */
    public void setBusinessCenters(BusinessCenters value) {
        this.businessCenters = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
