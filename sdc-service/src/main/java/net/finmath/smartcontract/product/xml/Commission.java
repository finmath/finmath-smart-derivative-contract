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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type describing the commission that will be charged for each of the hedge transactions.
 * 
 * <p>Java class for Commission complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Commission"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="commissionDenomination" type="{http://www.fpml.org/FpML-5/confirmation}CommissionDenominationEnum"/&gt;
 *         &lt;element name="commissionAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="currency" type="{http://www.fpml.org/FpML-5/confirmation}Currency" minOccurs="0"/&gt;
 *         &lt;element name="commissionPerTrade" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="fxRate" type="{http://www.fpml.org/FpML-5/confirmation}FxRate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Commission", propOrder = {
    "commissionDenomination",
    "commissionAmount",
    "currency",
    "commissionPerTrade",
    "fxRate"
})
public class Commission {

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected CommissionDenominationEnum commissionDenomination;
    @XmlElement(required = true)
    protected BigDecimal commissionAmount;
    protected Currency currency;
    protected BigDecimal commissionPerTrade;
    protected List<FxRate> fxRate;

    /**
     * Gets the value of the commissionDenomination property.
     * 
     * @return
     *     possible object is
     *     {@link CommissionDenominationEnum }
     *     
     */
    public CommissionDenominationEnum getCommissionDenomination() {
        return commissionDenomination;
    }

    /**
     * Sets the value of the commissionDenomination property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommissionDenominationEnum }
     *     
     */
    public void setCommissionDenomination(CommissionDenominationEnum value) {
        this.commissionDenomination = value;
    }

    /**
     * Gets the value of the commissionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    /**
     * Sets the value of the commissionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCommissionAmount(BigDecimal value) {
        this.commissionAmount = value;
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
     * Gets the value of the commissionPerTrade property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCommissionPerTrade() {
        return commissionPerTrade;
    }

    /**
     * Sets the value of the commissionPerTrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCommissionPerTrade(BigDecimal value) {
        this.commissionPerTrade = value;
    }

    /**
     * Gets the value of the fxRate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the fxRate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFxRate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxRate }
     * 
     * 
     */
    public List<FxRate> getFxRate() {
        if (fxRate == null) {
            fxRate = new ArrayList<FxRate>();
        }
        return this.fxRate;
    }

}
