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
 * <p>Java class for FxBarrierStyleEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="FxBarrierStyleEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="American"/&gt;
 *     &lt;enumeration value="European"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FxBarrierStyleEnum")
@XmlEnum
public enum FxBarrierStyleEnum {


    /**
     * The barrier is observed continuously through the observation period.
     * 
     */
    @XmlEnumValue("American")
    AMERICAN("American"),

    /**
     * The barrier is observed on a discrete expiry date, or (in the case of a multi-phase product) series of expiry dates.
     * 
     */
    @XmlEnumValue("European")
    EUROPEAN("European");
    private final String value;

    FxBarrierStyleEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FxBarrierStyleEnum fromValue(String v) {
        for (FxBarrierStyleEnum c: FxBarrierStyleEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}