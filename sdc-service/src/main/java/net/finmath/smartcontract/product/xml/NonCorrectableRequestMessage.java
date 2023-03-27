//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining the content model for a request message that cannot be subsequently corrected or retracted.
 * 
 * <p>Java class for NonCorrectableRequestMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NonCorrectableRequestMessage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}RequestMessage"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}CorrelationAndSequence.model" minOccurs="0"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}OnBehalfOf.model" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NonCorrectableRequestMessage", propOrder = {
    "parentCorrelationId",
    "correlationId",
    "sequenceNumber",
    "onBehalfOf"
})
@XmlSeeAlso({
    RequestEventStatus.class,
    RequestRetransmission.class,
    VerificationStatusNotification.class,
    RequestExecutionRetracted.class,
    ExecutionRetracted.class,
    ExecutionAdviceRetracted.class,
    RequestTradeReferenceInformationUpdateRetracted.class,
    TradeChangeAdviceRetracted.class,
    RequestConsentRetracted.class,
    ConfirmationRetracted.class,
    RequestClearingRetracted.class,
    RequestAllocationRetracted.class,
    CreditEventNotificationRetracted.class,
    AccrualOptionChangeNotificationRetracted.class,
    AccruingFeeChangeNotificationRetracted.class,
    AccruingFeePaymentNotificationRetracted.class,
    AccruingPikPaymentNotificationRetracted.class,
    FacilityNotificationRetracted.class,
    FacilityRateChangeNotificationRetracted.class,
    LcNotificationRetracted.class,
    LoanContractNotificationRetracted.class,
    NonRecurringFeePaymentNotificationRetracted.class,
    PrepaymentNotificationRetracted.class,
    RolloverNotificationRetracted.class
})
public class NonCorrectableRequestMessage
    extends RequestMessage
{

    protected CorrelationId parentCorrelationId;
    protected CorrelationId correlationId;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger sequenceNumber;
    protected OnBehalfOf onBehalfOf;

    /**
     * Gets the value of the parentCorrelationId property.
     * 
     * @return
     *     possible object is
     *     {@link CorrelationId }
     *     
     */
    public CorrelationId getParentCorrelationId() {
        return parentCorrelationId;
    }

    /**
     * Sets the value of the parentCorrelationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link CorrelationId }
     *     
     */
    public void setParentCorrelationId(CorrelationId value) {
        this.parentCorrelationId = value;
    }

    /**
     * Gets the value of the correlationId property.
     * 
     * @return
     *     possible object is
     *     {@link CorrelationId }
     *     
     */
    public CorrelationId getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the value of the correlationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link CorrelationId }
     *     
     */
    public void setCorrelationId(CorrelationId value) {
        this.correlationId = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNumber(BigInteger value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the onBehalfOf property.
     * 
     * @return
     *     possible object is
     *     {@link OnBehalfOf }
     *     
     */
    public OnBehalfOf getOnBehalfOf() {
        return onBehalfOf;
    }

    /**
     * Sets the value of the onBehalfOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnBehalfOf }
     *     
     */
    public void setOnBehalfOf(OnBehalfOf value) {
        this.onBehalfOf = value;
    }

}
