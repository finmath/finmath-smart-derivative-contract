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
import jakarta.xml.bind.annotation.XmlType;


/**
 * Defines stock loan information where this is required per underlyer. You must not duplicate infromation within dividend conditions at transaction level
 * 
 * <p>Java class for UnderlyerLoanRate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnderlyerLoanRate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}StockLoan.model"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnderlyerLoanRate", propOrder = {
    "lossOfStockBorrow",
    "maximumStockLoanRate",
    "increasedCostOfStockBorrow",
    "initialStockLoanRate"
})
public class UnderlyerLoanRate {

    protected Boolean lossOfStockBorrow;
    protected BigDecimal maximumStockLoanRate;
    protected Boolean increasedCostOfStockBorrow;
    protected BigDecimal initialStockLoanRate;

    /**
     * Gets the value of the lossOfStockBorrow property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLossOfStockBorrow() {
        return lossOfStockBorrow;
    }

    /**
     * Sets the value of the lossOfStockBorrow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLossOfStockBorrow(Boolean value) {
        this.lossOfStockBorrow = value;
    }

    /**
     * Gets the value of the maximumStockLoanRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMaximumStockLoanRate() {
        return maximumStockLoanRate;
    }

    /**
     * Sets the value of the maximumStockLoanRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMaximumStockLoanRate(BigDecimal value) {
        this.maximumStockLoanRate = value;
    }

    /**
     * Gets the value of the increasedCostOfStockBorrow property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncreasedCostOfStockBorrow() {
        return increasedCostOfStockBorrow;
    }

    /**
     * Sets the value of the increasedCostOfStockBorrow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncreasedCostOfStockBorrow(Boolean value) {
        this.increasedCostOfStockBorrow = value;
    }

    /**
     * Gets the value of the initialStockLoanRate property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInitialStockLoanRate() {
        return initialStockLoanRate;
    }

    /**
     * Sets the value of the initialStockLoanRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInitialStockLoanRate(BigDecimal value) {
        this.initialStockLoanRate = value;
    }

}