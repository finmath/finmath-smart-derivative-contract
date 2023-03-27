//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A structure describing a trade registration event that is part of a clearing process.
 * 
 * <p>Java class for Clearing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Clearing"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="submitted" type="{http://www.fpml.org/FpML-5/confirmation}TradeWrapper"/&gt;
 *         &lt;element name="cleared" type="{http://www.fpml.org/FpML-5/confirmation}TradeWrapper" maxOccurs="2" minOccurs="2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Clearing", propOrder = {
    "submitted",
    "cleared"
})
public class Clearing {

    @XmlElement(required = true)
    protected TradeWrapper submitted;
    @XmlElement(required = true)
    protected List<TradeWrapper> cleared;

    /**
     * Gets the value of the submitted property.
     * 
     * @return
     *     possible object is
     *     {@link TradeWrapper }
     *     
     */
    public TradeWrapper getSubmitted() {
        return submitted;
    }

    /**
     * Sets the value of the submitted property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradeWrapper }
     *     
     */
    public void setSubmitted(TradeWrapper value) {
        this.submitted = value;
    }

    /**
     * Gets the value of the cleared property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the cleared property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCleared().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TradeWrapper }
     * 
     * 
     */
    public List<TradeWrapper> getCleared() {
        if (cleared == null) {
            cleared = new ArrayList<TradeWrapper>();
        }
        return this.cleared;
    }

}