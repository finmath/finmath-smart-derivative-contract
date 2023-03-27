//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining a rounding direction and precision to be used in the rounding of a rate.
 * 
 * <p>Java class for Rounding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Rounding"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="roundingDirection" type="{http://www.fpml.org/FpML-5/confirmation}RoundingDirectionEnum"/&gt;
 *         &lt;element name="precision" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rounding", propOrder = {
    "roundingDirection",
    "precision"
})
public class Rounding {

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected RoundingDirectionEnum roundingDirection;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger precision;

    /**
     * Gets the value of the roundingDirection property.
     * 
     * @return
     *     possible object is
     *     {@link RoundingDirectionEnum }
     *     
     */
    public RoundingDirectionEnum getRoundingDirection() {
        return roundingDirection;
    }

    /**
     * Sets the value of the roundingDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoundingDirectionEnum }
     *     
     */
    public void setRoundingDirection(RoundingDirectionEnum value) {
        this.roundingDirection = value;
    }

    /**
     * Gets the value of the precision property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the precision property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrecision(BigInteger value) {
        this.precision = value;
    }

}
