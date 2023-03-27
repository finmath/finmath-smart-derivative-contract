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
 * A type defining the beneficiary of the funds.
 * 
 * <p>Java class for Beneficiary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Beneficiary"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}RoutingIdentification.model"/&gt;
 *         &lt;element name="beneficiaryPartyReference" type="{http://www.fpml.org/FpML-5/confirmation}PartyReference" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Beneficiary", propOrder = {
    "routingIds",
    "routingExplicitDetails",
    "routingIdsAndExplicitDetails",
    "beneficiaryPartyReference"
})
public class Beneficiary {

    protected RoutingIds routingIds;
    protected RoutingExplicitDetails routingExplicitDetails;
    protected RoutingIdsAndExplicitDetails routingIdsAndExplicitDetails;
    protected PartyReference beneficiaryPartyReference;

    /**
     * Gets the value of the routingIds property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingIds }
     *     
     */
    public RoutingIds getRoutingIds() {
        return routingIds;
    }

    /**
     * Sets the value of the routingIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingIds }
     *     
     */
    public void setRoutingIds(RoutingIds value) {
        this.routingIds = value;
    }

    /**
     * Gets the value of the routingExplicitDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingExplicitDetails }
     *     
     */
    public RoutingExplicitDetails getRoutingExplicitDetails() {
        return routingExplicitDetails;
    }

    /**
     * Sets the value of the routingExplicitDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingExplicitDetails }
     *     
     */
    public void setRoutingExplicitDetails(RoutingExplicitDetails value) {
        this.routingExplicitDetails = value;
    }

    /**
     * Gets the value of the routingIdsAndExplicitDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RoutingIdsAndExplicitDetails }
     *     
     */
    public RoutingIdsAndExplicitDetails getRoutingIdsAndExplicitDetails() {
        return routingIdsAndExplicitDetails;
    }

    /**
     * Sets the value of the routingIdsAndExplicitDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoutingIdsAndExplicitDetails }
     *     
     */
    public void setRoutingIdsAndExplicitDetails(RoutingIdsAndExplicitDetails value) {
        this.routingIdsAndExplicitDetails = value;
    }

    /**
     * Gets the value of the beneficiaryPartyReference property.
     * 
     * @return
     *     possible object is
     *     {@link PartyReference }
     *     
     */
    public PartyReference getBeneficiaryPartyReference() {
        return beneficiaryPartyReference;
    }

    /**
     * Sets the value of the beneficiaryPartyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyReference }
     *     
     */
    public void setBeneficiaryPartyReference(PartyReference value) {
        this.beneficiaryPartyReference = value;
    }

}
