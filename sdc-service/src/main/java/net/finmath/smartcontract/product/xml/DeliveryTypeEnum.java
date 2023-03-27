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
 * <p>Java class for DeliveryTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="DeliveryTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Firm"/&gt;
 *     &lt;enumeration value="Interruptible"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DeliveryTypeEnum")
@XmlEnum
public enum DeliveryTypeEnum {

    @XmlEnumValue("Firm")
    FIRM("Firm"),
    @XmlEnumValue("Interruptible")
    INTERRUPTIBLE("Interruptible");
    private final String value;

    DeliveryTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeliveryTypeEnum fromValue(String v) {
        for (DeliveryTypeEnum c: DeliveryTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
