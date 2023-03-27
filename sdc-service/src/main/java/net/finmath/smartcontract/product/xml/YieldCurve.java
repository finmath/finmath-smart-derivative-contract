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
 * A generic yield curve object, which can be valued in a variety of ways.
 * 
 * <p>Java class for YieldCurve complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="YieldCurve"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}PricingStructure"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}YieldCurveCharacteristics.model" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "YieldCurve", propOrder = {
    "algorithm",
    "forecastRateIndex"
})
public class YieldCurve
    extends PricingStructure
{

    protected String algorithm;
    protected ForecastRateIndex forecastRateIndex;

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the value of the algorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

    /**
     * Gets the value of the forecastRateIndex property.
     * 
     * @return
     *     possible object is
     *     {@link ForecastRateIndex }
     *     
     */
    public ForecastRateIndex getForecastRateIndex() {
        return forecastRateIndex;
    }

    /**
     * Sets the value of the forecastRateIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForecastRateIndex }
     *     
     */
    public void setForecastRateIndex(ForecastRateIndex value) {
        this.forecastRateIndex = value;
    }

}
