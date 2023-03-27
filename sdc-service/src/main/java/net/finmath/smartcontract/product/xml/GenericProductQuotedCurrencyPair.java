//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type that describes the composition of a rate that has been quoted or is to be quoted. This includes the two currencies and the quotation relationship between the two currencies and is used as a building block throughout the FX specification.
 * 
 * <p>Java class for GenericProductQuotedCurrencyPair complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GenericProductQuotedCurrencyPair"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="currency1" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;element name="currency2" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;element name="quoteBasis" type="{http://www.fpml.org/FpML-5/confirmation}QuoteBasisEnum"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericProductQuotedCurrencyPair", propOrder = {
    "currency1",
    "currency2",
    "quoteBasis"
})
public class GenericProductQuotedCurrencyPair {

    @XmlElement(required = true)
    protected Currency currency1;
    @XmlElement(required = true)
    protected Currency currency2;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected QuoteBasisEnum quoteBasis;

    /**
     * Gets the value of the currency1 property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency1() {
        return currency1;
    }

    /**
     * Sets the value of the currency1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency1(Currency value) {
        this.currency1 = value;
    }

    /**
     * Gets the value of the currency2 property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency2() {
        return currency2;
    }

    /**
     * Sets the value of the currency2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency2(Currency value) {
        this.currency2 = value;
    }

    /**
     * Gets the value of the quoteBasis property.
     * 
     * @return
     *     possible object is
     *     {@link QuoteBasisEnum }
     *     
     */
    public QuoteBasisEnum getQuoteBasis() {
        return quoteBasis;
    }

    /**
     * Sets the value of the quoteBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuoteBasisEnum }
     *     
     */
    public void setQuoteBasis(QuoteBasisEnum value) {
        this.quoteBasis = value;
    }

}
