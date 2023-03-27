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
 * Represents an evergreen option that is available within a letter of credit instrument.
 * 
 * <p>Java class for EvergreenOption complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EvergreenOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nonRenewalNoticePeriod" type="{http://www.fpml.org/FpML-5/confirmation}Period" minOccurs="0"/&gt;
 *         &lt;element name="extensionPeriod" type="{http://www.fpml.org/FpML-5/confirmation}Period"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvergreenOption", propOrder = {
    "nonRenewalNoticePeriod",
    "extensionPeriod"
})
public class EvergreenOption {

    protected Period nonRenewalNoticePeriod;
    @XmlElement(required = true)
    protected Period extensionPeriod;

    /**
     * Gets the value of the nonRenewalNoticePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getNonRenewalNoticePeriod() {
        return nonRenewalNoticePeriod;
    }

    /**
     * Sets the value of the nonRenewalNoticePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setNonRenewalNoticePeriod(Period value) {
        this.nonRenewalNoticePeriod = value;
    }

    /**
     * Gets the value of the extensionPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getExtensionPeriod() {
        return extensionPeriod;
    }

    /**
     * Sets the value of the extensionPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setExtensionPeriod(Period value) {
        this.extensionPeriod = value;
    }

}
