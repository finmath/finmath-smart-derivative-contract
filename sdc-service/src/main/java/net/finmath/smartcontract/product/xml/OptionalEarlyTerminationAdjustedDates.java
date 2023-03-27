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
 * A type defining the adjusted dates associated with an optional early termination provision.
 * 
 * <p>Java class for OptionalEarlyTerminationAdjustedDates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OptionalEarlyTerminationAdjustedDates"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="earlyTerminationEvent" type="{http://www.fpml.org/FpML-5/confirmation}EarlyTerminationEvent" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionalEarlyTerminationAdjustedDates", propOrder = {
    "earlyTerminationEvent"
})
public class OptionalEarlyTerminationAdjustedDates {

    @XmlElement(required = true)
    protected List<EarlyTerminationEvent> earlyTerminationEvent;

    /**
     * Gets the value of the earlyTerminationEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the earlyTerminationEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEarlyTerminationEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EarlyTerminationEvent }
     * 
     * 
     */
    public List<EarlyTerminationEvent> getEarlyTerminationEvent() {
        if (earlyTerminationEvent == null) {
            earlyTerminationEvent = new ArrayList<EarlyTerminationEvent>();
        }
        return this.earlyTerminationEvent;
    }

}
