//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Describes an FX volatility and variance swap.
 * 
 * <p>Java class for FxPerformanceSwap complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxPerformanceSwap"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Product"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="quotedCurrencyPair" type="{http://www.fpml.org/FpML-5/confirmation}QuotedCurrencyPair"/&gt;
 *         &lt;element name="vegaNotional" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeMoney"/&gt;
 *         &lt;element name="notional" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeMoney" minOccurs="0"/&gt;
 *         &lt;element name="fixedLeg" type="{http://www.fpml.org/FpML-5/confirmation}FxPerformanceFixedLeg"/&gt;
 *         &lt;element name="floatingLeg" type="{http://www.fpml.org/FpML-5/confirmation}FxPerformanceFloatingLeg"/&gt;
 *         &lt;element name="fixingInformationSource" type="{http://www.fpml.org/FpML-5/confirmation}FxSpotRateSource"/&gt;
 *         &lt;element name="fixingSchedule" type="{http://www.fpml.org/FpML-5/confirmation}FxFixingScheduleSimple"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="valuationDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *           &lt;element name="valuationDateOffset" type="{http://www.fpml.org/FpML-5/confirmation}FxValuationDateOffset"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="settlementDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrAdjustedDate"/&gt;
 *         &lt;element name="annualizationFactor" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="meanAdjustment" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="numberOfReturns" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="additionalPayment" type="{http://www.fpml.org/FpML-5/confirmation}Payment" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="cashSettlement" type="{http://www.fpml.org/FpML-5/confirmation}FxCashSettlementSimple" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxPerformanceSwap", propOrder = {
    "quotedCurrencyPair",
    "vegaNotional",
    "notional",
    "fixedLeg",
    "floatingLeg",
    "fixingInformationSource",
    "fixingSchedule",
    "valuationDate",
    "valuationDateOffset",
    "settlementDate",
    "annualizationFactor",
    "meanAdjustment",
    "numberOfReturns",
    "additionalPayment",
    "cashSettlement"
})
public class FxPerformanceSwap
    extends Product
{

    @XmlElement(required = true)
    protected QuotedCurrencyPair quotedCurrencyPair;
    @XmlElement(required = true)
    protected NonNegativeMoney vegaNotional;
    protected NonNegativeMoney notional;
    @XmlElement(required = true)
    protected FxPerformanceFixedLeg fixedLeg;
    @XmlElement(required = true)
    protected FxPerformanceFloatingLeg floatingLeg;
    @XmlElement(required = true)
    protected FxSpotRateSource fixingInformationSource;
    @XmlElement(required = true)
    protected FxFixingScheduleSimple fixingSchedule;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar valuationDate;
    protected FxValuationDateOffset valuationDateOffset;
    @XmlElement(required = true)
    protected AdjustableOrAdjustedDate settlementDate;
    @XmlElement(required = true)
    protected BigDecimal annualizationFactor;
    protected boolean meanAdjustment;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfReturns;
    protected List<Payment> additionalPayment;
    protected FxCashSettlementSimple cashSettlement;

    /**
     * Gets the value of the quotedCurrencyPair property.
     * 
     * @return
     *     possible object is
     *     {@link QuotedCurrencyPair }
     *     
     */
    public QuotedCurrencyPair getQuotedCurrencyPair() {
        return quotedCurrencyPair;
    }

    /**
     * Sets the value of the quotedCurrencyPair property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuotedCurrencyPair }
     *     
     */
    public void setQuotedCurrencyPair(QuotedCurrencyPair value) {
        this.quotedCurrencyPair = value;
    }

    /**
     * Gets the value of the vegaNotional property.
     * 
     * @return
     *     possible object is
     *     {@link NonNegativeMoney }
     *     
     */
    public NonNegativeMoney getVegaNotional() {
        return vegaNotional;
    }

    /**
     * Sets the value of the vegaNotional property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonNegativeMoney }
     *     
     */
    public void setVegaNotional(NonNegativeMoney value) {
        this.vegaNotional = value;
    }

    /**
     * Gets the value of the notional property.
     * 
     * @return
     *     possible object is
     *     {@link NonNegativeMoney }
     *     
     */
    public NonNegativeMoney getNotional() {
        return notional;
    }

    /**
     * Sets the value of the notional property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonNegativeMoney }
     *     
     */
    public void setNotional(NonNegativeMoney value) {
        this.notional = value;
    }

    /**
     * Gets the value of the fixedLeg property.
     * 
     * @return
     *     possible object is
     *     {@link FxPerformanceFixedLeg }
     *     
     */
    public FxPerformanceFixedLeg getFixedLeg() {
        return fixedLeg;
    }

    /**
     * Sets the value of the fixedLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxPerformanceFixedLeg }
     *     
     */
    public void setFixedLeg(FxPerformanceFixedLeg value) {
        this.fixedLeg = value;
    }

    /**
     * Gets the value of the floatingLeg property.
     * 
     * @return
     *     possible object is
     *     {@link FxPerformanceFloatingLeg }
     *     
     */
    public FxPerformanceFloatingLeg getFloatingLeg() {
        return floatingLeg;
    }

    /**
     * Sets the value of the floatingLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxPerformanceFloatingLeg }
     *     
     */
    public void setFloatingLeg(FxPerformanceFloatingLeg value) {
        this.floatingLeg = value;
    }

    /**
     * Gets the value of the fixingInformationSource property.
     * 
     * @return
     *     possible object is
     *     {@link FxSpotRateSource }
     *     
     */
    public FxSpotRateSource getFixingInformationSource() {
        return fixingInformationSource;
    }

    /**
     * Sets the value of the fixingInformationSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxSpotRateSource }
     *     
     */
    public void setFixingInformationSource(FxSpotRateSource value) {
        this.fixingInformationSource = value;
    }

    /**
     * Gets the value of the fixingSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link FxFixingScheduleSimple }
     *     
     */
    public FxFixingScheduleSimple getFixingSchedule() {
        return fixingSchedule;
    }

    /**
     * Sets the value of the fixingSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxFixingScheduleSimple }
     *     
     */
    public void setFixingSchedule(FxFixingScheduleSimple value) {
        this.fixingSchedule = value;
    }

    /**
     * Gets the value of the valuationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValuationDate() {
        return valuationDate;
    }

    /**
     * Sets the value of the valuationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValuationDate(XMLGregorianCalendar value) {
        this.valuationDate = value;
    }

    /**
     * Gets the value of the valuationDateOffset property.
     * 
     * @return
     *     possible object is
     *     {@link FxValuationDateOffset }
     *     
     */
    public FxValuationDateOffset getValuationDateOffset() {
        return valuationDateOffset;
    }

    /**
     * Sets the value of the valuationDateOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxValuationDateOffset }
     *     
     */
    public void setValuationDateOffset(FxValuationDateOffset value) {
        this.valuationDateOffset = value;
    }

    /**
     * Gets the value of the settlementDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrAdjustedDate }
     *     
     */
    public AdjustableOrAdjustedDate getSettlementDate() {
        return settlementDate;
    }

    /**
     * Sets the value of the settlementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrAdjustedDate }
     *     
     */
    public void setSettlementDate(AdjustableOrAdjustedDate value) {
        this.settlementDate = value;
    }

    /**
     * Gets the value of the annualizationFactor property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAnnualizationFactor() {
        return annualizationFactor;
    }

    /**
     * Sets the value of the annualizationFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAnnualizationFactor(BigDecimal value) {
        this.annualizationFactor = value;
    }

    /**
     * Gets the value of the meanAdjustment property.
     * 
     */
    public boolean isMeanAdjustment() {
        return meanAdjustment;
    }

    /**
     * Sets the value of the meanAdjustment property.
     * 
     */
    public void setMeanAdjustment(boolean value) {
        this.meanAdjustment = value;
    }

    /**
     * Gets the value of the numberOfReturns property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfReturns() {
        return numberOfReturns;
    }

    /**
     * Sets the value of the numberOfReturns property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfReturns(BigInteger value) {
        this.numberOfReturns = value;
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
     * Gets the value of the cashSettlement property.
     * 
     * @return
     *     possible object is
     *     {@link FxCashSettlementSimple }
     *     
     */
    public FxCashSettlementSimple getCashSettlement() {
        return cashSettlement;
    }

    /**
     * Sets the value of the cashSettlement property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxCashSettlementSimple }
     *     
     */
    public void setCashSettlement(FxCashSettlementSimple value) {
        this.cashSettlement = value;
    }

}
