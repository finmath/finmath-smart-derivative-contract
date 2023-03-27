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
 * Describes a set of disruption events and the fallbacks they will invoke
 * 
 * <p>Java class for FxDisruptionProvisions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxDisruptionProvisions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="events" type="{http://www.fpml.org/FpML-5/confirmation}FxDisruptionEvents"/&gt;
 *         &lt;element name="fallbacks" type="{http://www.fpml.org/FpML-5/confirmation}FxDisruptionFallbacks"/&gt;
 *         &lt;element name="applicableTerms" type="{http://www.fpml.org/FpML-5/confirmation}FxTemplateTerms" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxDisruptionProvisions", propOrder = {
    "events",
    "fallbacks",
    "applicableTerms"
})
public class FxDisruptionProvisions {

    @XmlElement(required = true)
    protected FxDisruptionEvents events;
    @XmlElement(required = true)
    protected FxDisruptionFallbacks fallbacks;
    protected FxTemplateTerms applicableTerms;

    /**
     * Gets the value of the events property.
     * 
     * @return
     *     possible object is
     *     {@link FxDisruptionEvents }
     *     
     */
    public FxDisruptionEvents getEvents() {
        return events;
    }

    /**
     * Sets the value of the events property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxDisruptionEvents }
     *     
     */
    public void setEvents(FxDisruptionEvents value) {
        this.events = value;
    }

    /**
     * Gets the value of the fallbacks property.
     * 
     * @return
     *     possible object is
     *     {@link FxDisruptionFallbacks }
     *     
     */
    public FxDisruptionFallbacks getFallbacks() {
        return fallbacks;
    }

    /**
     * Sets the value of the fallbacks property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxDisruptionFallbacks }
     *     
     */
    public void setFallbacks(FxDisruptionFallbacks value) {
        this.fallbacks = value;
    }

    /**
     * Gets the value of the applicableTerms property.
     * 
     * @return
     *     possible object is
     *     {@link FxTemplateTerms }
     *     
     */
    public FxTemplateTerms getApplicableTerms() {
        return applicableTerms;
    }

    /**
     * Sets the value of the applicableTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxTemplateTerms }
     *     
     */
    public void setApplicableTerms(FxTemplateTerms value) {
        this.applicableTerms = value;
    }

}
