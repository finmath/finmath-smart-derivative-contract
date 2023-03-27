//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A loan servicing retraction used to cancel a previous PIK rate payment.
 * 
 * <p>Java class for AccruingPikPaymentNotificationRetracted complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccruingPikPaymentNotificationRetracted"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}NonCorrectableRequestMessage"&gt;
 *       &lt;choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="eventIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}BusinessEventIdentifier"/&gt;
 *           &lt;element name="party" type="{http://www.fpml.org/FpML-5/confirmation}Party"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="accruingPikPayment" type="{http://www.fpml.org/FpML-5/confirmation}AccruingPikPayment"/&gt;
 *           &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}FacilityDetails.model"/&gt;
 *           &lt;element name="party" type="{http://www.fpml.org/FpML-5/confirmation}Party" maxOccurs="unbounded"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/choice&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccruingPikPaymentNotificationRetracted", propOrder = {
    "rest"
})
public class AccruingPikPaymentNotificationRetracted
    extends NonCorrectableRequestMessage
{

    @XmlElementRefs({
        @XmlElementRef(name = "eventIdentifier", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "party", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "accruingPikPayment", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "facilityIdentifier", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "facilitySummary", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> rest;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "Party" is used by two different parts of a schema. See: 
     * line 514 of file:/C:/Users/xn85719/IdeaProjects/finmath-smart-derivative-contract/sdc-service/src/main/resources/schemas/fpml-schemas/fpml-loan-5-9.xsd
     * line 505 of file:/C:/Users/xn85719/IdeaProjects/finmath-smart-derivative-contract/sdc-service/src/main/resources/schemas/fpml-schemas/fpml-loan-5-9.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BusinessEventIdentifier }{@code >}
     * {@link JAXBElement }{@code <}{@link Party }{@code >}
     * {@link JAXBElement }{@code <}{@link AccruingPikPayment }{@code >}
     * {@link JAXBElement }{@code <}{@link FacilityIdentifier }{@code >}
     * {@link JAXBElement }{@code <}{@link FacilitySummary }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<?>>();
        }
        return this.rest;
    }

}
