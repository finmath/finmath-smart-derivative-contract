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
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type including a reference to a bond to support the representation of an asset swap or Condition Precedent Bond.
 * 
 * <p>Java class for BondReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BondReference"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.fpml.org/FpML-5/confirmation}bond"/&gt;
 *         &lt;element name="conditionPrecedentBond" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="discrepancyClause" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BondReference", propOrder = {
    "bond",
    "conditionPrecedentBond",
    "discrepancyClause"
})
public class BondReference {

    @XmlElement(required = true)
    protected Bond bond;
    protected boolean conditionPrecedentBond;
    protected Boolean discrepancyClause;

    /**
     * Reference to a bond underlyer.
     * 
     * @return
     *     possible object is
     *     {@link Bond }
     *     
     */
    public Bond getBond() {
        return bond;
    }

    /**
     * Sets the value of the bond property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bond }
     *     
     */
    public void setBond(Bond value) {
        this.bond = value;
    }

    /**
     * Gets the value of the conditionPrecedentBond property.
     * 
     */
    public boolean isConditionPrecedentBond() {
        return conditionPrecedentBond;
    }

    /**
     * Sets the value of the conditionPrecedentBond property.
     * 
     */
    public void setConditionPrecedentBond(boolean value) {
        this.conditionPrecedentBond = value;
    }

    /**
     * Gets the value of the discrepancyClause property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDiscrepancyClause() {
        return discrepancyClause;
    }

    /**
     * Sets the value of the discrepancyClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDiscrepancyClause(Boolean value) {
        this.discrepancyClause = value;
    }

}
