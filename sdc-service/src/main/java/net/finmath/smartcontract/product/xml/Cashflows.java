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
 * A type defining the cashflow representation of a swap trade.
 * 
 * <p>Java class for Cashflows complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Cashflows"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cashflowsMatchParameters" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="principalExchange" type="{http://www.fpml.org/FpML-5/confirmation}PrincipalExchange" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="paymentCalculationPeriod" type="{http://www.fpml.org/FpML-5/confirmation}PaymentCalculationPeriod" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cashflows", propOrder = {
    "cashflowsMatchParameters",
    "principalExchange",
    "paymentCalculationPeriod"
})
public class Cashflows {

    protected boolean cashflowsMatchParameters;
    protected List<PrincipalExchange> principalExchange;
    protected List<PaymentCalculationPeriod> paymentCalculationPeriod;

    /**
     * Gets the value of the cashflowsMatchParameters property.
     * 
     */
    public boolean isCashflowsMatchParameters() {
        return cashflowsMatchParameters;
    }

    /**
     * Sets the value of the cashflowsMatchParameters property.
     * 
     */
    public void setCashflowsMatchParameters(boolean value) {
        this.cashflowsMatchParameters = value;
    }

    /**
     * Gets the value of the principalExchange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the principalExchange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrincipalExchange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrincipalExchange }
     * 
     * 
     */
    public List<PrincipalExchange> getPrincipalExchange() {
        if (principalExchange == null) {
            principalExchange = new ArrayList<PrincipalExchange>();
        }
        return this.principalExchange;
    }

    /**
     * Gets the value of the paymentCalculationPeriod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the paymentCalculationPeriod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentCalculationPeriod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentCalculationPeriod }
     * 
     * 
     */
    public List<PaymentCalculationPeriod> getPaymentCalculationPeriod() {
        if (paymentCalculationPeriod == null) {
            paymentCalculationPeriod = new ArrayList<PaymentCalculationPeriod>();
        }
        return this.paymentCalculationPeriod;
    }

}
