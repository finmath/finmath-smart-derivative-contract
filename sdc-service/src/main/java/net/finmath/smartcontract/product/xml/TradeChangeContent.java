//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A structure describing a non-negotiated trade resulting from a market event.
 * 
 * <p>Java class for TradeChangeContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TradeChangeContent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="oldTradeIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}PartyTradeIdentifier"/&gt;
 *           &lt;element name="oldTrade" type="{http://www.fpml.org/FpML-5/confirmation}Trade"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="trade" type="{http://www.fpml.org/FpML-5/confirmation}Trade"/&gt;
 *         &lt;element name="effectiveDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element ref="{http://www.fpml.org/FpML-5/confirmation}changeEvent"/&gt;
 *         &lt;element name="payment" type="{http://www.fpml.org/FpML-5/confirmation}Payment" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TradeChangeContent", propOrder = {
    "oldTradeIdentifier",
    "oldTrade",
    "trade",
    "effectiveDate",
    "changeEvent",
    "payment"
})
public class TradeChangeContent {

    protected PartyTradeIdentifier oldTradeIdentifier;
    protected Trade oldTrade;
    @XmlElement(required = true)
    protected Trade trade;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar effectiveDate;
    @XmlElementRef(name = "changeEvent", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class)
    protected JAXBElement<? extends ChangeEvent> changeEvent;
    protected Payment payment;

    /**
     * Gets the value of the oldTradeIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link PartyTradeIdentifier }
     *     
     */
    public PartyTradeIdentifier getOldTradeIdentifier() {
        return oldTradeIdentifier;
    }

    /**
     * Sets the value of the oldTradeIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyTradeIdentifier }
     *     
     */
    public void setOldTradeIdentifier(PartyTradeIdentifier value) {
        this.oldTradeIdentifier = value;
    }

    /**
     * Gets the value of the oldTrade property.
     * 
     * @return
     *     possible object is
     *     {@link Trade }
     *     
     */
    public Trade getOldTrade() {
        return oldTrade;
    }

    /**
     * Sets the value of the oldTrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trade }
     *     
     */
    public void setOldTrade(Trade value) {
        this.oldTrade = value;
    }

    /**
     * Gets the value of the trade property.
     * 
     * @return
     *     possible object is
     *     {@link Trade }
     *     
     */
    public Trade getTrade() {
        return trade;
    }

    /**
     * Sets the value of the trade property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trade }
     *     
     */
    public void setTrade(Trade value) {
        this.trade = value;
    }

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveDate(XMLGregorianCalendar value) {
        this.effectiveDate = value;
    }

    /**
     * Substitution point for types of change
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CorporateActionEvent }{@code >}
     *     {@link JAXBElement }{@code <}{@link IndexChange }{@code >}
     *     {@link JAXBElement }{@code <}{@link BasketChangeEvent }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChangeEvent }{@code >}
     *     
     */
    public JAXBElement<? extends ChangeEvent> getChangeEvent() {
        return changeEvent;
    }

    /**
     * Sets the value of the changeEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CorporateActionEvent }{@code >}
     *     {@link JAXBElement }{@code <}{@link IndexChange }{@code >}
     *     {@link JAXBElement }{@code <}{@link BasketChangeEvent }{@code >}
     *     {@link JAXBElement }{@code <}{@link ChangeEvent }{@code >}
     *     
     */
    public void setChangeEvent(JAXBElement<? extends ChangeEvent> value) {
        this.changeEvent = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link Payment }
     *     
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment }
     *     
     */
    public void setPayment(Payment value) {
        this.payment = value;
    }

}