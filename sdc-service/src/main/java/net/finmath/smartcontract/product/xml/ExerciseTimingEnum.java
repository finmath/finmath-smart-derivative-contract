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
 * <p>Java class for ExerciseTimingEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="ExerciseTimingEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Immediate"/&gt;
 *     &lt;enumeration value="OnExpiration"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ExerciseTimingEnum")
@XmlEnum
public enum ExerciseTimingEnum {


    /**
     * Perform the requested exercise behavior immediately on receipt of the request.
     * 
     */
    @XmlEnumValue("Immediate")
    IMMEDIATE("Immediate"),

    /**
     * Perform the requested exercise behavior at the expiration of the option.
     * 
     */
    @XmlEnumValue("OnExpiration")
    ON_EXPIRATION("OnExpiration");
    private final String value;

    ExerciseTimingEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExerciseTimingEnum fromValue(String v) {
        for (ExerciseTimingEnum c: ExerciseTimingEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
