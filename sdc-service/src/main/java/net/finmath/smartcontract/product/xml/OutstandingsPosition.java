//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Represents outstanding loan contracts or outstanding letter of credit position. Both the global and lender position (current and prior) levels can be represented.
 * 
 * <p>Java class for OutstandingsPosition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutstandingsPosition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="loanContractReference" type="{http://www.fpml.org/FpML-5/confirmation}LoanContractReference"/&gt;
 *           &lt;element name="letterOfCreditReference" type="{http://www.fpml.org/FpML-5/confirmation}LetterOfCreditReference"/&gt;
 *         &lt;/choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="amount" type="{http://www.fpml.org/FpML-5/confirmation}MoneyWithParticipantShare"/&gt;
 *           &lt;element name="priorAmount" type="{http://www.fpml.org/FpML-5/confirmation}MoneyWithParticipantShare" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;element name="facilityFxRate" type="{http://www.fpml.org/FpML-5/confirmation}FxTerms" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutstandingsPosition", propOrder = {
    "loanContractReference",
    "letterOfCreditReference",
    "amount",
    "priorAmount",
    "facilityFxRate"
})
public class OutstandingsPosition {

    protected LoanContractReference loanContractReference;
    protected LetterOfCreditReference letterOfCreditReference;
    @XmlElement(required = true)
    protected MoneyWithParticipantShare amount;
    protected MoneyWithParticipantShare priorAmount;
    protected FxTerms facilityFxRate;

    /**
     * Gets the value of the loanContractReference property.
     * 
     * @return
     *     possible object is
     *     {@link LoanContractReference }
     *     
     */
    public LoanContractReference getLoanContractReference() {
        return loanContractReference;
    }

    /**
     * Sets the value of the loanContractReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoanContractReference }
     *     
     */
    public void setLoanContractReference(LoanContractReference value) {
        this.loanContractReference = value;
    }

    /**
     * Gets the value of the letterOfCreditReference property.
     * 
     * @return
     *     possible object is
     *     {@link LetterOfCreditReference }
     *     
     */
    public LetterOfCreditReference getLetterOfCreditReference() {
        return letterOfCreditReference;
    }

    /**
     * Sets the value of the letterOfCreditReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link LetterOfCreditReference }
     *     
     */
    public void setLetterOfCreditReference(LetterOfCreditReference value) {
        this.letterOfCreditReference = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link MoneyWithParticipantShare }
     *     
     */
    public MoneyWithParticipantShare getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MoneyWithParticipantShare }
     *     
     */
    public void setAmount(MoneyWithParticipantShare value) {
        this.amount = value;
    }

    /**
     * Gets the value of the priorAmount property.
     * 
     * @return
     *     possible object is
     *     {@link MoneyWithParticipantShare }
     *     
     */
    public MoneyWithParticipantShare getPriorAmount() {
        return priorAmount;
    }

    /**
     * Sets the value of the priorAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link MoneyWithParticipantShare }
     *     
     */
    public void setPriorAmount(MoneyWithParticipantShare value) {
        this.priorAmount = value;
    }

    /**
     * Gets the value of the facilityFxRate property.
     * 
     * @return
     *     possible object is
     *     {@link FxTerms }
     *     
     */
    public FxTerms getFacilityFxRate() {
        return facilityFxRate;
    }

    /**
     * Sets the value of the facilityFxRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link FxTerms }
     *     
     */
    public void setFacilityFxRate(FxTerms value) {
        this.facilityFxRate = value;
    }

}