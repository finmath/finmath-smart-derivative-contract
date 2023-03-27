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
import jakarta.xml.bind.annotation.XmlType;


/**
 * A complex type to support the credit default swap option.
 * 
 * <p>Java class for CreditDefaultSwapOption complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditDefaultSwapOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}OptionBaseExtended"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="clearingInstructions" type="{http://www.fpml.org/FpML-5/confirmation}SwaptionPhysicalSettlement" minOccurs="0"/&gt;
 *         &lt;element name="strike" type="{http://www.fpml.org/FpML-5/confirmation}CreditOptionStrike"/&gt;
 *         &lt;element name="creditDefaultSwap" type="{http://www.fpml.org/FpML-5/confirmation}CreditDefaultSwap"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditDefaultSwapOption", propOrder = {
    "clearingInstructions",
    "strike",
    "creditDefaultSwap"
})
public class CreditDefaultSwapOption
    extends OptionBaseExtended
{

    protected SwaptionPhysicalSettlement clearingInstructions;
    @XmlElement(required = true)
    protected CreditOptionStrike strike;
    @XmlElement(required = true)
    protected CreditDefaultSwap creditDefaultSwap;

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
     * Gets the value of the strike property.
     * 
     * @return
     *     possible object is
     *     {@link CreditOptionStrike }
     *     
     */
    public CreditOptionStrike getStrike() {
        return strike;
    }

    /**
     * Sets the value of the strike property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditOptionStrike }
     *     
     */
    public void setStrike(CreditOptionStrike value) {
        this.strike = value;
    }

    /**
     * Gets the value of the creditDefaultSwap property.
     * 
     * @return
     *     possible object is
     *     {@link CreditDefaultSwap }
     *     
     */
    public CreditDefaultSwap getCreditDefaultSwap() {
        return creditDefaultSwap;
    }

    /**
     * Sets the value of the creditDefaultSwap property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditDefaultSwap }
     *     
     */
    public void setCreditDefaultSwap(CreditDefaultSwap value) {
        this.creditDefaultSwap = value;
    }

}
