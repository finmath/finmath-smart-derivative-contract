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
 * A type for defining exercise procedures for equity options.
 * 
 * <p>Java class for EquityExerciseValuationSettlement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EquityExerciseValuationSettlement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="equityEuropeanExercise" type="{http://www.fpml.org/FpML-5/confirmation}EquityEuropeanExercise"/&gt;
 *           &lt;element name="equityAmericanExercise" type="{http://www.fpml.org/FpML-5/confirmation}EquityAmericanExercise"/&gt;
 *           &lt;element name="equityBermudaExercise" type="{http://www.fpml.org/FpML-5/confirmation}EquityBermudaExercise"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="automaticExercise" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *             &lt;element name="makeWholeProvisions" type="{http://www.fpml.org/FpML-5/confirmation}MakeWholeProvisions" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="prePayment" type="{http://www.fpml.org/FpML-5/confirmation}PrePayment"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="equityValuation" type="{http://www.fpml.org/FpML-5/confirmation}EquityValuation"/&gt;
 *         &lt;element name="settlementDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrRelativeDate" minOccurs="0"/&gt;
 *         &lt;element name="settlementCurrency" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;element name="settlementPriceSource" type="{http://www.fpml.org/FpML-5/confirmation}SettlementPriceSource" minOccurs="0"/&gt;
 *         &lt;element name="settlementType" type="{http://www.fpml.org/FpML-5/confirmation}SettlementTypeEnum"/&gt;
 *         &lt;element name="settlementMethodElectionDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrRelativeDate" minOccurs="0"/&gt;
 *         &lt;element name="settlementMethodElectingPartyReference" type="{http://www.fpml.org/FpML-5/confirmation}PartyReference" minOccurs="0"/&gt;
 *         &lt;element name="settlementPriceDefaultElection" type="{http://www.fpml.org/FpML-5/confirmation}SettlementPriceDefaultElection" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EquityExerciseValuationSettlement", propOrder = {
    "equityEuropeanExercise",
    "equityAmericanExercise",
    "equityBermudaExercise",
    "automaticExercise",
    "makeWholeProvisions",
    "prePayment",
    "equityValuation",
    "settlementDate",
    "settlementCurrency",
    "settlementPriceSource",
    "settlementType",
    "settlementMethodElectionDate",
    "settlementMethodElectingPartyReference",
    "settlementPriceDefaultElection"
})
public class EquityExerciseValuationSettlement {

    protected EquityEuropeanExercise equityEuropeanExercise;
    protected EquityAmericanExercise equityAmericanExercise;
    protected EquityBermudaExercise equityBermudaExercise;
    protected Boolean automaticExercise;
    protected MakeWholeProvisions makeWholeProvisions;
    protected PrePayment prePayment;
    @XmlElement(required = true)
    protected EquityValuation equityValuation;
    protected AdjustableOrRelativeDate settlementDate;
    @XmlElement(required = true)
    protected Currency settlementCurrency;
    protected SettlementPriceSource settlementPriceSource;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected SettlementTypeEnum settlementType;
    protected AdjustableOrRelativeDate settlementMethodElectionDate;
    protected PartyReference settlementMethodElectingPartyReference;
    protected SettlementPriceDefaultElection settlementPriceDefaultElection;

    /**
     * Gets the value of the equityEuropeanExercise property.
     * 
     * @return
     *     possible object is
     *     {@link EquityEuropeanExercise }
     *     
     */
    public EquityEuropeanExercise getEquityEuropeanExercise() {
        return equityEuropeanExercise;
    }

    /**
     * Sets the value of the equityEuropeanExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityEuropeanExercise }
     *     
     */
    public void setEquityEuropeanExercise(EquityEuropeanExercise value) {
        this.equityEuropeanExercise = value;
    }

    /**
     * Gets the value of the equityAmericanExercise property.
     * 
     * @return
     *     possible object is
     *     {@link EquityAmericanExercise }
     *     
     */
    public EquityAmericanExercise getEquityAmericanExercise() {
        return equityAmericanExercise;
    }

    /**
     * Sets the value of the equityAmericanExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityAmericanExercise }
     *     
     */
    public void setEquityAmericanExercise(EquityAmericanExercise value) {
        this.equityAmericanExercise = value;
    }

    /**
     * Gets the value of the equityBermudaExercise property.
     * 
     * @return
     *     possible object is
     *     {@link EquityBermudaExercise }
     *     
     */
    public EquityBermudaExercise getEquityBermudaExercise() {
        return equityBermudaExercise;
    }

    /**
     * Sets the value of the equityBermudaExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityBermudaExercise }
     *     
     */
    public void setEquityBermudaExercise(EquityBermudaExercise value) {
        this.equityBermudaExercise = value;
    }

    /**
     * Gets the value of the automaticExercise property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutomaticExercise() {
        return automaticExercise;
    }

    /**
     * Sets the value of the automaticExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutomaticExercise(Boolean value) {
        this.automaticExercise = value;
    }

    /**
     * Gets the value of the makeWholeProvisions property.
     * 
     * @return
     *     possible object is
     *     {@link MakeWholeProvisions }
     *     
     */
    public MakeWholeProvisions getMakeWholeProvisions() {
        return makeWholeProvisions;
    }

    /**
     * Sets the value of the makeWholeProvisions property.
     * 
     * @param value
     *     allowed object is
     *     {@link MakeWholeProvisions }
     *     
     */
    public void setMakeWholeProvisions(MakeWholeProvisions value) {
        this.makeWholeProvisions = value;
    }

    /**
     * Gets the value of the prePayment property.
     * 
     * @return
     *     possible object is
     *     {@link PrePayment }
     *     
     */
    public PrePayment getPrePayment() {
        return prePayment;
    }

    /**
     * Sets the value of the prePayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrePayment }
     *     
     */
    public void setPrePayment(PrePayment value) {
        this.prePayment = value;
    }

    /**
     * Gets the value of the equityValuation property.
     * 
     * @return
     *     possible object is
     *     {@link EquityValuation }
     *     
     */
    public EquityValuation getEquityValuation() {
        return equityValuation;
    }

    /**
     * Sets the value of the equityValuation property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityValuation }
     *     
     */
    public void setEquityValuation(EquityValuation value) {
        this.equityValuation = value;
    }

    /**
     * Gets the value of the settlementDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public AdjustableOrRelativeDate getSettlementDate() {
        return settlementDate;
    }

    /**
     * Sets the value of the settlementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public void setSettlementDate(AdjustableOrRelativeDate value) {
        this.settlementDate = value;
    }

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
     * Gets the value of the settlementPriceSource property.
     * 
     * @return
     *     possible object is
     *     {@link SettlementPriceSource }
     *     
     */
    public SettlementPriceSource getSettlementPriceSource() {
        return settlementPriceSource;
    }

    /**
     * Sets the value of the settlementPriceSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementPriceSource }
     *     
     */
    public void setSettlementPriceSource(SettlementPriceSource value) {
        this.settlementPriceSource = value;
    }

    /**
     * Gets the value of the settlementType property.
     * 
     * @return
     *     possible object is
     *     {@link SettlementTypeEnum }
     *     
     */
    public SettlementTypeEnum getSettlementType() {
        return settlementType;
    }

    /**
     * Sets the value of the settlementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementTypeEnum }
     *     
     */
    public void setSettlementType(SettlementTypeEnum value) {
        this.settlementType = value;
    }

    /**
     * Gets the value of the settlementMethodElectionDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public AdjustableOrRelativeDate getSettlementMethodElectionDate() {
        return settlementMethodElectionDate;
    }

    /**
     * Sets the value of the settlementMethodElectionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public void setSettlementMethodElectionDate(AdjustableOrRelativeDate value) {
        this.settlementMethodElectionDate = value;
    }

    /**
     * Gets the value of the settlementMethodElectingPartyReference property.
     * 
     * @return
     *     possible object is
     *     {@link PartyReference }
     *     
     */
    public PartyReference getSettlementMethodElectingPartyReference() {
        return settlementMethodElectingPartyReference;
    }

    /**
     * Sets the value of the settlementMethodElectingPartyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyReference }
     *     
     */
    public void setSettlementMethodElectingPartyReference(PartyReference value) {
        this.settlementMethodElectingPartyReference = value;
    }

    /**
     * Gets the value of the settlementPriceDefaultElection property.
     * 
     * @return
     *     possible object is
     *     {@link SettlementPriceDefaultElection }
     *     
     */
    public SettlementPriceDefaultElection getSettlementPriceDefaultElection() {
        return settlementPriceDefaultElection;
    }

    /**
     * Sets the value of the settlementPriceDefaultElection property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementPriceDefaultElection }
     *     
     */
    public void setSettlementPriceDefaultElection(SettlementPriceDefaultElection value) {
        this.settlementPriceDefaultElection = value;
    }

}