//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * An event representing the lender-specific payment of interest amounts for a given accrual period against a single loan contract.
 * 
 * <p>Java class for InterestPayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InterestPayment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}LoanContractEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}Period.model"/&gt;
 *         &lt;element name="calculationMethod" type="{http://www.fpml.org/FpML-5/confirmation}InterestCalculationMethodEnum"/&gt;
 *         &lt;element name="amount" type="{http://www.fpml.org/FpML-5/confirmation}MoneyWithParticipantShare"/&gt;
 *         &lt;sequence minOccurs="0"&gt;
 *           &lt;element name="accrualSchedule" type="{http://www.fpml.org/FpML-5/confirmation}AccrualPeriod" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="projection" type="{http://www.fpml.org/FpML-5/confirmation}PaymentProjection" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterestPayment", propOrder = {
    "startDate",
    "endDate",
    "calculationMethod",
    "amount",
    "accrualSchedule",
    "projection"
})
public class InterestPayment
    extends LoanContractEvent
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected InterestCalculationMethodEnum calculationMethod;
    @XmlElement(required = true)
    protected MoneyWithParticipantShare amount;
    protected List<AccrualPeriod> accrualSchedule;
    protected PaymentProjection projection;

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the calculationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link InterestCalculationMethodEnum }
     *     
     */
    public InterestCalculationMethodEnum getCalculationMethod() {
        return calculationMethod;
    }

    /**
     * Sets the value of the calculationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterestCalculationMethodEnum }
     *     
     */
    public void setCalculationMethod(InterestCalculationMethodEnum value) {
        this.calculationMethod = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link MoneyWithParticipantShare }
     *     
     */
    public MoneyWithParticipantShare getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MoneyWithParticipantShare }
     *     
     */
    public void setAmount(MoneyWithParticipantShare value) {
        this.amount = value;
    }

    /**
     * Gets the value of the accrualSchedule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the accrualSchedule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccrualSchedule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccrualPeriod }
     * 
     * 
     */
    public List<AccrualPeriod> getAccrualSchedule() {
        if (accrualSchedule == null) {
            accrualSchedule = new ArrayList<AccrualPeriod>();
        }
        return this.accrualSchedule;
    }

    /**
     * Gets the value of the projection property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentProjection }
     *     
     */
    public PaymentProjection getProjection() {
        return projection;
    }

    /**
     * Sets the value of the projection property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentProjection }
     *     
     */
    public void setProjection(PaymentProjection value) {
        this.projection = value;
    }

}