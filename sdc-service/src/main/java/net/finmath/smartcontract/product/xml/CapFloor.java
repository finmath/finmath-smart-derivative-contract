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
 * A type defining an interest rate cap, floor, or cap/floor strategy (e.g. collar) product.
 * 
 * <p>Java class for CapFloor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapFloor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Product"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="capFloorStream" type="{http://www.fpml.org/FpML-5/confirmation}InterestRateStream"/&gt;
 *         &lt;element name="premium" type="{http://www.fpml.org/FpML-5/confirmation}Payment" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="additionalPayment" type="{http://www.fpml.org/FpML-5/confirmation}Payment" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="earlyTerminationProvision" type="{http://www.fpml.org/FpML-5/confirmation}EarlyTerminationProvision" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapFloor", propOrder = {
    "capFloorStream",
    "premium",
    "additionalPayment",
    "earlyTerminationProvision"
})
public class CapFloor
    extends Product
{

    @XmlElement(required = true)
    protected InterestRateStream capFloorStream;
    protected List<Payment> premium;
    protected List<Payment> additionalPayment;
    protected EarlyTerminationProvision earlyTerminationProvision;

    /**
     * Gets the value of the capFloorStream property.
     * 
     * @return
     *     possible object is
     *     {@link InterestRateStream }
     *     
     */
    public InterestRateStream getCapFloorStream() {
        return capFloorStream;
    }

    /**
     * Sets the value of the capFloorStream property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterestRateStream }
     *     
     */
    public void setCapFloorStream(InterestRateStream value) {
        this.capFloorStream = value;
    }

    /**
     * Gets the value of the premium property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the premium property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremium().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Payment }
     * 
     * 
     */
    public List<Payment> getPremium() {
        if (premium == null) {
            premium = new ArrayList<Payment>();
        }
        return this.premium;
    }

    /**
     * Gets the value of the additionalPayment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalPayment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalPayment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Payment }
     * 
     * 
     */
    public List<Payment> getAdditionalPayment() {
        if (additionalPayment == null) {
            additionalPayment = new ArrayList<Payment>();
        }
        return this.additionalPayment;
    }

    /**
     * Gets the value of the earlyTerminationProvision property.
     * 
     * @return
     *     possible object is
     *     {@link EarlyTerminationProvision }
     *     
     */
    public EarlyTerminationProvision getEarlyTerminationProvision() {
        return earlyTerminationProvision;
    }

    /**
     * Sets the value of the earlyTerminationProvision property.
     * 
     * @param value
     *     allowed object is
     *     {@link EarlyTerminationProvision }
     *     
     */
    public void setEarlyTerminationProvision(EarlyTerminationProvision value) {
        this.earlyTerminationProvision = value;
    }

}