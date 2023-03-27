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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A structured forward product which consists of a strip of forwards. Each forward may be settled as an exchange of currencies or cash settled. At each settlement, the amount of gain that one party achieves is measured. The product has a target level of gain. Once the accumulated gain exceeds the target level, the product terminates and there are no further settlements.
 * 
 * <p>Java class for FxTargetKnockoutForward complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FxTargetKnockoutForward"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}Product"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="quotedCurrencyPair" type="{http://www.fpml.org/FpML-5/confirmation}QuotedCurrencyPair"/&gt;
 *         &lt;element name="notionalAmount" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeAmountSchedule"/&gt;
 *         &lt;element name="target" type="{http://www.fpml.org/FpML-5/confirmation}FxTarget" maxOccurs="2"/&gt;
 *         &lt;element name="expirySchedule" type="{http://www.fpml.org/FpML-5/confirmation}FxExpirySchedule"/&gt;
 *         &lt;element name="settlementSchedule" type="{http://www.fpml.org/FpML-5/confirmation}FxSettlementSchedule"/&gt;
 *         &lt;element name="fixingInformationSource" type="{http://www.fpml.org/FpML-5/confirmation}FxInformationSource"/&gt;
 *         &lt;element name="spotRate" type="{http://www.fpml.org/FpML-5/confirmation}NonNegativeDecimal" minOccurs="0"/&gt;
 *         &lt;sequence&gt;
 *           &lt;sequence minOccurs="0"&gt;
 *             &lt;element name="pivot" type="{http://www.fpml.org/FpML-5/confirmation}FxPivot"/&gt;
 *             &lt;element name="constantPayoffRegion" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetConstantPayoffRegion" maxOccurs="unbounded" minOccurs="0"/&gt;
 *             &lt;element name="linearPayoffRegion" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetLinearPayoffRegion"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="constantPayoffRegion" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetConstantPayoffRegion" maxOccurs="unbounded" minOccurs="0"/&gt;
 *             &lt;element name="linearPayoffRegion" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetLinearPayoffRegion"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *             &lt;element name="constantPayoffRegion" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetConstantPayoffRegion"/&gt;
 *             &lt;element name="linearPayoffRegion" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetLinearPayoffRegion"/&gt;
 *           &lt;/choice&gt;
 *         &lt;/sequence&gt;
 *         &lt;element name="barrier" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetBarrier" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="additionalPayment" type="{http://www.fpml.org/FpML-5/confirmation}SimplePayment" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="cashSettlement" type="{http://www.fpml.org/FpML-5/confirmation}FxCashSettlementSimple" minOccurs="0"/&gt;
 *         &lt;element name="settlementPeriodSchedule" type="{http://www.fpml.org/FpML-5/confirmation}FxTargetSettlementPeriodSchedule" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FxTargetKnockoutForward", propOrder = {
    "rest"
})
public class FxTargetKnockoutForward
    extends Product
{

    @XmlElementRefs({
        @XmlElementRef(name = "quotedCurrencyPair", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "notionalAmount", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "target", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "expirySchedule", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "settlementSchedule", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "fixingInformationSource", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "spotRate", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "pivot", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "constantPayoffRegion", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "linearPayoffRegion", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "barrier", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "additionalPayment", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "cashSettlement", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "settlementPeriodSchedule", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> rest;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "ConstantPayoffRegion" is used by two different parts of a schema. See: 
     * line 489 of file:/C:/Users/xn85719/IdeaProjects/finmath-smart-derivative-contract/sdc-service/src/main/resources/schemas/fpml-schemas/fpml-fx-targets-5-9.xsd
     * line 477 of file:/C:/Users/xn85719/IdeaProjects/finmath-smart-derivative-contract/sdc-service/src/main/resources/schemas/fpml-schemas/fpml-fx-targets-5-9.xsd
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
     * {@link JAXBElement }{@code <}{@link QuotedCurrencyPair }{@code >}
     * {@link JAXBElement }{@code <}{@link NonNegativeAmountSchedule }{@code >}
     * {@link JAXBElement }{@code <}{@link FxTarget }{@code >}
     * {@link JAXBElement }{@code <}{@link FxExpirySchedule }{@code >}
     * {@link JAXBElement }{@code <}{@link FxSettlementSchedule }{@code >}
     * {@link JAXBElement }{@code <}{@link FxInformationSource }{@code >}
     * {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     * {@link JAXBElement }{@code <}{@link FxPivot }{@code >}
     * {@link JAXBElement }{@code <}{@link FxTargetConstantPayoffRegion }{@code >}
     * {@link JAXBElement }{@code <}{@link FxTargetLinearPayoffRegion }{@code >}
     * {@link JAXBElement }{@code <}{@link FxTargetBarrier }{@code >}
     * {@link JAXBElement }{@code <}{@link SimplePayment }{@code >}
     * {@link JAXBElement }{@code <}{@link FxCashSettlementSimple }{@code >}
     * {@link JAXBElement }{@code <}{@link FxTargetSettlementPeriodSchedule }{@code >}
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