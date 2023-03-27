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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * TBA
 * 
 * <p>Java class for SharedAmericanExercise complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SharedAmericanExercise"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Exercise"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="commencementDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrRelativeDate"/&gt;
 *         &lt;element name="expirationDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrRelativeDate"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="latestExerciseTime" type="{http://www.fpml.org/FpML-5/confirmation}BusinessCenterTime"/&gt;
 *           &lt;element name="latestExerciseTimeDetermination" type="{http://www.fpml.org/FpML-5/confirmation}DeterminationMethod"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SharedAmericanExercise", propOrder = {
    "commencementDate",
    "expirationDate",
    "latestExerciseTime",
    "latestExerciseTimeDetermination"
})
@XmlSeeAlso({
    EquityAmericanExercise.class,
    EquityBermudaExercise.class
})
public class SharedAmericanExercise
    extends Exercise
{

    @XmlElement(required = true)
    protected AdjustableOrRelativeDate commencementDate;
    @XmlElement(required = true)
    protected AdjustableOrRelativeDate expirationDate;
    protected BusinessCenterTime latestExerciseTime;
    protected DeterminationMethod latestExerciseTimeDetermination;

    /**
     * Gets the value of the commencementDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public AdjustableOrRelativeDate getCommencementDate() {
        return commencementDate;
    }

    /**
     * Sets the value of the commencementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public void setCommencementDate(AdjustableOrRelativeDate value) {
        this.commencementDate = value;
    }

    /**
     * Gets the value of the expirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public AdjustableOrRelativeDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public void setExpirationDate(AdjustableOrRelativeDate value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the latestExerciseTime property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCenterTime }
     *     
     */
    public BusinessCenterTime getLatestExerciseTime() {
        return latestExerciseTime;
    }

    /**
     * Sets the value of the latestExerciseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCenterTime }
     *     
     */
    public void setLatestExerciseTime(BusinessCenterTime value) {
        this.latestExerciseTime = value;
    }

    /**
     * Gets the value of the latestExerciseTimeDetermination property.
     * 
     * @return
     *     possible object is
     *     {@link DeterminationMethod }
     *     
     */
    public DeterminationMethod getLatestExerciseTimeDetermination() {
        return latestExerciseTimeDetermination;
    }

    /**
     * Sets the value of the latestExerciseTimeDetermination property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeterminationMethod }
     *     
     */
    public void setLatestExerciseTimeDetermination(DeterminationMethod value) {
        this.latestExerciseTimeDetermination = value;
    }

}