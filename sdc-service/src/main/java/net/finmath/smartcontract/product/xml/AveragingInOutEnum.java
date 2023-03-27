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
 * <p>Java class for AveragingInOutEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="AveragingInOutEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="In"/&gt;
 *     &lt;enumeration value="Out"/&gt;
 *     &lt;enumeration value="Both"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AveragingInOutEnum")
@XmlEnum
public enum AveragingInOutEnum {


    /**
     * The average price is used to derive the strike price. Also known as "Asian strike" style option.
     * 
     */
    @XmlEnumValue("In")
    IN("In"),

    /**
     * The average price is used to derive the expiration price. Also known as "Asian price" style option.
     * 
     */
    @XmlEnumValue("Out")
    OUT("Out"),

    /**
     * The average price is used to derive both the strike and the expiration price.
     * 
     */
    @XmlEnumValue("Both")
    BOTH("Both");
    private final String value;

    AveragingInOutEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AveragingInOutEnum fromValue(String v) {
        for (AveragingInOutEnum c: AveragingInOutEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
