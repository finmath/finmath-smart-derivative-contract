//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The representation of the schedule as an offset relative to another schedule. For example, the settlement schedule may be relative to the expiry schedule by an FxForward offset.
 * 
 * <p>Java class for FxDateOffset complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxDateOffset"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="convention" type="{http://www.fpml.org/FpML-5/confirmation}FxOffsetConventionEnum"/&gt;
 *         &lt;element name="offset" type="{http://www.fpml.org/FpML-5/confirmation}Period" minOccurs="0"/&gt;
 *         &lt;element name="relativeTo" type="{http://www.fpml.org/FpML-5/confirmation}FxScheduleReference"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxDateOffset", propOrder = {
    "convention",
    "offset",
    "relativeTo"
})
public class FxDateOffset {

    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected FxOffsetConventionEnum convention;
    protected Period offset;
    @XmlElement(required = true)
    protected FxScheduleReference relativeTo;

    /**
     * Gets the value of the convention property.
     * 
     * @return
     *     possible object is
     *     {@link FxOffsetConventionEnum }
     *     
     */
    public FxOffsetConventionEnum getConvention() {
        return convention;
    }

    /**
     * Sets the value of the convention property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxOffsetConventionEnum }
     *     
     */
    public void setConvention(FxOffsetConventionEnum value) {
        this.convention = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setOffset(Period value) {
        this.offset = value;
    }

    /**
     * Gets the value of the relativeTo property.
     * 
     * @return
     *     possible object is
     *     {@link FxScheduleReference }
     *     
     */
    public FxScheduleReference getRelativeTo() {
        return relativeTo;
    }

    /**
     * Sets the value of the relativeTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxScheduleReference }
     *     
     */
    public void setRelativeTo(FxScheduleReference value) {
        this.relativeTo = value;
    }

}