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
 * <p>Java class for InterestCalculationMethodEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="InterestCalculationMethodEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="ContractPositionThruPeriod"/&gt;
 *     &lt;enumeration value="ProRataShareSnapshot"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "InterestCalculationMethodEnum")
@XmlEnum
public enum InterestCalculationMethodEnum {


    /**
     * Agent bank is making an interest payment based on the lender's contract position throughout the interest payment period.
     * 
     */
    @XmlEnumValue("ContractPositionThruPeriod")
    CONTRACT_POSITION_THRU_PERIOD("ContractPositionThruPeriod"),

    /**
     * Agent bank is making an interest payment based on the lender pro-rata share snapshot at the time of payment.
     * 
     */
    @XmlEnumValue("ProRataShareSnapshot")
    PRO_RATA_SHARE_SNAPSHOT("ProRataShareSnapshot");
    private final String value;

    InterestCalculationMethodEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InterestCalculationMethodEnum fromValue(String v) {
        for (InterestCalculationMethodEnum c: InterestCalculationMethodEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}