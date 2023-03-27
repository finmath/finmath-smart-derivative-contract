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
 * An observation period that is offset from a Calculation Period.
 * 
 * <p>Java class for Lag complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Lag"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="lagDuration" type="{http://www.fpml.org/FpML-5/confirmation}Period"/&gt;
 *         &lt;element name="firstObservationDateOffset" type="{http://www.fpml.org/FpML-5/confirmation}Period" minOccurs="0"/&gt;
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
@XmlType(name = "Lag", propOrder = {
    "lagDuration",
    "firstObservationDateOffset"
})
public class Lag {

    @XmlElement(required = true)
    protected Period lagDuration;
    protected Period firstObservationDateOffset;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the lagDuration property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getLagDuration() {
        return lagDuration;
    }

    /**
     * Sets the value of the lagDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setLagDuration(Period value) {
        this.lagDuration = value;
    }

    /**
     * Gets the value of the firstObservationDateOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getFirstObservationDateOffset() {
        return firstObservationDateOffset;
    }

    /**
     * Sets the value of the firstObservationDateOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setFirstObservationDateOffset(Period value) {
        this.firstObservationDateOffset = value;
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
