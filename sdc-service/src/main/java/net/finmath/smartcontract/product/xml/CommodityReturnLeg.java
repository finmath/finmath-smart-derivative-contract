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
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type describing the return leg of a commodity return swap.
 * 
 * <p>Java class for CommodityReturnLeg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityReturnLeg"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}CommodityPerformanceSwapLeg"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CommodityCalculationPeriods.model"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CommodityPaymentDates.model"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CommodityUnderlyerChoice.model"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="notionalAmount" type="{http://www.fpml.org/FpML-5/confirmation}CommodityNotionalAmount"/&gt;
 *           &lt;element name="notionalAmountReference" type="{http://www.fpml.org/FpML-5/confirmation}CommodityNotionalAmountReference"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="commodityReturnCalculation" type="{http://www.fpml.org/FpML-5/confirmation}CommodityReturnCalculation"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommodityReturnLeg", propOrder = {
    "calculationDates",
    "calculationPeriods",
    "calculationPeriodsSchedule",
    "calculationPeriodsReference",
    "calculationPeriodsScheduleReference",
    "calculationPeriodsDatesReference",
    "relativePaymentDates",
    "paymentDates",
    "masterAgreementPaymentDates",
    "commodity",
    "commodityBasket",
    "notionalAmount",
    "notionalAmountReference",
    "commodityReturnCalculation"
})
public class CommodityReturnLeg
    extends CommodityPerformanceSwapLeg
{

    protected AdjustableDates calculationDates;
    protected AdjustableDates calculationPeriods;
    protected CommodityCalculationPeriodsSchedule calculationPeriodsSchedule;
    protected CalculationPeriodsReference calculationPeriodsReference;
    protected CalculationPeriodsScheduleReference calculationPeriodsScheduleReference;
    protected CalculationPeriodsDatesReference calculationPeriodsDatesReference;
    protected CommodityRelativePaymentDates relativePaymentDates;
    protected AdjustableDatesOrRelativeDateOffset paymentDates;
    protected Boolean masterAgreementPaymentDates;
    protected Commodity commodity;
    protected CommodityBasket commodityBasket;
    protected CommodityNotionalAmount notionalAmount;
    protected CommodityNotionalAmountReference notionalAmountReference;
    @XmlElement(required = true)
    protected CommodityReturnCalculation commodityReturnCalculation;

    /**
     * Gets the value of the calculationDates property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableDates }
     *     
     */
    public AdjustableDates getCalculationDates() {
        return calculationDates;
    }

    /**
     * Sets the value of the calculationDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableDates }
     *     
     */
    public void setCalculationDates(AdjustableDates value) {
        this.calculationDates = value;
    }

    /**
     * Gets the value of the calculationPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableDates }
     *     
     */
    public AdjustableDates getCalculationPeriods() {
        return calculationPeriods;
    }

    /**
     * Sets the value of the calculationPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableDates }
     *     
     */
    public void setCalculationPeriods(AdjustableDates value) {
        this.calculationPeriods = value;
    }

    /**
     * Gets the value of the calculationPeriodsSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityCalculationPeriodsSchedule }
     *     
     */
    public CommodityCalculationPeriodsSchedule getCalculationPeriodsSchedule() {
        return calculationPeriodsSchedule;
    }

    /**
     * Sets the value of the calculationPeriodsSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityCalculationPeriodsSchedule }
     *     
     */
    public void setCalculationPeriodsSchedule(CommodityCalculationPeriodsSchedule value) {
        this.calculationPeriodsSchedule = value;
    }

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
     * Gets the value of the relativePaymentDates property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityRelativePaymentDates }
     *     
     */
    public CommodityRelativePaymentDates getRelativePaymentDates() {
        return relativePaymentDates;
    }

    /**
     * Sets the value of the relativePaymentDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityRelativePaymentDates }
     *     
     */
    public void setRelativePaymentDates(CommodityRelativePaymentDates value) {
        this.relativePaymentDates = value;
    }

    /**
     * Gets the value of the paymentDates property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableDatesOrRelativeDateOffset }
     *     
     */
    public AdjustableDatesOrRelativeDateOffset getPaymentDates() {
        return paymentDates;
    }

    /**
     * Sets the value of the paymentDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableDatesOrRelativeDateOffset }
     *     
     */
    public void setPaymentDates(AdjustableDatesOrRelativeDateOffset value) {
        this.paymentDates = value;
    }

    /**
     * Gets the value of the masterAgreementPaymentDates property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMasterAgreementPaymentDates() {
        return masterAgreementPaymentDates;
    }

    /**
     * Sets the value of the masterAgreementPaymentDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMasterAgreementPaymentDates(Boolean value) {
        this.masterAgreementPaymentDates = value;
    }

    /**
     * Gets the value of the commodity property.
     * 
     * @return
     *     possible object is
     *     {@link Commodity }
     *     
     */
    public Commodity getCommodity() {
        return commodity;
    }

    /**
     * Sets the value of the commodity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Commodity }
     *     
     */
    public void setCommodity(Commodity value) {
        this.commodity = value;
    }

    /**
     * Gets the value of the commodityBasket property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityBasket }
     *     
     */
    public CommodityBasket getCommodityBasket() {
        return commodityBasket;
    }

    /**
     * Sets the value of the commodityBasket property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityBasket }
     *     
     */
    public void setCommodityBasket(CommodityBasket value) {
        this.commodityBasket = value;
    }

    /**
     * Gets the value of the notionalAmount property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityNotionalAmount }
     *     
     */
    public CommodityNotionalAmount getNotionalAmount() {
        return notionalAmount;
    }

    /**
     * Sets the value of the notionalAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityNotionalAmount }
     *     
     */
    public void setNotionalAmount(CommodityNotionalAmount value) {
        this.notionalAmount = value;
    }

    /**
     * Gets the value of the notionalAmountReference property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityNotionalAmountReference }
     *     
     */
    public CommodityNotionalAmountReference getNotionalAmountReference() {
        return notionalAmountReference;
    }

    /**
     * Sets the value of the notionalAmountReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityNotionalAmountReference }
     *     
     */
    public void setNotionalAmountReference(CommodityNotionalAmountReference value) {
        this.notionalAmountReference = value;
    }

    /**
     * Gets the value of the commodityReturnCalculation property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityReturnCalculation }
     *     
     */
    public CommodityReturnCalculation getCommodityReturnCalculation() {
        return commodityReturnCalculation;
    }

    /**
     * Sets the value of the commodityReturnCalculation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityReturnCalculation }
     *     
     */
    public void setCommodityReturnCalculation(CommodityReturnCalculation value) {
        this.commodityReturnCalculation = value;
    }

}
