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
 * <p>Java class for FxCounterCurrencyAmount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxCounterCurrencyAmount"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}NonNegativeAmountSchedule"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="strikeReference" type="{http://www.fpml.org/FpML-5/confirmation}FxStrikeReference" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxCounterCurrencyAmount", propOrder = {
    "strikeReference"
})
public class FxCounterCurrencyAmount
    extends NonNegativeAmountSchedule
{

    protected FxStrikeReference strikeReference;

    /**
     * Gets the value of the strikeReference property.
     * 
     * @return
     *     possible object is
     *     {@link FxStrikeReference }
     *     
     */
    public FxStrikeReference getStrikeReference() {
        return strikeReference;
    }

    /**
     * Sets the value of the strikeReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxStrikeReference }
     *     
     */
    public void setStrikeReference(FxStrikeReference value) {
        this.strikeReference = value;
    }

}