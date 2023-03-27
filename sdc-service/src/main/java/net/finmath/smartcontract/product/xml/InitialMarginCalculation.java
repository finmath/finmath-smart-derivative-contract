//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Defines the initial margin calculation applicable to a single piece of collateral.
 * 
 * <p>Java class for InitialMarginCalculation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InitialMarginCalculation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="marginRatio" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *             &lt;element name="marginRatioThreshold" type="{http://www.w3.org/2001/XMLSchema}decimal" maxOccurs="2" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="haircut" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *             &lt;element name="haircutThreshold" type="{http://www.w3.org/2001/XMLSchema}decimal" maxOccurs="2" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="assetReference" type="{http://www.fpml.org/FpML-5/confirmation}AssetReference" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InitialMarginCalculation", propOrder = {
    "marginRatio",
    "marginRatioThreshold",
    "haircut",
    "haircutThreshold",
    "assetReference"
})
public class InitialMarginCalculation {

    protected BigDecimal marginRatio;
    protected List<BigDecimal> marginRatioThreshold;
    protected BigDecimal haircut;
    protected List<BigDecimal> haircutThreshold;
    protected AssetReference assetReference;

    /**
     * Gets the value of the marginRatio property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMarginRatio() {
        return marginRatio;
    }

    /**
     * Sets the value of the marginRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMarginRatio(BigDecimal value) {
        this.marginRatio = value;
    }

    /**
     * Gets the value of the marginRatioThreshold property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the marginRatioThreshold property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMarginRatioThreshold().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigDecimal }
     * 
     * 
     */
    public List<BigDecimal> getMarginRatioThreshold() {
        if (marginRatioThreshold == null) {
            marginRatioThreshold = new ArrayList<BigDecimal>();
        }
        return this.marginRatioThreshold;
    }

    /**
     * Gets the value of the haircut property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getHaircut() {
        return haircut;
    }

    /**
     * Sets the value of the haircut property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setHaircut(BigDecimal value) {
        this.haircut = value;
    }

    /**
     * Gets the value of the haircutThreshold property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the haircutThreshold property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHaircutThreshold().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigDecimal }
     * 
     * 
     */
    public List<BigDecimal> getHaircutThreshold() {
        if (haircutThreshold == null) {
            haircutThreshold = new ArrayList<BigDecimal>();
        }
        return this.haircutThreshold;
    }

    /**
     * Gets the value of the assetReference property.
     * 
     * @return
     *     possible object is
     *     {@link AssetReference }
     *     
     */
    public AssetReference getAssetReference() {
        return assetReference;
    }

    /**
     * Sets the value of the assetReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetReference }
     *     
     */
    public void setAssetReference(AssetReference value) {
        this.assetReference = value;
    }

}