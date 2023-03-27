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
import jakarta.xml.bind.annotation.XmlType;


/**
 * Container for Dividend Adjustment Periods, which are used to calculate the Deviation between Expected Dividend and Actual Dividend in that Period.
 * 
 * <p>Java class for DividendAdjustment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DividendAdjustment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dividendPeriod" type="{http://www.fpml.org/FpML-5/confirmation}DividendPeriodDividend" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DividendAdjustment", propOrder = {
    "dividendPeriod"
})
public class DividendAdjustment {

    @XmlElement(required = true)
    protected List<DividendPeriodDividend> dividendPeriod;

    /**
     * Gets the value of the dividendPeriod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dividendPeriod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDividendPeriod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DividendPeriodDividend }
     * 
     * 
     */
    public List<DividendPeriodDividend> getDividendPeriod() {
        if (dividendPeriod == null) {
            dividendPeriod = new ArrayList<DividendPeriodDividend>();
        }
        return this.dividendPeriod;
    }

}