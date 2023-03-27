//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Summary information about a trade package.
 * 
 * <p>Java class for PackageSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageSummary"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="packageIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}IssuerTradeId" minOccurs="0"/&gt;
 *         &lt;element name="orderIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}OrderIdentifier" minOccurs="0"/&gt;
 *         &lt;element name="packageType" type="{http://www.fpml.org/FpML-5/confirmation}PackageType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="size" type="{http://www.fpml.org/FpML-5/confirmation}PositiveDecimal" minOccurs="0"/&gt;
 *         &lt;element name="sequenceNumber" type="{http://www.fpml.org/FpML-5/confirmation}PositiveDecimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageSummary", propOrder = {
    "packageIdentifier",
    "orderIdentifier",
    "packageType",
    "size",
    "sequenceNumber"
})
public class PackageSummary {

    protected IssuerTradeId packageIdentifier;
    protected OrderIdentifier orderIdentifier;
    protected List<PackageType> packageType;
    protected BigDecimal size;
    protected BigDecimal sequenceNumber;

    /**
     * Gets the value of the packageIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link IssuerTradeId }
     *     
     */
    public IssuerTradeId getPackageIdentifier() {
        return packageIdentifier;
    }

    /**
     * Sets the value of the packageIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link IssuerTradeId }
     *     
     */
    public void setPackageIdentifier(IssuerTradeId value) {
        this.packageIdentifier = value;
    }

    /**
     * Gets the value of the orderIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link OrderIdentifier }
     *     
     */
    public OrderIdentifier getOrderIdentifier() {
        return orderIdentifier;
    }

    /**
     * Sets the value of the orderIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderIdentifier }
     *     
     */
    public void setOrderIdentifier(OrderIdentifier value) {
        this.orderIdentifier = value;
    }

    /**
     * Gets the value of the packageType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the packageType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackageType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackageType }
     * 
     * 
     */
    public List<PackageType> getPackageType() {
        if (packageType == null) {
            packageType = new ArrayList<PackageType>();
        }
        return this.packageType;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSize(BigDecimal value) {
        this.size = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSequenceNumber(BigDecimal value) {
        this.sequenceNumber = value;
    }

}
