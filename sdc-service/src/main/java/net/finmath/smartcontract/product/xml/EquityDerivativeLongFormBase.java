//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * type for defining the common features of equity derivatives.
 * 
 * <p>Java class for EquityDerivativeLongFormBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EquityDerivativeLongFormBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}EquityDerivativeBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dividendConditions" type="{http://www.fpml.org/FpML-5/confirmation}DividendConditions" minOccurs="0"/&gt;
 *         &lt;element name="methodOfAdjustment" type="{http://www.fpml.org/FpML-5/confirmation}MethodOfAdjustmentEnum"/&gt;
 *         &lt;element name="extraordinaryEvents" type="{http://www.fpml.org/FpML-5/confirmation}ExtraordinaryEvents"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EquityDerivativeLongFormBase", propOrder = {
    "dividendConditions",
    "methodOfAdjustment",
    "extraordinaryEvents"
})
@XmlSeeAlso({
    EquityForward.class,
    EquityOption.class
})
public abstract class EquityDerivativeLongFormBase
    extends EquityDerivativeBase
{

    protected DividendConditions dividendConditions;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected MethodOfAdjustmentEnum methodOfAdjustment;
    @XmlElement(required = true)
    protected ExtraordinaryEvents extraordinaryEvents;

    /**
     * Gets the value of the dividendConditions property.
     * 
     * @return
     *     possible object is
     *     {@link DividendConditions }
     *     
     */
    public DividendConditions getDividendConditions() {
        return dividendConditions;
    }

    /**
     * Sets the value of the dividendConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link DividendConditions }
     *     
     */
    public void setDividendConditions(DividendConditions value) {
        this.dividendConditions = value;
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
     * Gets the value of the extraordinaryEvents property.
     * 
     * @return
     *     possible object is
     *     {@link ExtraordinaryEvents }
     *     
     */
    public ExtraordinaryEvents getExtraordinaryEvents() {
        return extraordinaryEvents;
    }

    /**
     * Sets the value of the extraordinaryEvents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtraordinaryEvents }
     *     
     */
    public void setExtraordinaryEvents(ExtraordinaryEvents value) {
        this.extraordinaryEvents = value;
    }

}