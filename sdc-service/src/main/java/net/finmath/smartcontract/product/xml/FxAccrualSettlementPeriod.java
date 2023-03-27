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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FxAccrualSettlementPeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxAccrualSettlementPeriod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}SettlementPeriod"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="payoff" type="{http://www.fpml.org/FpML-5/confirmation}FxAccrualSettlementPeriodPayoff" maxOccurs="unbounded"/&gt;
 *         &lt;element name="accrualFactor" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="barrier" type="{http://www.fpml.org/FpML-5/confirmation}FxSettlementPeriodBarrier" minOccurs="0"/&gt;
 *         &lt;element name="accrualFixingDates" type="{http://www.fpml.org/FpML-5/confirmation}SettlementPeriodFixingDates"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxAccrualSettlementPeriod", propOrder = {
    "payoff",
    "accrualFactor",
    "barrier",
    "accrualFixingDates"
})
public class FxAccrualSettlementPeriod
    extends SettlementPeriod
{

    @XmlElement(required = true)
    protected List<FxAccrualSettlementPeriodPayoff> payoff;
    protected BigDecimal accrualFactor;
    protected FxSettlementPeriodBarrier barrier;
    @XmlElement(required = true)
    protected SettlementPeriodFixingDates accrualFixingDates;

    /**
     * Gets the value of the payoff property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the payoff property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayoff().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxAccrualSettlementPeriodPayoff }
     * 
     * 
     */
    public List<FxAccrualSettlementPeriodPayoff> getPayoff() {
        if (payoff == null) {
            payoff = new ArrayList<FxAccrualSettlementPeriodPayoff>();
        }
        return this.payoff;
    }

    /**
     * Gets the value of the accrualFactor property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAccrualFactor() {
        return accrualFactor;
    }

    /**
     * Sets the value of the accrualFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAccrualFactor(BigDecimal value) {
        this.accrualFactor = value;
    }

    /**
     * Gets the value of the barrier property.
     * 
     * @return
     *     possible object is
     *     {@link FxSettlementPeriodBarrier }
     *     
     */
    public FxSettlementPeriodBarrier getBarrier() {
        return barrier;
    }

    /**
     * Sets the value of the barrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxSettlementPeriodBarrier }
     *     
     */
    public void setBarrier(FxSettlementPeriodBarrier value) {
        this.barrier = value;
    }

    /**
     * Gets the value of the accrualFixingDates property.
     * 
     * @return
     *     possible object is
     *     {@link SettlementPeriodFixingDates }
     *     
     */
    public SettlementPeriodFixingDates getAccrualFixingDates() {
        return accrualFixingDates;
    }

    /**
     * Sets the value of the accrualFixingDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementPeriodFixingDates }
     *     
     */
    public void setAccrualFixingDates(SettlementPeriodFixingDates value) {
        this.accrualFixingDates = value;
    }

}