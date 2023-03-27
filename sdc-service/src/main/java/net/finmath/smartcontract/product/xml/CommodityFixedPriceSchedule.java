//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The Fixed Price for a given Calculation Period during the life of the trade. There must be a Fixed Price step specified for each Calculation Period, regardless of whether the Fixed Price changes or remains the same between periods.
 * 
 * <p>Java class for CommodityFixedPriceSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityFixedPriceSchedule"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="fixedPriceStep" type="{http://www.fpml.org/FpML-5/confirmation}FixedPrice" maxOccurs="unbounded"/&gt;
 *           &lt;element name="worldscaleRateStep" type="{http://www.w3.org/2001/XMLSchema}decimal" maxOccurs="unbounded"/&gt;
 *           &lt;element name="contractRateStep" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeMoney" maxOccurs="unbounded"/&gt;
 *           &lt;element name="settlementPeriodsPriceSchedule" type="{http://www.fpml.org/FpML-5/confirmation}CommoditySettlementPeriodsPriceSchedule" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CommodityCalculationPeriodsPointer.model"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommodityFixedPriceSchedule", propOrder = {
    "fixedPriceStep",
    "worldscaleRateStep",
    "contractRateStep",
    "settlementPeriodsPriceSchedule",
    "calculationPeriodsReference",
    "calculationPeriodsScheduleReference",
    "calculationPeriodsDatesReference"
})
public class CommodityFixedPriceSchedule {

    protected List<FixedPrice> fixedPriceStep;
    protected List<BigDecimal> worldscaleRateStep;
    protected List<NonNegativeMoney> contractRateStep;
    protected List<CommoditySettlementPeriodsPriceSchedule> settlementPeriodsPriceSchedule;
    protected CalculationPeriodsReference calculationPeriodsReference;
    protected CalculationPeriodsScheduleReference calculationPeriodsScheduleReference;
    protected CalculationPeriodsDatesReference calculationPeriodsDatesReference;

    /**
     * Gets the value of the fixedPriceStep property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the fixedPriceStep property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFixedPriceStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FixedPrice }
     * 
     * 
     */
    public List<FixedPrice> getFixedPriceStep() {
        if (fixedPriceStep == null) {
            fixedPriceStep = new ArrayList<FixedPrice>();
        }
        return this.fixedPriceStep;
    }

    /**
     * Gets the value of the worldscaleRateStep property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the worldscaleRateStep property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWorldscaleRateStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigDecimal }
     * 
     * 
     */
    public List<BigDecimal> getWorldscaleRateStep() {
        if (worldscaleRateStep == null) {
            worldscaleRateStep = new ArrayList<BigDecimal>();
        }
        return this.worldscaleRateStep;
    }

    /**
     * Gets the value of the contractRateStep property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the contractRateStep property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContractRateStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NonNegativeMoney }
     * 
     * 
     */
    public List<NonNegativeMoney> getContractRateStep() {
        if (contractRateStep == null) {
            contractRateStep = new ArrayList<NonNegativeMoney>();
        }
        return this.contractRateStep;
    }

    /**
     * Gets the value of the settlementPeriodsPriceSchedule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the settlementPeriodsPriceSchedule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSettlementPeriodsPriceSchedule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommoditySettlementPeriodsPriceSchedule }
     * 
     * 
     */
    public List<CommoditySettlementPeriodsPriceSchedule> getSettlementPeriodsPriceSchedule() {
        if (settlementPeriodsPriceSchedule == null) {
            settlementPeriodsPriceSchedule = new ArrayList<CommoditySettlementPeriodsPriceSchedule>();
        }
        return this.settlementPeriodsPriceSchedule;
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

}
