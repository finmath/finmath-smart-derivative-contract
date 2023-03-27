//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A structure that describes how an option settles into a physical trade.
 * 
 * <p>Java class for PhysicalSettlement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PhysicalSettlement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="resultingTradeIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}PartyTradeIdentifier"/&gt;
 *         &lt;element name="resultingTrade" type="{http://www.fpml.org/FpML-5/confirmation}Trade"/&gt;
 *         &lt;element ref="{http://www.fpml.org/FpML-5/confirmation}product"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhysicalSettlement", propOrder = {
    "resultingTradeIdentifier",
    "resultingTrade",
    "product"
})
public class PhysicalSettlement {

    protected PartyTradeIdentifier resultingTradeIdentifier;
    protected Trade resultingTrade;
    @XmlElementRef(name = "product", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends Product> product;

    /**
     * Gets the value of the resultingTradeIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link PartyTradeIdentifier }
     *     
     */
    public PartyTradeIdentifier getResultingTradeIdentifier() {
        return resultingTradeIdentifier;
    }

    /**
     * Sets the value of the resultingTradeIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyTradeIdentifier }
     *     
     */
    public void setResultingTradeIdentifier(PartyTradeIdentifier value) {
        this.resultingTradeIdentifier = value;
    }

    /**
     * Gets the value of the resultingTrade property.
     * 
     * @return
     *     possible object is
     *     {@link Trade }
     *     
     */
    public Trade getResultingTrade() {
        return resultingTrade;
    }

    /**
     * Sets the value of the resultingTrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trade }
     *     
     */
    public void setResultingTrade(Trade value) {
        this.resultingTrade = value;
    }

    /**
     * Gets the value of the product property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CommodityPerformanceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link CorrelationSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link Strategy }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link DividendSwapOptionTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityOptionTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityBasketOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxPerformanceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link TermDeposit }{@code >}
     *     {@link JAXBElement }{@code <}{@link BrokerEquityOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityDigitalOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link VolatilitySwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxSingleLeg }{@code >}
     *     {@link JAXBElement }{@code <}{@link GenericProduct }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxAccrualOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CreditDefaultSwapOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link BondOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link VarianceOptionTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link Repo }{@code >}
     *     {@link JAXBElement }{@code <}{@link Swap }{@code >}
     *     {@link JAXBElement }{@code <}{@link VolatilitySwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxTargetKnockoutForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxForwardVolatilityAgreement }{@code >}
     *     {@link JAXBElement }{@code <}{@link VarianceSwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link InstrumentTradeDetails }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxFlexibleForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link StandardProduct }{@code >}
     *     {@link JAXBElement }{@code <}{@link VarianceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link ReturnSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link Swaption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxDigitalOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommoditySwaption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CreditDefaultSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link Fra }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxAccrualDigitalOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommoditySwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquitySwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxPerformanceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link BulletPayment }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxRangeAccrual }{@code >}
     *     {@link JAXBElement }{@code <}{@link DividendSwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link CapFloor }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxAccrualForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link Product }{@code >}
     *     
     */
    public JAXBElement<? extends Product> getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CommodityPerformanceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link CorrelationSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link Strategy }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link DividendSwapOptionTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityOptionTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityBasketOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxPerformanceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link TermDeposit }{@code >}
     *     {@link JAXBElement }{@code <}{@link BrokerEquityOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityDigitalOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link VolatilitySwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxSingleLeg }{@code >}
     *     {@link JAXBElement }{@code <}{@link GenericProduct }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxAccrualOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CreditDefaultSwapOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link BondOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link VarianceOptionTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link Repo }{@code >}
     *     {@link JAXBElement }{@code <}{@link Swap }{@code >}
     *     {@link JAXBElement }{@code <}{@link VolatilitySwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxTargetKnockoutForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxForwardVolatilityAgreement }{@code >}
     *     {@link JAXBElement }{@code <}{@link VarianceSwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link InstrumentTradeDetails }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxFlexibleForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link StandardProduct }{@code >}
     *     {@link JAXBElement }{@code <}{@link VarianceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link ReturnSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link Swaption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxDigitalOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommoditySwaption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CreditDefaultSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link Fra }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxAccrualDigitalOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommoditySwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquitySwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxPerformanceSwap }{@code >}
     *     {@link JAXBElement }{@code <}{@link BulletPayment }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxRangeAccrual }{@code >}
     *     {@link JAXBElement }{@code <}{@link DividendSwapTransactionSupplement }{@code >}
     *     {@link JAXBElement }{@code <}{@link CapFloor }{@code >}
     *     {@link JAXBElement }{@code <}{@link FxAccrualForward }{@code >}
     *     {@link JAXBElement }{@code <}{@link Product }{@code >}
     *     
     */
    public void setProduct(JAXBElement<? extends Product> value) {
        this.product = value;
    }

}