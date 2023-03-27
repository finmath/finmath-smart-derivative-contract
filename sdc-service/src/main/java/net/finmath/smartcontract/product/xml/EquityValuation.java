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
 * A type for defining how and when an equity option is to be valued.
 * 
 * <p>Java class for EquityValuation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EquityValuation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="valuationDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableDateOrRelativeDateSequence"/&gt;
 *           &lt;element name="valuationDates" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableRelativeOrPeriodicDates"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="valuationTimeType" type="{http://www.fpml.org/FpML-5/confirmation}TimeTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="valuationTime" type="{http://www.fpml.org/FpML-5/confirmation}BusinessCenterTime" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="futuresPriceValuation" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *           &lt;element name="optionsPriceValuation" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="numberOfValuationDates" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="dividendValuationDates" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableRelativeOrPeriodicDates" minOccurs="0"/&gt;
 *         &lt;element name="fPVFinalPriceElectionFallback" type="{http://www.fpml.org/FpML-5/confirmation}FPVFinalPriceElectionFallbackEnum" minOccurs="0"/&gt;
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
@XmlType(name = "EquityValuation", propOrder = {
    "valuationDate",
    "valuationDates",
    "valuationTimeType",
    "valuationTime",
    "futuresPriceValuation",
    "optionsPriceValuation",
    "numberOfValuationDates",
    "dividendValuationDates",
    "fpvFinalPriceElectionFallback"
})
public class EquityValuation {

    protected AdjustableDateOrRelativeDateSequence valuationDate;
    protected AdjustableRelativeOrPeriodicDates valuationDates;
    @XmlSchemaType(name = "token")
    protected TimeTypeEnum valuationTimeType;
    protected BusinessCenterTime valuationTime;
    protected Boolean futuresPriceValuation;
    protected Boolean optionsPriceValuation;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfValuationDates;
    protected AdjustableRelativeOrPeriodicDates dividendValuationDates;
    @XmlElement(name = "fPVFinalPriceElectionFallback")
    @XmlSchemaType(name = "token")
    protected FPVFinalPriceElectionFallbackEnum fpvFinalPriceElectionFallback;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the valuationDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableDateOrRelativeDateSequence }
     *     
     */
    public AdjustableDateOrRelativeDateSequence getValuationDate() {
        return valuationDate;
    }

    /**
     * Sets the value of the valuationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableDateOrRelativeDateSequence }
     *     
     */
    public void setValuationDate(AdjustableDateOrRelativeDateSequence value) {
        this.valuationDate = value;
    }

    /**
     * Gets the value of the valuationDates property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableRelativeOrPeriodicDates }
     *     
     */
    public AdjustableRelativeOrPeriodicDates getValuationDates() {
        return valuationDates;
    }

    /**
     * Sets the value of the valuationDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableRelativeOrPeriodicDates }
     *     
     */
    public void setValuationDates(AdjustableRelativeOrPeriodicDates value) {
        this.valuationDates = value;
    }

    /**
     * Gets the value of the valuationTimeType property.
     * 
     * @return
     *     possible object is
     *     {@link TimeTypeEnum }
     *     
     */
    public TimeTypeEnum getValuationTimeType() {
        return valuationTimeType;
    }

    /**
     * Sets the value of the valuationTimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeTypeEnum }
     *     
     */
    public void setValuationTimeType(TimeTypeEnum value) {
        this.valuationTimeType = value;
    }

    /**
     * Gets the value of the valuationTime property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCenterTime }
     *     
     */
    public BusinessCenterTime getValuationTime() {
        return valuationTime;
    }

    /**
     * Sets the value of the valuationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCenterTime }
     *     
     */
    public void setValuationTime(BusinessCenterTime value) {
        this.valuationTime = value;
    }

    /**
     * Gets the value of the futuresPriceValuation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFuturesPriceValuation() {
        return futuresPriceValuation;
    }

    /**
     * Sets the value of the futuresPriceValuation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFuturesPriceValuation(Boolean value) {
        this.futuresPriceValuation = value;
    }

    /**
     * Gets the value of the optionsPriceValuation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOptionsPriceValuation() {
        return optionsPriceValuation;
    }

    /**
     * Sets the value of the optionsPriceValuation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptionsPriceValuation(Boolean value) {
        this.optionsPriceValuation = value;
    }

    /**
     * Gets the value of the numberOfValuationDates property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfValuationDates() {
        return numberOfValuationDates;
    }

    /**
     * Sets the value of the numberOfValuationDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfValuationDates(BigInteger value) {
        this.numberOfValuationDates = value;
    }

    /**
     * Gets the value of the dividendValuationDates property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableRelativeOrPeriodicDates }
     *     
     */
    public AdjustableRelativeOrPeriodicDates getDividendValuationDates() {
        return dividendValuationDates;
    }

    /**
     * Sets the value of the dividendValuationDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableRelativeOrPeriodicDates }
     *     
     */
    public void setDividendValuationDates(AdjustableRelativeOrPeriodicDates value) {
        this.dividendValuationDates = value;
    }

    /**
     * Gets the value of the fpvFinalPriceElectionFallback property.
     * 
     * @return
     *     possible object is
     *     {@link FPVFinalPriceElectionFallbackEnum }
     *     
     */
    public FPVFinalPriceElectionFallbackEnum getFPVFinalPriceElectionFallback() {
        return fpvFinalPriceElectionFallback;
    }

    /**
     * Sets the value of the fpvFinalPriceElectionFallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link FPVFinalPriceElectionFallbackEnum }
     *     
     */
    public void setFPVFinalPriceElectionFallback(FPVFinalPriceElectionFallbackEnum value) {
        this.fpvFinalPriceElectionFallback = value;
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