//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CollateralValueAllocationEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="CollateralValueAllocationEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Buffer"/&gt;
 *     &lt;enumeration value="Full"/&gt;
 *     &lt;enumeration value="ExcessOverMargin"/&gt;
 *     &lt;enumeration value="Margin"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CollateralValueAllocationEnum")
@XmlEnum
public enum CollateralValueAllocationEnum {


    /**
     * The amount that is being allocated from a buffer account. A buffer account is meant to hold funds that the FCM can use to cover the liability of any client. In effect, the FCM has provided funds that are available to meet its customers’ needs and the DCO may use such collateral to meet a default by a customer to the same extent as if the customer provided the collateral.
     * 
     */
    @XmlEnumValue("Buffer")
    BUFFER("Buffer"),

    /**
     * The full amount is being allocated
     * 
     */
    @XmlEnumValue("Full")
    FULL("Full"),

    /**
     * The allocated amount is an excess over the margin requirement
     * 
     */
    @XmlEnumValue("ExcessOverMargin")
    EXCESS_OVER_MARGIN("ExcessOverMargin"),

    /**
     * The allocated amount for margin requirement
     * 
     */
    @XmlEnumValue("Margin")
    MARGIN("Margin");
    private final String value;

    CollateralValueAllocationEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CollateralValueAllocationEnum fromValue(String v) {
        for (CollateralValueAllocationEnum c: CollateralValueAllocationEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
