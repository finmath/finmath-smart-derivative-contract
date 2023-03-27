//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DividendSwapOptionTransactionSupplement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DividendSwapOptionTransactionSupplement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}OptionBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="equityPremium" type="{http://www.fpml.org/FpML-5/confirmation}EquityPremium"/&gt;
 *         &lt;element name="equityExercise" type="{http://www.fpml.org/FpML-5/confirmation}EquityExerciseValuationSettlement"/&gt;
 *         &lt;element name="exchangeLookAlike" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="methodOfAdjustment" type="{http://www.fpml.org/FpML-5/confirmation}MethodOfAdjustmentEnum" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="optionEntitlement" type="{http://www.fpml.org/FpML-5/confirmation}PositiveDecimal"/&gt;
 *           &lt;element name="multiplier" type="{http://www.fpml.org/FpML-5/confirmation}PositiveDecimal"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="clearingInstructions" type="{http://www.fpml.org/FpML-5/confirmation}SwaptionPhysicalSettlement" minOccurs="0"/&gt;
 *         &lt;element name="dividendSwapTransactionSupplement" type="{http://www.fpml.org/FpML-5/confirmation}DividendSwapTransactionSupplement"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DividendSwapOptionTransactionSupplement", propOrder = {
    "equityPremium",
    "equityExercise",
    "exchangeLookAlike",
    "methodOfAdjustment",
    "optionEntitlement",
    "multiplier",
    "clearingInstructions",
    "dividendSwapTransactionSupplement"
})
public class DividendSwapOptionTransactionSupplement
    extends OptionBase
{

    @XmlElement(required = true)
    protected EquityPremium equityPremium;
    @XmlElement(required = true)
    protected EquityExerciseValuationSettlement equityExercise;
    protected Boolean exchangeLookAlike;
    @XmlSchemaType(name = "token")
    protected MethodOfAdjustmentEnum methodOfAdjustment;
    protected BigDecimal optionEntitlement;
    protected BigDecimal multiplier;
    protected SwaptionPhysicalSettlement clearingInstructions;
    @XmlElement(required = true)
    protected DividendSwapTransactionSupplement dividendSwapTransactionSupplement;

    /**
     * Gets the value of the equityPremium property.
     * 
     * @return
     *     possible object is
     *     {@link EquityPremium }
     *     
     */
    public EquityPremium getEquityPremium() {
        return equityPremium;
    }

    /**
     * Sets the value of the equityPremium property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityPremium }
     *     
     */
    public void setEquityPremium(EquityPremium value) {
        this.equityPremium = value;
    }

    /**
     * Gets the value of the equityExercise property.
     * 
     * @return
     *     possible object is
     *     {@link EquityExerciseValuationSettlement }
     *     
     */
    public EquityExerciseValuationSettlement getEquityExercise() {
        return equityExercise;
    }

    /**
     * Sets the value of the equityExercise property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityExerciseValuationSettlement }
     *     
     */
    public void setEquityExercise(EquityExerciseValuationSettlement value) {
        this.equityExercise = value;
    }

    /**
     * Gets the value of the exchangeLookAlike property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExchangeLookAlike() {
        return exchangeLookAlike;
    }

    /**
     * Sets the value of the exchangeLookAlike property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExchangeLookAlike(Boolean value) {
        this.exchangeLookAlike = value;
    }

    /**
     * Gets the value of the methodOfAdjustment property.
     * 
     * @return
     *     possible object is
     *     {@link MethodOfAdjustmentEnum }
     *     
     */
    public MethodOfAdjustmentEnum getMethodOfAdjustment() {
        return methodOfAdjustment;
    }

    /**
     * Sets the value of the methodOfAdjustment property.
     * 
     * @param value
     *     allowed object is
     *     {@link MethodOfAdjustmentEnum }
     *     
     */
    public void setMethodOfAdjustment(MethodOfAdjustmentEnum value) {
        this.methodOfAdjustment = value;
    }

    /**
     * Gets the value of the optionEntitlement property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOptionEntitlement() {
        return optionEntitlement;
    }

    /**
     * Sets the value of the optionEntitlement property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOptionEntitlement(BigDecimal value) {
        this.optionEntitlement = value;
    }

    /**
     * Gets the value of the multiplier property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMultiplier() {
        return multiplier;
    }

    /**
     * Sets the value of the multiplier property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMultiplier(BigDecimal value) {
        this.multiplier = value;
    }

    /**
     * Gets the value of the clearingInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link SwaptionPhysicalSettlement }
     *     
     */
    public SwaptionPhysicalSettlement getClearingInstructions() {
        return clearingInstructions;
    }

    /**
     * Sets the value of the clearingInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link SwaptionPhysicalSettlement }
     *     
     */
    public void setClearingInstructions(SwaptionPhysicalSettlement value) {
        this.clearingInstructions = value;
    }

    /**
     * Gets the value of the dividendSwapTransactionSupplement property.
     * 
     * @return
     *     possible object is
     *     {@link DividendSwapTransactionSupplement }
     *     
     */
    public DividendSwapTransactionSupplement getDividendSwapTransactionSupplement() {
        return dividendSwapTransactionSupplement;
    }

    /**
     * Sets the value of the dividendSwapTransactionSupplement property.
     * 
     * @param value
     *     allowed object is
     *     {@link DividendSwapTransactionSupplement }
     *     
     */
    public void setDividendSwapTransactionSupplement(DividendSwapTransactionSupplement value) {
        this.dividendSwapTransactionSupplement = value;
    }

}
