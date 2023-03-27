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
import jakarta.xml.bind.annotation.XmlType;


/**
 * Represents current/prior facility commitment and outstanding amounts on both the global and lender position levels.
 * 
 * <p>Java class for FacilityOutstandingsPosition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FacilityOutstandingsPosition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}FacilityPosition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="outstandingsPosition" type="{http://www.fpml.org/FpML-5/confirmation}OutstandingsPosition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FacilityOutstandingsPosition", propOrder = {
    "outstandingsPosition"
})
public class FacilityOutstandingsPosition
    extends FacilityPosition
{

    protected List<OutstandingsPosition> outstandingsPosition;

    /**
     * Gets the value of the outstandingsPosition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the outstandingsPosition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutstandingsPosition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OutstandingsPosition }
     * 
     * 
     */
    public List<OutstandingsPosition> getOutstandingsPosition() {
        if (outstandingsPosition == null) {
            outstandingsPosition = new ArrayList<OutstandingsPosition>();
        }
        return this.outstandingsPosition;
    }

}