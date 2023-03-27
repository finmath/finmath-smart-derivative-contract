//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Code that describes what type of collateral is posted by a party to a transaction. Options include Uncollateralized, Partial, Full, One-Way.
 * 
 * <p>Java class for CollateralizationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CollateralizationType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.fpml.org/FpML-5/confirmation&gt;Scheme"&gt;
 *       &lt;attribute name="collateralTypeScheme" type="{http://www.fpml.org/FpML-5/confirmation}NonEmptyURI" default="http://www.fpml.org/coding-scheme/collateral-type" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollateralizationType", propOrder = {
    "value"
})
public class CollateralizationType {

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String value;
    @XmlAttribute(name = "collateralTypeScheme")
    protected String collateralTypeScheme;

    /**
     * The base class for all types which define coding schemes that are allowed to be empty.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the collateralTypeScheme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollateralTypeScheme() {
        if (collateralTypeScheme == null) {
            return "http://www.fpml.org/coding-scheme/collateral-type";
        } else {
            return collateralTypeScheme;
        }
    }

    /**
     * Sets the value of the collateralTypeScheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollateralTypeScheme(String value) {
        this.collateralTypeScheme = value;
    }

}
