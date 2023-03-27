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
 * <p>Java class for FxAccrualKnockoutBarrierRetentionEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="FxAccrualKnockoutBarrierRetentionEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Keep"/&gt;
 *     &lt;enumeration value="Lose"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FxAccrualKnockoutBarrierRetentionEnum")
@XmlEnum
public enum FxAccrualKnockoutBarrierRetentionEnum {


    /**
     * If the barrier is triggered, the accrual process for that period stops. The parties retain the underlying settlement rights for that period with the currently accrued notional.
     * 
     */
    @XmlEnumValue("Keep")
    KEEP("Keep"),

    /**
     * If the barrier is triggered, the accrual process for that period stops. No settlement occurs for that period.
     * 
     */
    @XmlEnumValue("Lose")
    LOSE("Lose");
    private final String value;

    FxAccrualKnockoutBarrierRetentionEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FxAccrualKnockoutBarrierRetentionEnum fromValue(String v) {
        for (FxAccrualKnockoutBarrierRetentionEnum c: FxAccrualKnockoutBarrierRetentionEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
