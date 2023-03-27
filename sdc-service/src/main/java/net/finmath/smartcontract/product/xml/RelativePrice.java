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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type which represents Pricing relative to a Benchmark.
 * 
 * <p>Java class for RelativePrice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelativePrice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="spread" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}BondEquity.model" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelativePrice", propOrder = {
    "spread",
    "bondEquityModel"
})
public class RelativePrice {

    @XmlElement(required = true)
    protected BigDecimal spread;
    @XmlElements({
        @XmlElement(name = "bond", type = Bond.class),
        @XmlElement(name = "convertibleBond", type = ConvertibleBond.class),
        @XmlElement(name = "equity", type = EquityAsset.class)
    })
    protected List<UnderlyingAsset> bondEquityModel;

    /**
     * Gets the value of the spread property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpread() {
        return spread;
    }

    /**
     * Sets the value of the spread property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpread(BigDecimal value) {
        this.spread = value;
    }

    /**
     * The benchmark being referred to; either a bond or equity product.Gets the value of the bondEquityModel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the bondEquityModel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBondEquityModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bond }
     * {@link ConvertibleBond }
     * {@link EquityAsset }
     * 
     * 
     */
    public List<UnderlyingAsset> getBondEquityModel() {
        if (bondEquityModel == null) {
            bondEquityModel = new ArrayList<UnderlyingAsset>();
        }
        return this.bondEquityModel;
    }

}
