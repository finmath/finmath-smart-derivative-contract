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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * An abstract base class for all directional leg types with effective date, termination date, and underlyer, where a payer makes a stream of payments of greater than zero value to a receiver.
 * 
 * <p>Java class for DirectionalLegUnderlyerValuation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectionalLegUnderlyerValuation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}DirectionalLegUnderlyer"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="valuation" type="{http://www.fpml.org/FpML-5/confirmation}EquityValuation"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectionalLegUnderlyerValuation", propOrder = {
    "valuation"
})
@XmlSeeAlso({
    CorrelationLeg.class,
    VarianceLeg.class,
    VolatilityLeg.class
})
public abstract class DirectionalLegUnderlyerValuation
    extends DirectionalLegUnderlyer
{

    @XmlElement(required = true)
    protected EquityValuation valuation;

    /**
     * Gets the value of the valuation property.
     * 
     * @return
     *     possible object is
     *     {@link EquityValuation }
     *     
     */
    public EquityValuation getValuation() {
        return valuation;
    }

    /**
     * Sets the value of the valuation property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquityValuation }
     *     
     */
    public void setValuation(EquityValuation value) {
        this.valuation = value;
    }

}
