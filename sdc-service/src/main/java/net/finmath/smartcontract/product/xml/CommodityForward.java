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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Commodity Forward
 * 
 * <p>Java class for CommodityForward complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommodityForward"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Product"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="valueDate" type="{http://www.fpml.org/FpML-5/confirmation}AdjustableOrRelativeDate" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="fixedLeg" type="{http://www.fpml.org/FpML-5/confirmation}NonPeriodicFixedPriceLeg"/&gt;
 *           &lt;element name="averagePriceLeg" type="{http://www.fpml.org/FpML-5/confirmation}AveragePriceLeg"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element ref="{http://www.fpml.org/FpML-5/confirmation}commodityForwardLeg"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CommodityContent.model" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommodityForward", propOrder = {
    "valueDate",
    "fixedLeg",
    "averagePriceLeg",
    "commodityForwardLeg",
    "commonPricing",
    "marketDisruption",
    "settlementDisruption",
    "rounding"
})
public class CommodityForward
    extends Product
{

    protected AdjustableOrRelativeDate valueDate;
    protected NonPeriodicFixedPriceLeg fixedLeg;
    protected AveragePriceLeg averagePriceLeg;
    @XmlElementRef(name = "commodityForwardLeg", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class)
    protected JAXBElement<? extends CommodityForwardLeg> commodityForwardLeg;
    protected Boolean commonPricing;
    protected CommodityMarketDisruption marketDisruption;
    @XmlSchemaType(name = "token")
    protected CommodityBullionSettlementDisruptionEnum settlementDisruption;
    protected Rounding rounding;

    /**
     * Gets the value of the valueDate property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public AdjustableOrRelativeDate getValueDate() {
        return valueDate;
    }

    /**
     * Sets the value of the valueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustableOrRelativeDate }
     *     
     */
    public void setValueDate(AdjustableOrRelativeDate value) {
        this.valueDate = value;
    }

    /**
     * Gets the value of the fixedLeg property.
     * 
     * @return
     *     possible object is
     *     {@link NonPeriodicFixedPriceLeg }
     *     
     */
    public NonPeriodicFixedPriceLeg getFixedLeg() {
        return fixedLeg;
    }

    /**
     * Sets the value of the fixedLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonPeriodicFixedPriceLeg }
     *     
     */
    public void setFixedLeg(NonPeriodicFixedPriceLeg value) {
        this.fixedLeg = value;
    }

    /**
     * Gets the value of the averagePriceLeg property.
     * 
     * @return
     *     possible object is
     *     {@link AveragePriceLeg }
     *     
     */
    public AveragePriceLeg getAveragePriceLeg() {
        return averagePriceLeg;
    }

    /**
     * Sets the value of the averagePriceLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link AveragePriceLeg }
     *     
     */
    public void setAveragePriceLeg(AveragePriceLeg value) {
        this.averagePriceLeg = value;
    }

    /**
     * Gets the value of the commodityForwardLeg property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MetalPhysicalLeg }{@code >}
     *     {@link JAXBElement }{@code <}{@link BullionPhysicalLeg }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityForwardLeg }{@code >}
     *     
     */
    public JAXBElement<? extends CommodityForwardLeg> getCommodityForwardLeg() {
        return commodityForwardLeg;
    }

    /**
     * Sets the value of the commodityForwardLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MetalPhysicalLeg }{@code >}
     *     {@link JAXBElement }{@code <}{@link BullionPhysicalLeg }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommodityForwardLeg }{@code >}
     *     
     */
    public void setCommodityForwardLeg(JAXBElement<? extends CommodityForwardLeg> value) {
        this.commodityForwardLeg = value;
    }

    /**
     * Gets the value of the commonPricing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCommonPricing() {
        return commonPricing;
    }

    /**
     * Sets the value of the commonPricing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCommonPricing(Boolean value) {
        this.commonPricing = value;
    }

    /**
     * Gets the value of the marketDisruption property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityMarketDisruption }
     *     
     */
    public CommodityMarketDisruption getMarketDisruption() {
        return marketDisruption;
    }

    /**
     * Sets the value of the marketDisruption property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityMarketDisruption }
     *     
     */
    public void setMarketDisruption(CommodityMarketDisruption value) {
        this.marketDisruption = value;
    }

    /**
     * Gets the value of the settlementDisruption property.
     * 
     * @return
     *     possible object is
     *     {@link CommodityBullionSettlementDisruptionEnum }
     *     
     */
    public CommodityBullionSettlementDisruptionEnum getSettlementDisruption() {
        return settlementDisruption;
    }

    /**
     * Sets the value of the settlementDisruption property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommodityBullionSettlementDisruptionEnum }
     *     
     */
    public void setSettlementDisruption(CommodityBullionSettlementDisruptionEnum value) {
        this.settlementDisruption = value;
    }

    /**
     * Gets the value of the rounding property.
     * 
     * @return
     *     possible object is
     *     {@link Rounding }
     *     
     */
    public Rounding getRounding() {
        return rounding;
    }

    /**
     * Sets the value of the rounding property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rounding }
     *     
     */
    public void setRounding(Rounding value) {
        this.rounding = value;
    }

}
