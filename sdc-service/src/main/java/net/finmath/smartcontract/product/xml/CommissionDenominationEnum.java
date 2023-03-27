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
 * <p>Java class for CommissionDenominationEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="CommissionDenominationEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="BPS"/&gt;
 *     &lt;enumeration value="Percentage"/&gt;
 *     &lt;enumeration value="CentsPerShare"/&gt;
 *     &lt;enumeration value="FixedAmount"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CommissionDenominationEnum")
@XmlEnum
public enum CommissionDenominationEnum {


    /**
     * The commission is expressed in basis points, in reference to the price referenced in the document.
     * 
     */
    BPS("BPS"),

    /**
     * The commission is expressed as a percentage of the gross price referenced in the document.
     * 
     */
    @XmlEnumValue("Percentage")
    PERCENTAGE("Percentage"),

    /**
     * The commission is expressed in cents per share.
     * 
     */
    @XmlEnumValue("CentsPerShare")
    CENTS_PER_SHARE("CentsPerShare"),

    /**
     * The commission is expressed as a absolute amount.
     * 
     */
    @XmlEnumValue("FixedAmount")
    FIXED_AMOUNT("FixedAmount");
    private final String value;

    CommissionDenominationEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CommissionDenominationEnum fromValue(String v) {
        for (CommissionDenominationEnum c: CommissionDenominationEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}