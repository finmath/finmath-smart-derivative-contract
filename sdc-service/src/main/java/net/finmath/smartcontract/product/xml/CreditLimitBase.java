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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A structure describing a basic credit limit.
 * 
 * <p>Java class for CreditLimitBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditLimitBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="limitId" type="{http://www.fpml.org/FpML-5/confirmation}LimitId"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}Product.model"/&gt;
 *         &lt;element name="currency" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;element name="tenor" type="{http://www.fpml.org/FpML-5/confirmation}Period"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditLimitBase", propOrder = {
    "limitId",
    "primaryAssetClass",
    "secondaryAssetClass",
    "productType",
    "productId",
    "assetClass",
    "currency",
    "tenor"
})
@XmlSeeAlso({
    CreditLimit.class
})
public class CreditLimitBase {

    @XmlElement(required = true)
    protected LimitId limitId;
    protected AssetClass primaryAssetClass;
    protected List<AssetClass> secondaryAssetClass;
    protected List<ProductType> productType;
    protected List<ProductId> productId;
    protected List<AssetClass> assetClass;
    @XmlElement(required = true)
    protected Currency currency;
    @XmlElement(required = true)
    protected Period tenor;

    /**
     * Gets the value of the limitId property.
     * 
     * @return
     *     possible object is
     *     {@link LimitId }
     *     
     */
    public LimitId getLimitId() {
        return limitId;
    }

    /**
     * Sets the value of the limitId property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitId }
     *     
     */
    public void setLimitId(LimitId value) {
        this.limitId = value;
    }

    /**
     * Gets the value of the primaryAssetClass property.
     * 
     * @return
     *     possible object is
     *     {@link AssetClass }
     *     
     */
    public AssetClass getPrimaryAssetClass() {
        return primaryAssetClass;
    }

    /**
     * Sets the value of the primaryAssetClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetClass }
     *     
     */
    public void setPrimaryAssetClass(AssetClass value) {
        this.primaryAssetClass = value;
    }

    /**
     * Gets the value of the secondaryAssetClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the secondaryAssetClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecondaryAssetClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssetClass }
     * 
     * 
     */
    public List<AssetClass> getSecondaryAssetClass() {
        if (secondaryAssetClass == null) {
            secondaryAssetClass = new ArrayList<AssetClass>();
        }
        return this.secondaryAssetClass;
    }

    /**
     * Gets the value of the productType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the productType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductType }
     * 
     * 
     */
    public List<ProductType> getProductType() {
        if (productType == null) {
            productType = new ArrayList<ProductType>();
        }
        return this.productType;
    }

    /**
     * Gets the value of the productId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the productId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductId }
     * 
     * 
     */
    public List<ProductId> getProductId() {
        if (productId == null) {
            productId = new ArrayList<ProductId>();
        }
        return this.productId;
    }

    /**
     * Gets the value of the assetClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the assetClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssetClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssetClass }
     * 
     * 
     */
    public List<AssetClass> getAssetClass() {
        if (assetClass == null) {
            assetClass = new ArrayList<AssetClass>();
        }
        return this.assetClass;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency(Currency value) {
        this.currency = value;
    }

    /**
     * Gets the value of the tenor property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getTenor() {
        return tenor;
    }

    /**
     * Sets the value of the tenor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setTenor(Period value) {
        this.tenor = value;
    }

}
