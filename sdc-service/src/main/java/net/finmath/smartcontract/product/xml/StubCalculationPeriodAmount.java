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
 * A type defining how the initial or final stub calculation period amounts is calculated. For example, the rate to be applied to the initial or final stub calculation period may be the linear interpolation of two different tenors for the floating rate index specified in the calculation period amount component, e.g. A two month stub period may used the linear interpolation of a one month and three month floating rate. The different rate tenors would be specified in this component. Note that a maximum of two rate tenors can be specified. If a stub period uses a single index tenor and this is the same as that specified in the calculation period amount component then the initial stub or final stub component, as the case may be, must not be included.
 * 
 * <p>Java class for StubCalculationPeriodAmount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StubCalculationPeriodAmount"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="calculationPeriodDatesReference" type="{http://www.fpml.org/FpML-5/confirmation}CalculationPeriodDatesReference"/&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="initialStub" type="{http://www.fpml.org/FpML-5/confirmation}StubValue"/&gt;
 *             &lt;element name="finalStub" type="{http://www.fpml.org/FpML-5/confirmation}StubValue" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="finalStub" type="{http://www.fpml.org/FpML-5/confirmation}StubValue"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StubCalculationPeriodAmount", propOrder = {
    "content"
})
public class StubCalculationPeriodAmount {

    @XmlElementRefs({
        @XmlElementRef(name = "calculationPeriodDatesReference", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "initialStub", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "finalStub", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> content;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "FinalStub" is used by two different parts of a schema. See: 
     * line 1499 of file:/C:/Users/xn85719/IdeaProjects/finmath-smart-derivative-contract/sdc-service/src/main/resources/schemas/fpml-schemas/fpml-ird-5-9.xsd
     * line 1493 of file:/C:/Users/xn85719/IdeaProjects/finmath-smart-derivative-contract/sdc-service/src/main/resources/schemas/fpml-schemas/fpml-ird-5-9.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link CalculationPeriodDatesReference }{@code >}
     * {@link JAXBElement }{@code <}{@link StubValue }{@code >}
     * {@link JAXBElement }{@code <}{@link StubValue }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return this.content;
    }

}
