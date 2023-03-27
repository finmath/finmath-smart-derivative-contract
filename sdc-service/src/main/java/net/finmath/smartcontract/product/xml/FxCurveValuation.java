//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A valuation of an FX curve object., which includes pricing inputs and term structures for fx forwards.
 * 
 * <p>Java class for FxCurveValuation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxCurveValuation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}PricingStructureValuation"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="settlementCurrencyYieldCurve" type="{http://www.fpml.org/FpML-5/confirmation}PricingStructureReference" minOccurs="0"/&gt;
 *         &lt;element name="forecastCurrencyYieldCurve" type="{http://www.fpml.org/FpML-5/confirmation}PricingStructureReference" minOccurs="0"/&gt;
 *         &lt;element name="spotRate" type="{http://www.fpml.org/FpML-5/confirmation}FxRateSet" minOccurs="0"/&gt;
 *         &lt;element name="fxForwardCurve" type="{http://www.fpml.org/FpML-5/confirmation}TermCurve" minOccurs="0"/&gt;
 *         &lt;element name="fxForwardPointsCurve" type="{http://www.fpml.org/FpML-5/confirmation}TermCurve" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxCurveValuation", propOrder = {
    "settlementCurrencyYieldCurve",
    "forecastCurrencyYieldCurve",
    "spotRate",
    "fxForwardCurve",
    "fxForwardPointsCurve"
})
public class FxCurveValuation
    extends PricingStructureValuation
{

    protected PricingStructureReference settlementCurrencyYieldCurve;
    protected PricingStructureReference forecastCurrencyYieldCurve;
    protected FxRateSet spotRate;
    protected TermCurve fxForwardCurve;
    protected TermCurve fxForwardPointsCurve;

    /**
     * Gets the value of the settlementCurrencyYieldCurve property.
     * 
     * @return
     *     possible object is
     *     {@link PricingStructureReference }
     *     
     */
    public PricingStructureReference getSettlementCurrencyYieldCurve() {
        return settlementCurrencyYieldCurve;
    }

    /**
     * Sets the value of the settlementCurrencyYieldCurve property.
     * 
     * @param value
     *     allowed object is
     *     {@link PricingStructureReference }
     *     
     */
    public void setSettlementCurrencyYieldCurve(PricingStructureReference value) {
        this.settlementCurrencyYieldCurve = value;
    }

    /**
     * Gets the value of the forecastCurrencyYieldCurve property.
     * 
     * @return
     *     possible object is
     *     {@link PricingStructureReference }
     *     
     */
    public PricingStructureReference getForecastCurrencyYieldCurve() {
        return forecastCurrencyYieldCurve;
    }

    /**
     * Sets the value of the forecastCurrencyYieldCurve property.
     * 
     * @param value
     *     allowed object is
     *     {@link PricingStructureReference }
     *     
     */
    public void setForecastCurrencyYieldCurve(PricingStructureReference value) {
        this.forecastCurrencyYieldCurve = value;
    }

    /**
     * Gets the value of the spotRate property.
     * 
     * @return
     *     possible object is
     *     {@link FxRateSet }
     *     
     */
    public FxRateSet getSpotRate() {
        return spotRate;
    }

    /**
     * Sets the value of the spotRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxRateSet }
     *     
     */
    public void setSpotRate(FxRateSet value) {
        this.spotRate = value;
    }

    /**
     * Gets the value of the fxForwardCurve property.
     * 
     * @return
     *     possible object is
     *     {@link TermCurve }
     *     
     */
    public TermCurve getFxForwardCurve() {
        return fxForwardCurve;
    }

    /**
     * Sets the value of the fxForwardCurve property.
     * 
     * @param value
     *     allowed object is
     *     {@link TermCurve }
     *     
     */
    public void setFxForwardCurve(TermCurve value) {
        this.fxForwardCurve = value;
    }

    /**
     * Gets the value of the fxForwardPointsCurve property.
     * 
     * @return
     *     possible object is
     *     {@link TermCurve }
     *     
     */
    public TermCurve getFxForwardPointsCurve() {
        return fxForwardPointsCurve;
    }

    /**
     * Sets the value of the fxForwardPointsCurve property.
     * 
     * @param value
     *     allowed object is
     *     {@link TermCurve }
     *     
     */
    public void setFxForwardPointsCurve(TermCurve value) {
        this.fxForwardPointsCurve = value;
    }

}