//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A full definition of the accrual characteristics of a loan contract. This structure defines both the underlying base rate as well as any additional margins and costs associated with the loan contract.
 * 
 * <p>Java class for FloatingRateAccrual complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FloatingRateAccrual"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}FloatingRateOptionBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}PeriodWithDays.model"/&gt;
 *         &lt;sequence minOccurs="0"&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="rateFixingDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *             &lt;element name="baseRate" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;sequence minOccurs="0"&gt;
 *             &lt;element name="penaltySpread" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *             &lt;element name="defaultSpread" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *             &lt;element name="mandatoryCostRate" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *             &lt;element name="allInRate" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *           &lt;/sequence&gt;
 *         &lt;/sequence&gt;
 *         &lt;element name="paymentProjection" type="{http://www.fpml.org/FpML-5/confirmation}PaymentProjection" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FloatingRateAccrual", propOrder = {
    "startDate",
    "endDate",
    "numberOfDays",
    "rateFixingDate",
    "baseRate",
    "penaltySpread",
    "defaultSpread",
    "mandatoryCostRate",
    "allInRate",
    "paymentProjection"
})
public class FloatingRateAccrual
    extends FloatingRateOptionBase
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
    @XmlElement(required = true)
    protected BigDecimal numberOfDays;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar rateFixingDate;
    protected BigDecimal baseRate;
    protected BigDecimal penaltySpread;
    protected BigDecimal defaultSpread;
    protected BigDecimal mandatoryCostRate;
    protected BigDecimal allInRate;
    protected PaymentProjection paymentProjection;

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
     * Gets the value of the numberOfDays property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfDays() {
        return numberOfDays;
    }

    /**
     * Sets the value of the numberOfDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfDays(BigDecimal value) {
        this.numberOfDays = value;
    }

    /**
     * Gets the value of the rateFixingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRateFixingDate() {
        return rateFixingDate;
    }

    /**
     * Sets the value of the rateFixingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRateFixingDate(XMLGregorianCalendar value) {
        this.rateFixingDate = value;
    }

    /**
     * Gets the value of the baseRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getBaseRate() {
        return baseRate;
    }

    /**
     * Sets the value of the baseRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setBaseRate(BigDecimal value) {
        this.baseRate = value;
    }

    /**
     * Gets the value of the penaltySpread property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPenaltySpread() {
        return penaltySpread;
    }

    /**
     * Sets the value of the penaltySpread property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPenaltySpread(BigDecimal value) {
        this.penaltySpread = value;
    }

    /**
     * Gets the value of the defaultSpread property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDefaultSpread() {
        return defaultSpread;
    }

    /**
     * Sets the value of the defaultSpread property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDefaultSpread(BigDecimal value) {
        this.defaultSpread = value;
    }

    /**
     * Gets the value of the mandatoryCostRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMandatoryCostRate() {
        return mandatoryCostRate;
    }

    /**
     * Sets the value of the mandatoryCostRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMandatoryCostRate(BigDecimal value) {
        this.mandatoryCostRate = value;
    }

    /**
     * Gets the value of the allInRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAllInRate() {
        return allInRate;
    }

    /**
     * Sets the value of the allInRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAllInRate(BigDecimal value) {
        this.allInRate = value;
    }

    /**
     * Gets the value of the paymentProjection property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentProjection }
     *     
     */
    public PaymentProjection getPaymentProjection() {
        return paymentProjection;
    }

    /**
     * Sets the value of the paymentProjection property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentProjection }
     *     
     */
    public void setPaymentProjection(PaymentProjection value) {
        this.paymentProjection = value;
    }

}
