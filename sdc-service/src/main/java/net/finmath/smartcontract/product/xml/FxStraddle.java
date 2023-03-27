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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Straddle details. Straddle is composed of two options: a call and a put involving the quotedCurrencyPair.
 * 
 * <p>Java class for FxStraddle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxStraddle"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="straddleType" type="{http://www.fpml.org/FpML-5/confirmation}FxStraddleTypeEnum"/&gt;
 *         &lt;element name="tenorPeriod" type="{http://www.fpml.org/FpML-5/confirmation}Period" minOccurs="0"/&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="europeanExercise" type="{http://www.fpml.org/FpML-5/confirmation}FxEuropeanExercise"/&gt;
 *           &lt;element name="exerciseProcedure" type="{http://www.fpml.org/FpML-5/confirmation}ExerciseProcedure" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="notional" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeMoney"/&gt;
 *           &lt;element name="counterCurrency" type="{http://www.fpml.org/FpML-5/confirmation}Currency"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;element name="premium" type="{http://www.fpml.org/FpML-5/confirmation}FxStraddlePremium" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="settlementDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrAdjustedDate" minOccurs="0"/&gt;
 *         &lt;element name="cashSettlement" type="{http://www.fpml.org/FpML-5/confirmation}FxCashSettlementSimple" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxStraddle", propOrder = {
    "straddleType",
    "tenorPeriod",
    "europeanExercise",
    "exerciseProcedure",
    "notional",
    "counterCurrency",
    "premium",
    "settlementDate",
    "cashSettlement"
})
public class FxStraddle {

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected FxStraddleTypeEnum straddleType;
    protected Period tenorPeriod;
    @XmlElement(required = true)
    protected FxEuropeanExercise europeanExercise;
    protected ExerciseProcedure exerciseProcedure;
    @XmlElement(required = true)
    protected NonNegativeMoney notional;
    @XmlElement(required = true)
    protected Currency counterCurrency;
    protected List<FxStraddlePremium> premium;
    protected AdjustableOrAdjustedDate settlementDate;
    protected FxCashSettlementSimple cashSettlement;

    /**
     * Gets the value of the straddleType property.
     * 
     * @return
     *     possible object is
     *     {@link FxStraddleTypeEnum }
     *     
     */
    public FxStraddleTypeEnum getStraddleType() {
        return straddleType;
    }

    /**
     * Sets the value of the straddleType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxStraddleTypeEnum }
     *     
     */
    public void setStraddleType(FxStraddleTypeEnum value) {
        this.straddleType = value;
    }

    /**
     * Gets the value of the tenorPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getTenorPeriod() {
        return tenorPeriod;
    }

    /**
     * Sets the value of the tenorPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setTenorPeriod(Period value) {
        this.tenorPeriod = value;
    }

    /**
     * Gets the value of the europeanExercise property.
     * 
     * @return
     *     possible object is
     *     {@link FxEuropeanExercise }
     *     
     */
    public FxEuropeanExercise getEuropeanExercise() {
        return europeanExercise;
    }

    /**
     * Sets the value of the europeanExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxEuropeanExercise }
     *     
     */
    public void setEuropeanExercise(FxEuropeanExercise value) {
        this.europeanExercise = value;
    }

    /**
     * Gets the value of the exerciseProcedure property.
     * 
     * @return
     *     possible object is
     *     {@link ExerciseProcedure }
     *     
     */
    public ExerciseProcedure getExerciseProcedure() {
        return exerciseProcedure;
    }

    /**
     * Sets the value of the exerciseProcedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExerciseProcedure }
     *     
     */
    public void setExerciseProcedure(ExerciseProcedure value) {
        this.exerciseProcedure = value;
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
     * Gets the value of the counterCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCounterCurrency() {
        return counterCurrency;
    }

    /**
     * Sets the value of the counterCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCounterCurrency(Currency value) {
        this.counterCurrency = value;
    }

    /**
     * Gets the value of the premium property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the premium property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremium().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxStraddlePremium }
     * 
     * 
     */
    public List<FxStraddlePremium> getPremium() {
        if (premium == null) {
            premium = new ArrayList<FxStraddlePremium>();
        }
        return this.premium;
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
