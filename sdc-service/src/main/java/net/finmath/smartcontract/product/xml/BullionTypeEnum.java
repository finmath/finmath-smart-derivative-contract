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
 * <p>Java class for BullionTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="BullionTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Gold"/&gt;
 *     &lt;enumeration value="Palladium"/&gt;
 *     &lt;enumeration value="Platinum"/&gt;
 *     &lt;enumeration value="Silver"/&gt;
 *     &lt;enumeration value="Rhodium"/&gt;
 *     &lt;enumeration value="RhodiumSponge"/&gt;
 *     &lt;enumeration value="Iridium"/&gt;
 *     &lt;enumeration value="Ruthenium"/&gt;
 *     &lt;enumeration value="Osmium"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "BullionTypeEnum")
@XmlEnum
public enum BullionTypeEnum {


    /**
     * Gold. Quality as per the Good Delivery Rules issued by the London Bullion Market Association.
     * 
     */
    @XmlEnumValue("Gold")
    GOLD("Gold"),

    /**
     * Palladium. Quality as per the Good Delivery Rules issued by the London Platinum and Palladium Market.
     * 
     */
    @XmlEnumValue("Palladium")
    PALLADIUM("Palladium"),

    /**
     * Palladium. Quality as per the Good Delivery Rules issued by the London Platinum and Palladium Market.
     * 
     */
    @XmlEnumValue("Platinum")
    PLATINUM("Platinum"),

    /**
     * Silver. Quality as per the Good Delivery Rules issued by the London Bullion Market Association.
     * 
     */
    @XmlEnumValue("Silver")
    SILVER("Silver"),

    /**
     * Quality as per the Good Delivery Rules for Rhodium.
     * 
     */
    @XmlEnumValue("Rhodium")
    RHODIUM("Rhodium"),

    /**
     * DEPRECATED value which will be removed in FpML-6-0 onwards. Quality as per the Good Delivery Rules for Rhodium (Sponge) is too specific.
     * 
     */
    @XmlEnumValue("RhodiumSponge")
    RHODIUM_SPONGE("RhodiumSponge"),

    /**
     * Quality as per the Good Delivery Rules for Iridium.
     * 
     */
    @XmlEnumValue("Iridium")
    IRIDIUM("Iridium"),

    /**
     * Quality as per the Good Delivery Rules for Ruthenium.
     * 
     */
    @XmlEnumValue("Ruthenium")
    RUTHENIUM("Ruthenium"),

    /**
     * Quality as per the Good Delivery Rules for Osmium.
     * 
     */
    @XmlEnumValue("Osmium")
    OSMIUM("Osmium");
    private final String value;

    BullionTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BullionTypeEnum fromValue(String v) {
        for (BullionTypeEnum c: BullionTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
