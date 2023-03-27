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
 * This type contains all the reference pool items to define the reference entity and reference obligation(s) in the basket.
 * 
 * <p>Java class for ReferencePool complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferencePool"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="referencePoolItem" type="{http://www.fpml.org/FpML-5/confirmation}ReferencePoolItem" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferencePool", propOrder = {
    "referencePoolItem"
})
public class ReferencePool {

    @XmlElement(required = true)
    protected List<ReferencePoolItem> referencePoolItem;

    /**
     * Gets the value of the referencePoolItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the referencePoolItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencePoolItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferencePoolItem }
     * 
     * 
     */
    public List<ReferencePoolItem> getReferencePoolItem() {
        if (referencePoolItem == null) {
            referencePoolItem = new ArrayList<ReferencePoolItem>();
        }
        return this.referencePoolItem;
    }

}
