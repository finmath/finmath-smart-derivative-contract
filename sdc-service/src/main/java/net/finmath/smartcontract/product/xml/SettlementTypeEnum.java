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
 * <p>Java class for SettlementTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="SettlementTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="Cash"/&gt;
 *     &lt;enumeration value="Physical"/&gt;
 *     &lt;enumeration value="Election"/&gt;
 *     &lt;enumeration value="CashOrPhysical"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SettlementTypeEnum")
@XmlEnum
public enum SettlementTypeEnum {


    /**
     * The intrinsic value of the option will be delivered by way of a cash settlement amount determined, (i) by reference to the differential between the strike price and the settlement price; or (ii) in accordance with a bilateral agreement between the parties
     * 
     */
    @XmlEnumValue("Cash")
    CASH("Cash"),

    /**
     * The securities underlying the transaction will be delivered by (i) in the case of a call, the seller to the buyer, or (ii) in the case of a put, the buyer to the seller versus a settlement amount equivalent to the strike price per share
     * 
     */
    @XmlEnumValue("Physical")
    PHYSICAL("Physical"),

    /**
     * Allow Election of either Cash or Physical settlement
     * 
     */
    @XmlEnumValue("Election")
    ELECTION("Election"),

    /**
     * Allow use of either Cash or Physical settlement without prior Election
     * 
     */
    @XmlEnumValue("CashOrPhysical")
    CASH_OR_PHYSICAL("CashOrPhysical");
    private final String value;

    SettlementTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SettlementTypeEnum fromValue(String v) {
        for (SettlementTypeEnum c: SettlementTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}