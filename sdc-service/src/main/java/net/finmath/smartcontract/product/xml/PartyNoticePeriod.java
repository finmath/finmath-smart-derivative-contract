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
 * A type to represent agreed period of notice to be given in advance before exercise of the open repo trade by a party requesting such exercise and reference to that party.
 * 
 * <p>Java class for PartyNoticePeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyNoticePeriod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="partyReference" type="{http://www.fpml.org/FpML-5/confirmation}PartyReference"/&gt;
 *         &lt;element name="noticePeriod" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOffset"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyNoticePeriod", propOrder = {
    "partyReference",
    "noticePeriod"
})
public class PartyNoticePeriod {

    @XmlElement(required = true)
    protected PartyReference partyReference;
    @XmlElement(required = true)
    protected AdjustableOffset noticePeriod;

    /**
     * Gets the value of the partyReference property.
     * 
     * @return
     *     possible object is
     *     {@link PartyReference }
     *     
     */
    public PartyReference getPartyReference() {
        return partyReference;
    }

    /**
     * Sets the value of the partyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyReference }
     *     
     */
    public void setPartyReference(PartyReference value) {
        this.partyReference = value;
    }

    /**
     * Gets the value of the noticePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOffset }
     *     
     */
    public AdjustableOffset getNoticePeriod() {
        return noticePeriod;
    }

    /**
     * Sets the value of the noticePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOffset }
     *     
     */
    public void setNoticePeriod(AdjustableOffset value) {
        this.noticePeriod = value;
    }

}