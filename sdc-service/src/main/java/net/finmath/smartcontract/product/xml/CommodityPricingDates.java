//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * The dates on which prices are observed for the underlyer.
 * 
 * <p>Java class for CommodityPricingDates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityPricingDates"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CommodityCalculationPeriodsPointer.model"/&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="lag" type="{http://www.fpml.org/FpML-5/confirmation}Lag" minOccurs="0"/&gt;
 *             &lt;choice&gt;
 *               &lt;sequence&gt;
 *                 &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}Days.model"/&gt;
 *                 &lt;element name="businessCalendar" type="{http://www.fpml.org/FpML-5/confirmation}CommodityBusinessCalendar" minOccurs="0"/&gt;
 *                 &lt;element name="calendarSource" type="{http://www.fpml.org/FpML-5/confirmation}CalendarSourceEnum" minOccurs="0"/&gt;
 *               &lt;/sequence&gt;
 *               &lt;element name="settlementPeriods" type="{http://www.fpml.org/FpML-5/confirmation}SettlementPeriods" maxOccurs="unbounded"/&gt;
 *               &lt;element name="settlementPeriodsReference" type="{http://www.fpml.org/FpML-5/confirmation}SettlementPeriodsReference" maxOccurs="unbounded"/&gt;
 *             &lt;/choice&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="pricingDates" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableDates" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
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
@XmlType(name = "CommodityPricingDates", propOrder = {
    "calculationPeriodsReference",
    "calculationPeriodsScheduleReference",
    "calculationPeriodsDatesReference",
    "lag",
    "dayType",
    "dayDistribution",
    "dayCount",
    "dayOfWeek",
    "dayNumber",
    "businessDayConvention",
    "businessCalendar",
    "calendarSource",
    "settlementPeriods",
    "settlementPeriodsReference",
    "pricingDates"
})
public class CommodityPricingDates {

    protected CalculationPeriodsReference calculationPeriodsReference;
    protected CalculationPeriodsScheduleReference calculationPeriodsScheduleReference;
    protected CalculationPeriodsDatesReference calculationPeriodsDatesReference;
    protected Lag lag;
    protected String dayType;
    protected CommodityFrequencyType dayDistribution;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dayCount;
    @XmlSchemaType(name = "token")
    protected List<DayOfWeekEnum> dayOfWeek;
    protected BigInteger dayNumber;
    @XmlSchemaType(name = "token")
    protected BusinessDayConventionEnum businessDayConvention;
    protected CommodityBusinessCalendar businessCalendar;
    @XmlSchemaType(name = "token")
    protected CalendarSourceEnum calendarSource;
    protected List<SettlementPeriods> settlementPeriods;
    protected List<SettlementPeriodsReference> settlementPeriodsReference;
    protected List<AdjustableDates> pricingDates;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the calculationPeriodsReference property.
     * 
     * @return
     *     possible object is
     *     {@link CalculationPeriodsReference }
     *     
     */
    public CalculationPeriodsReference getCalculationPeriodsReference() {
        return calculationPeriodsReference;
    }

    /**
     * Sets the value of the calculationPeriodsReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalculationPeriodsReference }
     *     
     */
    public void setCalculationPeriodsReference(CalculationPeriodsReference value) {
        this.calculationPeriodsReference = value;
    }

    /**
     * Gets the value of the calculationPeriodsScheduleReference property.
     * 
     * @return
     *     possible object is
     *     {@link CalculationPeriodsScheduleReference }
     *     
     */
    public CalculationPeriodsScheduleReference getCalculationPeriodsScheduleReference() {
        return calculationPeriodsScheduleReference;
    }

    /**
     * Sets the value of the calculationPeriodsScheduleReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalculationPeriodsScheduleReference }
     *     
     */
    public void setCalculationPeriodsScheduleReference(CalculationPeriodsScheduleReference value) {
        this.calculationPeriodsScheduleReference = value;
    }

    /**
     * Gets the value of the calculationPeriodsDatesReference property.
     * 
     * @return
     *     possible object is
     *     {@link CalculationPeriodsDatesReference }
     *     
     */
    public CalculationPeriodsDatesReference getCalculationPeriodsDatesReference() {
        return calculationPeriodsDatesReference;
    }

    /**
     * Sets the value of the calculationPeriodsDatesReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalculationPeriodsDatesReference }
     *     
     */
    public void setCalculationPeriodsDatesReference(CalculationPeriodsDatesReference value) {
        this.calculationPeriodsDatesReference = value;
    }

    /**
     * Gets the value of the lag property.
     * 
     * @return
     *     possible object is
     *     {@link Lag }
     *     
     */
    public Lag getLag() {
        return lag;
    }

    /**
     * Sets the value of the lag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Lag }
     *     
     */
    public void setLag(Lag value) {
        this.lag = value;
    }

    /**
     * Gets the value of the dayType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayType() {
        return dayType;
    }

    /**
     * Sets the value of the dayType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayType(String value) {
        this.dayType = value;
    }

    /**
     * Gets the value of the dayDistribution property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityFrequencyType }
     *     
     */
    public CommodityFrequencyType getDayDistribution() {
        return dayDistribution;
    }

    /**
     * Sets the value of the dayDistribution property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityFrequencyType }
     *     
     */
    public void setDayDistribution(CommodityFrequencyType value) {
        this.dayDistribution = value;
    }

    /**
     * Gets the value of the dayCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDayCount() {
        return dayCount;
    }

    /**
     * Sets the value of the dayCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDayCount(BigInteger value) {
        this.dayCount = value;
    }

    /**
     * Gets the value of the dayOfWeek property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dayOfWeek property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDayOfWeek().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DayOfWeekEnum }
     * 
     * 
     */
    public List<DayOfWeekEnum> getDayOfWeek() {
        if (dayOfWeek == null) {
            dayOfWeek = new ArrayList<DayOfWeekEnum>();
        }
        return this.dayOfWeek;
    }

    /**
     * Gets the value of the dayNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDayNumber() {
        return dayNumber;
    }

    /**
     * Sets the value of the dayNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDayNumber(BigInteger value) {
        this.dayNumber = value;
    }

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
     * Gets the value of the businessCalendar property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityBusinessCalendar }
     *     
     */
    public CommodityBusinessCalendar getBusinessCalendar() {
        return businessCalendar;
    }

    /**
     * Sets the value of the businessCalendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityBusinessCalendar }
     *     
     */
    public void setBusinessCalendar(CommodityBusinessCalendar value) {
        this.businessCalendar = value;
    }

    /**
     * Gets the value of the calendarSource property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarSourceEnum }
     *     
     */
    public CalendarSourceEnum getCalendarSource() {
        return calendarSource;
    }

    /**
     * Sets the value of the calendarSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarSourceEnum }
     *     
     */
    public void setCalendarSource(CalendarSourceEnum value) {
        this.calendarSource = value;
    }

    /**
     * Gets the value of the settlementPeriods property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the settlementPeriods property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSettlementPeriods().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SettlementPeriods }
     * 
     * 
     */
    public List<SettlementPeriods> getSettlementPeriods() {
        if (settlementPeriods == null) {
            settlementPeriods = new ArrayList<SettlementPeriods>();
        }
        return this.settlementPeriods;
    }

    /**
     * Gets the value of the settlementPeriodsReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the settlementPeriodsReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSettlementPeriodsReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SettlementPeriodsReference }
     * 
     * 
     */
    public List<SettlementPeriodsReference> getSettlementPeriodsReference() {
        if (settlementPeriodsReference == null) {
            settlementPeriodsReference = new ArrayList<SettlementPeriodsReference>();
        }
        return this.settlementPeriodsReference;
    }

    /**
     * Gets the value of the pricingDates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the pricingDates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPricingDates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdjustableDates }
     * 
     * 
     */
    public List<AdjustableDates> getPricingDates() {
        if (pricingDates == null) {
            pricingDates = new ArrayList<AdjustableDates>();
        }
        return this.pricingDates;
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
