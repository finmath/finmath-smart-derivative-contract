//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigInteger;
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
 * A type defines nearest Delivery Date of the underlying Commodity of expiration of the futures contract.
 * 
 * <p>Java class for DeliveryNearby complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeliveryNearby"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="deliveryNearbyMultiplier" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/&gt;
 *         &lt;element name="deliveryNearbyType" type="{http://www.fpml.org/FpML-5/confirmation}DeliveryNearbyTypeEnum"/&gt;
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
@XmlType(name = "DeliveryNearby", propOrder = {
    "deliveryNearbyMultiplier",
    "deliveryNearbyType"
})
public class DeliveryNearby {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger deliveryNearbyMultiplier;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected DeliveryNearbyTypeEnum deliveryNearbyType;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the deliveryNearbyMultiplier property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDeliveryNearbyMultiplier() {
        return deliveryNearbyMultiplier;
    }

    /**
     * Sets the value of the deliveryNearbyMultiplier property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDeliveryNearbyMultiplier(BigInteger value) {
        this.deliveryNearbyMultiplier = value;
    }

    /**
     * Gets the value of the deliveryNearbyType property.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryNearbyTypeEnum }
     *     
     */
    public DeliveryNearbyTypeEnum getDeliveryNearbyType() {
        return deliveryNearbyType;
    }

    /**
     * Sets the value of the deliveryNearbyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryNearbyTypeEnum }
     *     
     */
    public void setDeliveryNearbyType(DeliveryNearbyTypeEnum value) {
        this.deliveryNearbyType = value;
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
