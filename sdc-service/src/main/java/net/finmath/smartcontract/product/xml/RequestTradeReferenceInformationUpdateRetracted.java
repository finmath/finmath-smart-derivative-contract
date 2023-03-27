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
 * Defines the structure for a message retracting a request to updated information about trade.
 * 
 * <p>Java class for RequestTradeReferenceInformationUpdateRetracted complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestTradeReferenceInformationUpdateRetracted"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}NonCorrectableRequestMessage"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}TradeReferenceInformation.model"/&gt;
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
@XmlType(name = "RequestTradeReferenceInformationUpdateRetracted", propOrder = {
    "tradeReferenceInformation",
    "party",
    "account"
})
public class RequestTradeReferenceInformationUpdateRetracted
    extends NonCorrectableRequestMessage
{

    @XmlElement(required = true)
    protected TradeReferenceInformation tradeReferenceInformation;
    protected List<Party> party;
    protected List<Account> account;

    /**
     * Gets the value of the tradeReferenceInformation property.
     * 
     * @return
     *     possible object is
     *     {@link TradeReferenceInformation }
     *     
     */
    public TradeReferenceInformation getTradeReferenceInformation() {
        return tradeReferenceInformation;
    }

    /**
     * Sets the value of the tradeReferenceInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TradeReferenceInformation }
     *     
     */
    public void setTradeReferenceInformation(TradeReferenceInformation value) {
        this.tradeReferenceInformation = value;
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
