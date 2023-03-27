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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A code that describes what type of role an organization plays, for example a SwapsDealer, a Major Swaps Participant, or Other
 * 
 * <p>Java class for OrganizationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganizationType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.fpml.org/FpML-5/confirmation&gt;Token"&gt;
 *       &lt;attribute name="organizationTypeScheme" type="{http://www.fpml.org/FpML-5/confirmation}NonEmptyURI" default="http://www.fpml.org/coding-scheme/organization-type" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationType", propOrder = {
    "value"
})
public class OrganizationType {

    @XmlValue
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String value;
    @XmlAttribute(name = "organizationTypeScheme")
    protected String organizationTypeScheme;

    /**
     * A token. FpML redefines this type so that in some views it can enforce that it may not be empty
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
     * Gets the value of the organizationTypeScheme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationTypeScheme() {
        if (organizationTypeScheme == null) {
            return "http://www.fpml.org/coding-scheme/organization-type";
        } else {
            return organizationTypeScheme;
        }
    }

    /**
     * Sets the value of the organizationTypeScheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationTypeScheme(String value) {
        this.organizationTypeScheme = value;
    }

}