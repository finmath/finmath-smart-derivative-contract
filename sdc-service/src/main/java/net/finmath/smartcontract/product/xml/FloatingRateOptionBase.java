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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Specifies the abstract type underlying a floating rate cash accrual option.
 * 
 * <p>Java class for FloatingRateOptionBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FloatingRateOptionBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}AccrualOptionBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}FloatingRateIndexLoan.model"/&gt;
 *         &lt;element name="spread" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="pikSpread" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FloatingRateOptionBase", propOrder = {
    "floatingRateIndex",
    "indexTenor",
    "spread",
    "pikSpread"
})
@XmlSeeAlso({
    FloatingRateAccrual.class,
    FloatingRateOption.class
})
public abstract class FloatingRateOptionBase
    extends AccrualOptionBase
{

    @XmlElement(required = true)
    protected FloatingRateIndexLoan floatingRateIndex;
    protected Period indexTenor;
    @XmlElement(required = true)
    protected BigDecimal spread;
    protected BigDecimal pikSpread;

    /**
     * Gets the value of the floatingRateIndex property.
     * 
     * @return
     *     possible object is
     *     {@link FloatingRateIndexLoan }
     *     
     */
    public FloatingRateIndexLoan getFloatingRateIndex() {
        return floatingRateIndex;
    }

    /**
     * Sets the value of the floatingRateIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link FloatingRateIndexLoan }
     *     
     */
    public void setFloatingRateIndex(FloatingRateIndexLoan value) {
        this.floatingRateIndex = value;
    }

    /**
     * Gets the value of the indexTenor property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getIndexTenor() {
        return indexTenor;
    }

    /**
     * Sets the value of the indexTenor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setIndexTenor(Period value) {
        this.indexTenor = value;
    }

    /**
     * Gets the value of the spread property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpread() {
        return spread;
    }

    /**
     * Sets the value of the spread property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpread(BigDecimal value) {
        this.spread = value;
    }

    /**
     * Gets the value of the pikSpread property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPikSpread() {
        return pikSpread;
    }

    /**
     * Sets the value of the pikSpread property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPikSpread(BigDecimal value) {
        this.pikSpread = value;
    }

}