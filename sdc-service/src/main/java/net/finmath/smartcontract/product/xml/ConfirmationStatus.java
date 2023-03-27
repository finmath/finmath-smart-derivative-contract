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
 * Message for sending matching results. Response message that returns the status of an event that have been submitted for matching.
 * 
 * <p>Java class for ConfirmationStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConfirmationStatus"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}ResponseMessage"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{http://www.fpml.org/FpML-5/confirmation}EventStatus"/&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="assertedEvent" type="{http://www.fpml.org/FpML-5/confirmation}EventsChoice"/&gt;
 *             &lt;element name="proposedMatch" type="{http://www.fpml.org/FpML-5/confirmation}EventProposedMatch" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="allegedEvent" type="{http://www.fpml.org/FpML-5/confirmation}EventsChoice"/&gt;
 *         &lt;/choice&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}PartiesAndAccounts.model" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfirmationStatus", propOrder = {
    "status",
    "assertedEvent",
    "proposedMatch",
    "allegedEvent",
    "party",
    "account"
})
public class ConfirmationStatus
    extends ResponseMessage
{

    @XmlElement(required = true)
    protected EventStatus status;
    protected EventsChoice assertedEvent;
    protected List<EventProposedMatch> proposedMatch;
    protected EventsChoice allegedEvent;
    protected List<Party> party;
    protected List<Account> account;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link EventStatus }
     *     
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventStatus }
     *     
     */
    public void setStatus(EventStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the assertedEvent property.
     * 
     * @return
     *     possible object is
     *     {@link EventsChoice }
     *     
     */
    public EventsChoice getAssertedEvent() {
        return assertedEvent;
    }

    /**
     * Sets the value of the assertedEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventsChoice }
     *     
     */
    public void setAssertedEvent(EventsChoice value) {
        this.assertedEvent = value;
    }

    /**
     * Gets the value of the proposedMatch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the proposedMatch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProposedMatch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventProposedMatch }
     * 
     * 
     */
    public List<EventProposedMatch> getProposedMatch() {
        if (proposedMatch == null) {
            proposedMatch = new ArrayList<EventProposedMatch>();
        }
        return this.proposedMatch;
    }

    /**
     * Gets the value of the allegedEvent property.
     * 
     * @return
     *     possible object is
     *     {@link EventsChoice }
     *     
     */
    public EventsChoice getAllegedEvent() {
        return allegedEvent;
    }

    /**
     * Sets the value of the allegedEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventsChoice }
     *     
     */
    public void setAllegedEvent(EventsChoice value) {
        this.allegedEvent = value;
    }

    /**
     * Gets the value of the party property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the party property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Party }
     * 
     * 
     */
    public List<Party> getParty() {
        if (party == null) {
            party = new ArrayList<Party>();
        }
        return this.party;
    }

    /**
     * Gets the value of the account property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the account property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Account }
     * 
     * 
     */
    public List<Account> getAccount() {
        if (account == null) {
            account = new ArrayList<Account>();
        }
        return this.account;
    }

}