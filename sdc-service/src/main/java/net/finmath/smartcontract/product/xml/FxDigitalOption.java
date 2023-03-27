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
 * Describes an option having a triggerable fixed payout.
 * 
 * <p>Java class for FxDigitalOption complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxDigitalOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Option"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="effectiveDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrRelativeDate" minOccurs="0"/&gt;
 *         &lt;element name="tenorPeriod" type="{http://www.fpml.org/FpML-5/confirmation}Period" minOccurs="0"/&gt;
 *         &lt;sequence&gt;
 *           &lt;choice&gt;
 *             &lt;sequence&gt;
 *               &lt;element name="americanExercise" type="{http://www.fpml.org/FpML-5/confirmation}FxDigitalAmericanExercise"/&gt;
 *               &lt;element name="touch" type="{http://www.fpml.org/FpML-5/confirmation}FxTouch" maxOccurs="unbounded"/&gt;
 *             &lt;/sequence&gt;
 *             &lt;sequence&gt;
 *               &lt;element name="europeanExercise" type="{http://www.fpml.org/FpML-5/confirmation}FxEuropeanExercise"/&gt;
 *               &lt;element name="trigger" type="{http://www.fpml.org/FpML-5/confirmation}FxTrigger" maxOccurs="unbounded"/&gt;
 *             &lt;/sequence&gt;
 *           &lt;/choice&gt;
 *           &lt;element name="exerciseProcedure" type="{http://www.fpml.org/FpML-5/confirmation}ExerciseProcedure" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;element name="payout" type="{http://www.fpml.org/FpML-5/confirmation}FxOptionPayout"/&gt;
 *         &lt;element name="premium" type="{http://www.fpml.org/FpML-5/confirmation}FxOptionPremium" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxDigitalOption", propOrder = {
    "effectiveDate",
    "tenorPeriod",
    "americanExercise",
    "touch",
    "europeanExercise",
    "trigger",
    "exerciseProcedure",
    "payout",
    "premium"
})
public class FxDigitalOption
    extends Option
{

    protected AdjustableOrRelativeDate effectiveDate;
    protected Period tenorPeriod;
    protected FxDigitalAmericanExercise americanExercise;
    protected List<FxTouch> touch;
    protected FxEuropeanExercise europeanExercise;
    protected List<FxTrigger> trigger;
    protected ExerciseProcedure exerciseProcedure;
    @XmlElement(required = true)
    protected FxOptionPayout payout;
    protected List<FxOptionPremium> premium;

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public AdjustableOrRelativeDate getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public void setEffectiveDate(AdjustableOrRelativeDate value) {
        this.effectiveDate = value;
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
     * Gets the value of the americanExercise property.
     * 
     * @return
     *     possible object is
     *     {@link FxDigitalAmericanExercise }
     *     
     */
    public FxDigitalAmericanExercise getAmericanExercise() {
        return americanExercise;
    }

    /**
     * Sets the value of the americanExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxDigitalAmericanExercise }
     *     
     */
    public void setAmericanExercise(FxDigitalAmericanExercise value) {
        this.americanExercise = value;
    }

    /**
     * Gets the value of the touch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the touch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTouch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxTouch }
     * 
     * 
     */
    public List<FxTouch> getTouch() {
        if (touch == null) {
            touch = new ArrayList<FxTouch>();
        }
        return this.touch;
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
     * Gets the value of the trigger property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the trigger property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrigger().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxTrigger }
     * 
     * 
     */
    public List<FxTrigger> getTrigger() {
        if (trigger == null) {
            trigger = new ArrayList<FxTrigger>();
        }
        return this.trigger;
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
     * Gets the value of the payout property.
     * 
     * @return
     *     possible object is
     *     {@link FxOptionPayout }
     *     
     */
    public FxOptionPayout getPayout() {
        return payout;
    }

    /**
     * Sets the value of the payout property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxOptionPayout }
     *     
     */
    public void setPayout(FxOptionPayout value) {
        this.payout = value;
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
     * {@link FxOptionPremium }
     * 
     * 
     */
    public List<FxOptionPremium> getPremium() {
        if (premium == null) {
            premium = new ArrayList<FxOptionPremium>();
        }
        return this.premium;
    }

}