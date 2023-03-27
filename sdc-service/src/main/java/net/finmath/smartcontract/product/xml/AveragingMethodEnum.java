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
 * <p>Java class for AveragingMethodEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="AveragingMethodEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Unweighted"/&gt;
 *     &lt;enumeration value="Weighted"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AveragingMethodEnum")
@XmlEnum
public enum AveragingMethodEnum {


    /**
     * The arithmetic mean of the relevant rates for each reset date.
     * 
     */
    @XmlEnumValue("Unweighted")
    UNWEIGHTED("Unweighted"),

    /**
     * The arithmetic mean of the relevant rates in effect for each day in a calculation period calculated by multiplying each relevant rate by the number of days such relevant rate is in effect, determining the sum of such products and dividing such sum by the number of days in the calculation period.
     * 
     */
    @XmlEnumValue("Weighted")
    WEIGHTED("Weighted");
    private final String value;

    AveragingMethodEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AveragingMethodEnum fromValue(String v) {
        for (AveragingMethodEnum c: AveragingMethodEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
