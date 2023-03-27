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
 * <p>Java class for NegativeInterestRateTreatmentEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="NegativeInterestRateTreatmentEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="NegativeInterestRateMethod"/&gt;
 *     &lt;enumeration value="ZeroInterestRateMethod"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "NegativeInterestRateTreatmentEnum")
@XmlEnum
public enum NegativeInterestRateTreatmentEnum {


    /**
     * Negative Interest Rate Method. Per 2000 ISDA Definitions, Section 6.4 Negative Interest Rates, paragraphs (b) and (c).
     * 
     */
    @XmlEnumValue("NegativeInterestRateMethod")
    NEGATIVE_INTEREST_RATE_METHOD("NegativeInterestRateMethod"),

    /**
     * Zero Interest Rate Method. Per 2000 ISDA Definitions, Section 6.4. Negative Interest Rates, paragraphs (d) and (e).
     * 
     */
    @XmlEnumValue("ZeroInterestRateMethod")
    ZERO_INTEREST_RATE_METHOD("ZeroInterestRateMethod");
    private final String value;

    NegativeInterestRateTreatmentEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NegativeInterestRateTreatmentEnum fromValue(String v) {
        for (NegativeInterestRateTreatmentEnum c: NegativeInterestRateTreatmentEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}