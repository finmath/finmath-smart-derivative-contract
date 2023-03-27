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
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining the components specifiying an Inflation Rate Calculation
 * 
 * <p>Java class for InflationRateCalculation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InflationRateCalculation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}FloatingRateCalculation"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inflationLag" type="{http://www.fpml.org/FpML-5/confirmation}Offset"/&gt;
 *         &lt;element name="indexSource" type="{http://www.fpml.org/FpML-5/confirmation}RateSourcePage"/&gt;
 *         &lt;element name="mainPublication" type="{http://www.fpml.org/FpML-5/confirmation}MainPublication" minOccurs="0"/&gt;
 *         &lt;element name="interpolationMethod" type="{http://www.fpml.org/FpML-5/confirmation}InterpolationMethod"/&gt;
 *         &lt;element name="initialIndexLevel" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="fallbackBondApplicable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InflationRateCalculation", propOrder = {
    "inflationLag",
    "indexSource",
    "mainPublication",
    "interpolationMethod",
    "initialIndexLevel",
    "fallbackBondApplicable"
})
public class InflationRateCalculation
    extends FloatingRateCalculation
{

    @XmlElement(required = true)
    protected Offset inflationLag;
    @XmlElement(required = true)
    protected RateSourcePage indexSource;
    protected MainPublication mainPublication;
    @XmlElement(required = true)
    protected InterpolationMethod interpolationMethod;
    protected BigDecimal initialIndexLevel;
    protected boolean fallbackBondApplicable;

    /**
     * Gets the value of the inflationLag property.
     * 
     * @return
     *     possible object is
     *     {@link Offset }
     *     
     */
    public Offset getInflationLag() {
        return inflationLag;
    }

    /**
     * Sets the value of the inflationLag property.
     * 
     * @param value
     *     allowed object is
     *     {@link Offset }
     *     
     */
    public void setInflationLag(Offset value) {
        this.inflationLag = value;
    }

    /**
     * Gets the value of the indexSource property.
     * 
     * @return
     *     possible object is
     *     {@link RateSourcePage }
     *     
     */
    public RateSourcePage getIndexSource() {
        return indexSource;
    }

    /**
     * Sets the value of the indexSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateSourcePage }
     *     
     */
    public void setIndexSource(RateSourcePage value) {
        this.indexSource = value;
    }

    /**
     * Gets the value of the mainPublication property.
     * 
     * @return
     *     possible object is
     *     {@link MainPublication }
     *     
     */
    public MainPublication getMainPublication() {
        return mainPublication;
    }

    /**
     * Sets the value of the mainPublication property.
     * 
     * @param value
     *     allowed object is
     *     {@link MainPublication }
     *     
     */
    public void setMainPublication(MainPublication value) {
        this.mainPublication = value;
    }

    /**
     * Gets the value of the interpolationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link InterpolationMethod }
     *     
     */
    public InterpolationMethod getInterpolationMethod() {
        return interpolationMethod;
    }

    /**
     * Sets the value of the interpolationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterpolationMethod }
     *     
     */
    public void setInterpolationMethod(InterpolationMethod value) {
        this.interpolationMethod = value;
    }

    /**
     * Gets the value of the initialIndexLevel property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInitialIndexLevel() {
        return initialIndexLevel;
    }

    /**
     * Sets the value of the initialIndexLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInitialIndexLevel(BigDecimal value) {
        this.initialIndexLevel = value;
    }

    /**
     * Gets the value of the fallbackBondApplicable property.
     * 
     */
    public boolean isFallbackBondApplicable() {
        return fallbackBondApplicable;
    }

    /**
     * Sets the value of the fallbackBondApplicable property.
     * 
     */
    public void setFallbackBondApplicable(boolean value) {
        this.fallbackBondApplicable = value;
    }

}
