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
 * <p>Java class for DeliveryNearbyTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="DeliveryNearbyTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="CalculationPeriod"/&gt;
 *     &lt;enumeration value="NearbyMonth"/&gt;
 *     &lt;enumeration value="NearbyWeek"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DeliveryNearbyTypeEnum")
@XmlEnum
public enum DeliveryNearbyTypeEnum {


    /**
     * Describes the contract to be the contract that pertains to the month-year of the calculation period. If used, the nearby count is expected to be 0.
     * 
     */
    @XmlEnumValue("CalculationPeriod")
    CALCULATION_PERIOD("CalculationPeriod"),

    /**
     * The Delivery Date of the underlying Commodity shall be the month of expiration of the futures contract.
     * 
     */
    @XmlEnumValue("NearbyMonth")
    NEARBY_MONTH("NearbyMonth"),

    /**
     * The Delivery Date of the underlying Commodity shall be the Week of expiration of the futures contract.
     * 
     */
    @XmlEnumValue("NearbyWeek")
    NEARBY_WEEK("NearbyWeek");
    private final String value;

    DeliveryNearbyTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeliveryNearbyTypeEnum fromValue(String v) {
        for (DeliveryNearbyTypeEnum c: DeliveryNearbyTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}