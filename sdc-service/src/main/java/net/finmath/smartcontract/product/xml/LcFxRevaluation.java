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
 * An event representing a change in either the [L/C -> Facility] or [Accrual -> L/C] FX rates (or both) on an outstanding letter of credit.
 * 
 * <p>Java class for LcFxRevaluation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LcFxRevaluation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}LcEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="lcFxRate" type="{http://www.fpml.org/FpML-5/confirmation}FxTerms" minOccurs="0"/&gt;
 *         &lt;element name="facilityFxRate" type="{http://www.fpml.org/FpML-5/confirmation}FxTerms" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LcFxRevaluation", propOrder = {
    "lcFxRate",
    "facilityFxRate"
})
public class LcFxRevaluation
    extends LcEvent
{

    protected FxTerms lcFxRate;
    protected FxTerms facilityFxRate;

    /**
     * Gets the value of the lcFxRate property.
     * 
     * @return
     *     possible object is
     *     {@link FxTerms }
     *     
     */
    public FxTerms getLcFxRate() {
        return lcFxRate;
    }

    /**
     * Sets the value of the lcFxRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxTerms }
     *     
     */
    public void setLcFxRate(FxTerms value) {
        this.lcFxRate = value;
    }

    /**
     * Gets the value of the facilityFxRate property.
     * 
     * @return
     *     possible object is
     *     {@link FxTerms }
     *     
     */
    public FxTerms getFacilityFxRate() {
        return facilityFxRate;
    }

    /**
     * Sets the value of the facilityFxRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxTerms }
     *     
     */
    public void setFacilityFxRate(FxTerms value) {
        this.facilityFxRate = value;
    }

}
