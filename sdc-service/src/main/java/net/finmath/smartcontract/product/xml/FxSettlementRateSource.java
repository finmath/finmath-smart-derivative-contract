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
 * <p>Java class for FxSettlementRateSource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxSettlementRateSource"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="settlementRateOption" type="{http://www.fpml.org/FpML-5/confirmation}SettlementRateOption"/&gt;
 *         &lt;element name="nonstandardSettlementRate" type="{http://www.fpml.org/FpML-5/confirmation}FxInformationSource"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxSettlementRateSource", propOrder = {
    "settlementRateOption",
    "nonstandardSettlementRate"
})
public class FxSettlementRateSource {

    protected SettlementRateOption settlementRateOption;
    protected FxInformationSource nonstandardSettlementRate;

    /**
     * Gets the value of the settlementRateOption property.
     * 
     * @return
     *     possible object is
     *     {@link SettlementRateOption }
     *     
     */
    public SettlementRateOption getSettlementRateOption() {
        return settlementRateOption;
    }

    /**
     * Sets the value of the settlementRateOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementRateOption }
     *     
     */
    public void setSettlementRateOption(SettlementRateOption value) {
        this.settlementRateOption = value;
    }

    /**
     * Gets the value of the nonstandardSettlementRate property.
     * 
     * @return
     *     possible object is
     *     {@link FxInformationSource }
     *     
     */
    public FxInformationSource getNonstandardSettlementRate() {
        return nonstandardSettlementRate;
    }

    /**
     * Sets the value of the nonstandardSettlementRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxInformationSource }
     *     
     */
    public void setNonstandardSettlementRate(FxInformationSource value) {
        this.nonstandardSettlementRate = value;
    }

}
