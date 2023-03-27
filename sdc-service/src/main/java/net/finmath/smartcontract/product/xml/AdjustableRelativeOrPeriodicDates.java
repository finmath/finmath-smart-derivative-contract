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
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for AdjustableRelativeOrPeriodicDates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdjustableRelativeOrPeriodicDates"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="adjustableDates" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableDates"/&gt;
 *         &lt;element name="relativeDateSequence" type="{http://www.fpml.org/FpML-5/confirmation}RelativeDateSequence"/&gt;
 *         &lt;element name="periodicDates" type="{http://www.fpml.org/FpML-5/confirmation}PeriodicDates"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdjustableRelativeOrPeriodicDates", propOrder = {
    "adjustableDates",
    "relativeDateSequence",
    "periodicDates"
})
public class AdjustableRelativeOrPeriodicDates {

    protected AdjustableDates adjustableDates;
    protected RelativeDateSequence relativeDateSequence;
    protected PeriodicDates periodicDates;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the adjustableDates property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableDates }
     *     
     */
    public AdjustableDates getAdjustableDates() {
        return adjustableDates;
    }

    /**
     * Sets the value of the adjustableDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableDates }
     *     
     */
    public void setAdjustableDates(AdjustableDates value) {
        this.adjustableDates = value;
    }

    /**
     * Gets the value of the relativeDateSequence property.
     * 
     * @return
     *     possible object is
     *     {@link RelativeDateSequence }
     *     
     */
    public RelativeDateSequence getRelativeDateSequence() {
        return relativeDateSequence;
    }

    /**
     * Sets the value of the relativeDateSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelativeDateSequence }
     *     
     */
    public void setRelativeDateSequence(RelativeDateSequence value) {
        this.relativeDateSequence = value;
    }

    /**
     * Gets the value of the periodicDates property.
     * 
     * @return
     *     possible object is
     *     {@link PeriodicDates }
     *     
     */
    public PeriodicDates getPeriodicDates() {
        return periodicDates;
    }

    /**
     * Sets the value of the periodicDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeriodicDates }
     *     
     */
    public void setPeriodicDates(PeriodicDates value) {
        this.periodicDates = value;
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
