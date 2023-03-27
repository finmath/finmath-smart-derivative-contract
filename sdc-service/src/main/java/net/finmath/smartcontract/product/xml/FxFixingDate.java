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
 * A type that is extending the Offset structure for providing the ability to specify an FX fixing date as an offset to dates specified somewhere else in the document.
 * 
 * <p>Java class for FxFixingDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxFixingDate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Offset"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="businessDayConvention" type="{http://www.fpml.org/FpML-5/confirmation}BusinessDayConventionEnum"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}BusinessCentersOrReference.model" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="dateRelativeToPaymentDates" type="{http://www.fpml.org/FpML-5/confirmation}DateRelativeToPaymentDates"/&gt;
 *           &lt;element name="dateRelativeToCalculationPeriodDates" type="{http://www.fpml.org/FpML-5/confirmation}DateRelativeToCalculationPeriodDates"/&gt;
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
@XmlType(name = "FxFixingDate", propOrder = {
    "businessDayConvention",
    "businessCentersReference",
    "businessCenters",
    "dateRelativeToPaymentDates",
    "dateRelativeToCalculationPeriodDates"
})
public class FxFixingDate
    extends Offset
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected BusinessDayConventionEnum businessDayConvention;
    protected BusinessCentersReference businessCentersReference;
    protected BusinessCenters businessCenters;
    protected DateRelativeToPaymentDates dateRelativeToPaymentDates;
    protected DateRelativeToCalculationPeriodDates dateRelativeToCalculationPeriodDates;

    /**
     * Gets the value of the businessDayConvention property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessDayConventionEnum }
     *     
     */
    public BusinessDayConventionEnum getBusinessDayConvention() {
        return businessDayConvention;
    }

    /**
     * Sets the value of the businessDayConvention property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessDayConventionEnum }
     *     
     */
    public void setBusinessDayConvention(BusinessDayConventionEnum value) {
        this.businessDayConvention = value;
    }

    /**
     * Gets the value of the businessCentersReference property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCentersReference }
     *     
     */
    public BusinessCentersReference getBusinessCentersReference() {
        return businessCentersReference;
    }

    /**
     * Sets the value of the businessCentersReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCentersReference }
     *     
     */
    public void setBusinessCentersReference(BusinessCentersReference value) {
        this.businessCentersReference = value;
    }

    /**
     * Gets the value of the businessCenters property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCenters }
     *     
     */
    public BusinessCenters getBusinessCenters() {
        return businessCenters;
    }

    /**
     * Sets the value of the businessCenters property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCenters }
     *     
     */
    public void setBusinessCenters(BusinessCenters value) {
        this.businessCenters = value;
    }

    /**
     * Gets the value of the dateRelativeToPaymentDates property.
     * 
     * @return
     *     possible object is
     *     {@link DateRelativeToPaymentDates }
     *     
     */
    public DateRelativeToPaymentDates getDateRelativeToPaymentDates() {
        return dateRelativeToPaymentDates;
    }

    /**
     * Sets the value of the dateRelativeToPaymentDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateRelativeToPaymentDates }
     *     
     */
    public void setDateRelativeToPaymentDates(DateRelativeToPaymentDates value) {
        this.dateRelativeToPaymentDates = value;
    }

    /**
     * Gets the value of the dateRelativeToCalculationPeriodDates property.
     * 
     * @return
     *     possible object is
     *     {@link DateRelativeToCalculationPeriodDates }
     *     
     */
    public DateRelativeToCalculationPeriodDates getDateRelativeToCalculationPeriodDates() {
        return dateRelativeToCalculationPeriodDates;
    }

    /**
     * Sets the value of the dateRelativeToCalculationPeriodDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateRelativeToCalculationPeriodDates }
     *     
     */
    public void setDateRelativeToCalculationPeriodDates(DateRelativeToCalculationPeriodDates value) {
        this.dateRelativeToCalculationPeriodDates = value;
    }

}