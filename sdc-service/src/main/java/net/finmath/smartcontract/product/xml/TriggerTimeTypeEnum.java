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
 * <p>Java class for TriggerTimeTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="TriggerTimeTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Closing"/&gt;
 *     &lt;enumeration value="Anytime"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TriggerTimeTypeEnum")
@XmlEnum
public enum TriggerTimeTypeEnum {


    /**
     * The close of trading on a day would be considered for valuation.
     * 
     */
    @XmlEnumValue("Closing")
    CLOSING("Closing"),

    /**
     * At any time during the Knock Determination period (continuous barrier).
     * 
     */
    @XmlEnumValue("Anytime")
    ANYTIME("Anytime");
    private final String value;

    TriggerTimeTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TriggerTimeTypeEnum fromValue(String v) {
        for (TriggerTimeTypeEnum c: TriggerTimeTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
