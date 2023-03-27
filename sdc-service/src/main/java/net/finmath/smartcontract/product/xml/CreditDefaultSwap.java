//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditDefaultSwap complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditDefaultSwap"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Product"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="generalTerms" type="{http://www.fpml.org/FpML-5/confirmation}GeneralTerms"/&gt;
 *         &lt;element name="feeLeg" type="{http://www.fpml.org/FpML-5/confirmation}FeeLeg"/&gt;
 *         &lt;element name="protectionTerms" type="{http://www.fpml.org/FpML-5/confirmation}ProtectionTerms" maxOccurs="unbounded"/&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="cashSettlementTerms" type="{http://www.fpml.org/FpML-5/confirmation}CashSettlementTerms"/&gt;
 *           &lt;element name="physicalSettlementTerms" type="{http://www.fpml.org/FpML-5/confirmation}PhysicalSettlementTerms"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditDefaultSwap", propOrder = {
    "generalTerms",
    "feeLeg",
    "protectionTerms",
    "cashSettlementTermsOrPhysicalSettlementTerms"
})
public class CreditDefaultSwap
    extends Product
{

    @XmlElement(required = true)
    protected GeneralTerms generalTerms;
    @XmlElement(required = true)
    protected FeeLeg feeLeg;
    @XmlElement(required = true)
    protected List<ProtectionTerms> protectionTerms;
    @XmlElements({
        @XmlElement(name = "cashSettlementTerms", type = CashSettlementTerms.class),
        @XmlElement(name = "physicalSettlementTerms", type = PhysicalSettlementTerms.class)
    })
    protected List<SettlementTerms> cashSettlementTermsOrPhysicalSettlementTerms;

    /**
     * Gets the value of the generalTerms property.
     * 
     * @return
     *     possible object is
     *     {@link GeneralTerms }
     *     
     */
    public GeneralTerms getGeneralTerms() {
        return generalTerms;
    }

    /**
     * Sets the value of the generalTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneralTerms }
     *     
     */
    public void setGeneralTerms(GeneralTerms value) {
        this.generalTerms = value;
    }

    /**
     * Gets the value of the feeLeg property.
     * 
     * @return
     *     possible object is
     *     {@link FeeLeg }
     *     
     */
    public FeeLeg getFeeLeg() {
        return feeLeg;
    }

    /**
     * Sets the value of the feeLeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeeLeg }
     *     
     */
    public void setFeeLeg(FeeLeg value) {
        this.feeLeg = value;
    }

    /**
     * Gets the value of the protectionTerms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the protectionTerms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProtectionTerms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProtectionTerms }
     * 
     * 
     */
    public List<ProtectionTerms> getProtectionTerms() {
        if (protectionTerms == null) {
            protectionTerms = new ArrayList<ProtectionTerms>();
        }
        return this.protectionTerms;
    }

    /**
     * Gets the value of the cashSettlementTermsOrPhysicalSettlementTerms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the cashSettlementTermsOrPhysicalSettlementTerms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCashSettlementTermsOrPhysicalSettlementTerms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CashSettlementTerms }
     * {@link PhysicalSettlementTerms }
     * 
     * 
     */
    public List<SettlementTerms> getCashSettlementTermsOrPhysicalSettlementTerms() {
        if (cashSettlementTermsOrPhysicalSettlementTerms == null) {
            cashSettlementTermsOrPhysicalSettlementTerms = new ArrayList<SettlementTerms>();
        }
        return this.cashSettlementTermsOrPhysicalSettlementTerms;
    }

}