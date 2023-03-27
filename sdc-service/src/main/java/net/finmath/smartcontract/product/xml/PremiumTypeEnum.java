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
 * <p>Java class for PremiumTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="PremiumTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="PrePaid"/&gt;
 *     &lt;enumeration value="PostPaid"/&gt;
 *     &lt;enumeration value="Variable"/&gt;
 *     &lt;enumeration value="Fixed"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PremiumTypeEnum")
@XmlEnum
public enum PremiumTypeEnum {


    /**
     * TODO
     * 
     */
    @XmlEnumValue("PrePaid")
    PRE_PAID("PrePaid"),

    /**
     * TODO
     * 
     */
    @XmlEnumValue("PostPaid")
    POST_PAID("PostPaid"),

    /**
     * TODO
     * 
     */
    @XmlEnumValue("Variable")
    VARIABLE("Variable"),

    /**
     * TODO
     * 
     */
    @XmlEnumValue("Fixed")
    FIXED("Fixed");
    private final String value;

    PremiumTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PremiumTypeEnum fromValue(String v) {
        for (PremiumTypeEnum c: PremiumTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
