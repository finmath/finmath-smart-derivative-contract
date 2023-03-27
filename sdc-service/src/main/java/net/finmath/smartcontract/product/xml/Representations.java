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
 * A type for defining ISDA 2002 Equity Derivative Representations.
 * 
 * <p>Java class for Representations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Representations"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nonReliance" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="agreementsRegardingHedging" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="indexDisclaimer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="additionalAcknowledgements" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Representations", propOrder = {
    "nonReliance",
    "agreementsRegardingHedging",
    "indexDisclaimer",
    "additionalAcknowledgements"
})
public class Representations {

    protected boolean nonReliance;
    protected boolean agreementsRegardingHedging;
    protected Boolean indexDisclaimer;
    protected boolean additionalAcknowledgements;

    /**
     * Gets the value of the nonReliance property.
     * 
     */
    public boolean isNonReliance() {
        return nonReliance;
    }

    /**
     * Sets the value of the nonReliance property.
     * 
     */
    public void setNonReliance(boolean value) {
        this.nonReliance = value;
    }

    /**
     * Gets the value of the agreementsRegardingHedging property.
     * 
     */
    public boolean isAgreementsRegardingHedging() {
        return agreementsRegardingHedging;
    }

    /**
     * Sets the value of the agreementsRegardingHedging property.
     * 
     */
    public void setAgreementsRegardingHedging(boolean value) {
        this.agreementsRegardingHedging = value;
    }

    /**
     * Gets the value of the indexDisclaimer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIndexDisclaimer() {
        return indexDisclaimer;
    }

    /**
     * Sets the value of the indexDisclaimer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndexDisclaimer(Boolean value) {
        this.indexDisclaimer = value;
    }

    /**
     * Gets the value of the additionalAcknowledgements property.
     * 
     */
    public boolean isAdditionalAcknowledgements() {
        return additionalAcknowledgements;
    }

    /**
     * Sets the value of the additionalAcknowledgements property.
     * 
     */
    public void setAdditionalAcknowledgements(boolean value) {
        this.additionalAcknowledgements = value;
    }

}