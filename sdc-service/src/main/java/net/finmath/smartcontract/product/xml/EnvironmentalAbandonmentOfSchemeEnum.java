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
 * <p>Java class for EnvironmentalAbandonmentOfSchemeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="EnvironmentalAbandonmentOfSchemeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="OptionA(1)"/&gt;
 *     &lt;enumeration value="OptionA(2)"/&gt;
 *     &lt;enumeration value="OptionB"/&gt;
 *     &lt;enumeration value="OptionC"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EnvironmentalAbandonmentOfSchemeEnum")
@XmlEnum
public enum EnvironmentalAbandonmentOfSchemeEnum {


    /**
     * Abandonment of Scheme constitutes an Additional Termination Event.
     * 
     */
    @XmlEnumValue("OptionA(1)")
    OPTION_A_1("OptionA(1)"),

    /**
     * Abandonment of Scheme entails no further obligations.
     * 
     */
    @XmlEnumValue("OptionA(2)")
    OPTION_A_2("OptionA(2)"),

    /**
     * The applicability of Abandonment of Scheme to Emissions Transactions is set forth in the applicable Confirmation.
     * 
     */
    @XmlEnumValue("OptionB")
    OPTION_B("OptionB"),

    /**
     * The applicability of Abandonment of Scheme does not apply.
     * 
     */
    @XmlEnumValue("OptionC")
    OPTION_C("OptionC");
    private final String value;

    EnvironmentalAbandonmentOfSchemeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnvironmentalAbandonmentOfSchemeEnum fromValue(String v) {
        for (EnvironmentalAbandonmentOfSchemeEnum c: EnvironmentalAbandonmentOfSchemeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}