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
 * A type that is used for describing cash settlement of a variance or volatility swap option. It includes the settlement currency together with the spot currency exchange required to calculate the settlement currency amount.
 * 
 * <p>Java class for FxCashSettlementSimple complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxCashSettlementSimple"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="settlementCurrency" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;element name="referenceCurrency" type="{http://www.fpml.org/FpML-5/confirmation}Currency" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="fixing" type="{http://www.fpml.org/FpML-5/confirmation}FxFixing" maxOccurs="unbounded"/&gt;
 *           &lt;element name="rateSourceFixing" type="{http://www.fpml.org/FpML-5/confirmation}FxRateSourceFixing" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxCashSettlementSimple", propOrder = {
    "settlementCurrency",
    "referenceCurrency",
    "fixing",
    "rateSourceFixing"
})
public class FxCashSettlementSimple {

    @XmlElement(required = true)
    protected Currency settlementCurrency;
    protected Currency referenceCurrency;
    protected List<FxFixing> fixing;
    protected List<FxRateSourceFixing> rateSourceFixing;

    /**
     * Gets the value of the settlementCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getSettlementCurrency() {
        return settlementCurrency;
    }

    /**
     * Sets the value of the settlementCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setSettlementCurrency(Currency value) {
        this.settlementCurrency = value;
    }

    /**
     * Gets the value of the referenceCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getReferenceCurrency() {
        return referenceCurrency;
    }

    /**
     * Sets the value of the referenceCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setReferenceCurrency(Currency value) {
        this.referenceCurrency = value;
    }

    /**
     * Gets the value of the fixing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the fixing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFixing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxFixing }
     * 
     * 
     */
    public List<FxFixing> getFixing() {
        if (fixing == null) {
            fixing = new ArrayList<FxFixing>();
        }
        return this.fixing;
    }

    /**
     * Gets the value of the rateSourceFixing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the rateSourceFixing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRateSourceFixing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxRateSourceFixing }
     * 
     * 
     */
    public List<FxRateSourceFixing> getRateSourceFixing() {
        if (rateSourceFixing == null) {
            rateSourceFixing = new ArrayList<FxRateSourceFixing>();
        }
        return this.rateSourceFixing;
    }

}
