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
 * <p>Java class for FxTenorPeriodEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="FxTenorPeriodEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Broken"/&gt;
 *     &lt;enumeration value="Today"/&gt;
 *     &lt;enumeration value="Tomorrow"/&gt;
 *     &lt;enumeration value="TomorrowNext"/&gt;
 *     &lt;enumeration value="Spot"/&gt;
 *     &lt;enumeration value="SpotNext"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FxTenorPeriodEnum")
@XmlEnum
public enum FxTenorPeriodEnum {


    /**
     * Broken/non conventional Tenor Period.
     * 
     */
    @XmlEnumValue("Broken")
    BROKEN("Broken"),

    /**
     * Today Tenor Period.
     * 
     */
    @XmlEnumValue("Today")
    TODAY("Today"),

    /**
     * Tomorrow Tenor Period.
     * 
     */
    @XmlEnumValue("Tomorrow")
    TOMORROW("Tomorrow"),

    /**
     * Day after Tomorrow Tenor Period.
     * 
     */
    @XmlEnumValue("TomorrowNext")
    TOMORROW_NEXT("TomorrowNext"),

    /**
     * Spot Tenor Period.
     * 
     */
    @XmlEnumValue("Spot")
    SPOT("Spot"),

    /**
     * Day after Spot Tenor period.
     * 
     */
    @XmlEnumValue("SpotNext")
    SPOT_NEXT("SpotNext");
    private final String value;

    FxTenorPeriodEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FxTenorPeriodEnum fromValue(String v) {
        for (FxTenorPeriodEnum c: FxTenorPeriodEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}