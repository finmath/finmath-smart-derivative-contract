//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining an early termination provision where either or both parties have the right to exercise.
 * 
 * <p>Java class for OptionalEarlyTermination complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OptionalEarlyTermination"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="singlePartyOption" type="{http://www.fpml.org/FpML-5/confirmation}SinglePartyOption" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.fpml.org/FpML-5/confirmation}exercise"/&gt;
 *         &lt;element name="exerciseNotice" type="{http://www.fpml.org/FpML-5/confirmation}ExerciseNotice" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="followUpConfirmation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="calculationAgent" type="{http://www.fpml.org/FpML-5/confirmation}CalculationAgent"/&gt;
 *         &lt;element name="cashSettlement" type="{http://www.fpml.org/FpML-5/confirmation}CashSettlement"/&gt;
 *         &lt;element name="optionalEarlyTerminationAdjustedDates" type="{http://www.fpml.org/FpML-5/confirmation}OptionalEarlyTerminationAdjustedDates" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionalEarlyTermination", propOrder = {
    "singlePartyOption",
    "exercise",
    "exerciseNotice",
    "followUpConfirmation",
    "calculationAgent",
    "cashSettlement",
    "optionalEarlyTerminationAdjustedDates"
})
public class OptionalEarlyTermination {

    protected SinglePartyOption singlePartyOption;
    @XmlElementRef(name = "exercise", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class)
    protected JAXBElement<? extends Exercise> exercise;
    protected List<ExerciseNotice> exerciseNotice;
    protected Boolean followUpConfirmation;
    @XmlElement(required = true)
    protected CalculationAgent calculationAgent;
    @XmlElement(required = true)
    protected CashSettlement cashSettlement;
    protected OptionalEarlyTerminationAdjustedDates optionalEarlyTerminationAdjustedDates;

    /**
     * Gets the value of the singlePartyOption property.
     * 
     * @return
     *     possible object is
     *     {@link SinglePartyOption }
     *     
     */
    public SinglePartyOption getSinglePartyOption() {
        return singlePartyOption;
    }

    /**
     * Sets the value of the singlePartyOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link SinglePartyOption }
     *     
     */
    public void setSinglePartyOption(SinglePartyOption value) {
        this.singlePartyOption = value;
    }

    /**
     * Gets the value of the exercise property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AmericanExercise }{@code >}
     *     {@link JAXBElement }{@code <}{@link EuropeanExercise }{@code >}
     *     {@link JAXBElement }{@code <}{@link BermudaExercise }{@code >}
     *     {@link JAXBElement }{@code <}{@link Exercise }{@code >}
     *     
     */
    public JAXBElement<? extends Exercise> getExercise() {
        return exercise;
    }

    /**
     * Sets the value of the exercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AmericanExercise }{@code >}
     *     {@link JAXBElement }{@code <}{@link EuropeanExercise }{@code >}
     *     {@link JAXBElement }{@code <}{@link BermudaExercise }{@code >}
     *     {@link JAXBElement }{@code <}{@link Exercise }{@code >}
     *     
     */
    public void setExercise(JAXBElement<? extends Exercise> value) {
        this.exercise = value;
    }

    /**
     * Gets the value of the exerciseNotice property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the exerciseNotice property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExerciseNotice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExerciseNotice }
     * 
     * 
     */
    public List<ExerciseNotice> getExerciseNotice() {
        if (exerciseNotice == null) {
            exerciseNotice = new ArrayList<ExerciseNotice>();
        }
        return this.exerciseNotice;
    }

    /**
     * Gets the value of the followUpConfirmation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFollowUpConfirmation() {
        return followUpConfirmation;
    }

    /**
     * Sets the value of the followUpConfirmation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFollowUpConfirmation(Boolean value) {
        this.followUpConfirmation = value;
    }

    /**
     * Gets the value of the calculationAgent property.
     * 
     * @return
     *     possible object is
     *     {@link CalculationAgent }
     *     
     */
    public CalculationAgent getCalculationAgent() {
        return calculationAgent;
    }

    /**
     * Sets the value of the calculationAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalculationAgent }
     *     
     */
    public void setCalculationAgent(CalculationAgent value) {
        this.calculationAgent = value;
    }

    /**
     * Gets the value of the cashSettlement property.
     * 
     * @return
     *     possible object is
     *     {@link CashSettlement }
     *     
     */
    public CashSettlement getCashSettlement() {
        return cashSettlement;
    }

    /**
     * Sets the value of the cashSettlement property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashSettlement }
     *     
     */
    public void setCashSettlement(CashSettlement value) {
        this.cashSettlement = value;
    }

    /**
     * Gets the value of the optionalEarlyTerminationAdjustedDates property.
     * 
     * @return
     *     possible object is
     *     {@link OptionalEarlyTerminationAdjustedDates }
     *     
     */
    public OptionalEarlyTerminationAdjustedDates getOptionalEarlyTerminationAdjustedDates() {
        return optionalEarlyTerminationAdjustedDates;
    }

    /**
     * Sets the value of the optionalEarlyTerminationAdjustedDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link OptionalEarlyTerminationAdjustedDates }
     *     
     */
    public void setOptionalEarlyTerminationAdjustedDates(OptionalEarlyTerminationAdjustedDates value) {
        this.optionalEarlyTerminationAdjustedDates = value;
    }

}
