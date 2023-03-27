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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenceInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenceInformation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="referenceEntity" type="{http://www.fpml.org/FpML-5/confirmation}LegalEntity"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="referenceObligation" type="{http://www.fpml.org/FpML-5/confirmation}ReferenceObligation" maxOccurs="unbounded"/&gt;
 *           &lt;element name="noReferenceObligation" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *           &lt;element name="unknownReferenceObligation" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="allGuarantees" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="referencePrice" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="referencePolicy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="securedList" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceInformation", propOrder = {
    "referenceEntity",
    "referenceObligation",
    "noReferenceObligation",
    "unknownReferenceObligation",
    "allGuarantees",
    "referencePrice",
    "referencePolicy",
    "securedList"
})
public class ReferenceInformation {

    @XmlElement(required = true)
    protected LegalEntity referenceEntity;
    protected List<ReferenceObligation> referenceObligation;
    protected Boolean noReferenceObligation;
    protected Boolean unknownReferenceObligation;
    protected Boolean allGuarantees;
    protected BigDecimal referencePrice;
    protected Boolean referencePolicy;
    protected Boolean securedList;

    /**
     * Gets the value of the referenceEntity property.
     * 
     * @return
     *     possible object is
     *     {@link LegalEntity }
     *     
     */
    public LegalEntity getReferenceEntity() {
        return referenceEntity;
    }

    /**
     * Sets the value of the referenceEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalEntity }
     *     
     */
    public void setReferenceEntity(LegalEntity value) {
        this.referenceEntity = value;
    }

    /**
     * Gets the value of the referenceObligation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the referenceObligation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferenceObligation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceObligation }
     * 
     * 
     */
    public List<ReferenceObligation> getReferenceObligation() {
        if (referenceObligation == null) {
            referenceObligation = new ArrayList<ReferenceObligation>();
        }
        return this.referenceObligation;
    }

    /**
     * Gets the value of the noReferenceObligation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNoReferenceObligation() {
        return noReferenceObligation;
    }

    /**
     * Sets the value of the noReferenceObligation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNoReferenceObligation(Boolean value) {
        this.noReferenceObligation = value;
    }

    /**
     * Gets the value of the unknownReferenceObligation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUnknownReferenceObligation() {
        return unknownReferenceObligation;
    }

    /**
     * Sets the value of the unknownReferenceObligation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnknownReferenceObligation(Boolean value) {
        this.unknownReferenceObligation = value;
    }

    /**
     * Gets the value of the allGuarantees property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllGuarantees() {
        return allGuarantees;
    }

    /**
     * Sets the value of the allGuarantees property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllGuarantees(Boolean value) {
        this.allGuarantees = value;
    }

    /**
     * Gets the value of the referencePrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getReferencePrice() {
        return referencePrice;
    }

    /**
     * Sets the value of the referencePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setReferencePrice(BigDecimal value) {
        this.referencePrice = value;
    }

    /**
     * Gets the value of the referencePolicy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReferencePolicy() {
        return referencePolicy;
    }

    /**
     * Sets the value of the referencePolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReferencePolicy(Boolean value) {
        this.referencePolicy = value;
    }

    /**
     * Gets the value of the securedList property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSecuredList() {
        return securedList;
    }

    /**
     * Sets the value of the securedList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSecuredList(Boolean value) {
        this.securedList = value;
    }

}
