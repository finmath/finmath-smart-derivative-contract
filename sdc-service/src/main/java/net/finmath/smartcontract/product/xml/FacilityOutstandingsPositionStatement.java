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
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Position details (including outstandings) for a single facility. Positions can be stated at the global and (optionally) at the lender-specific level.
 * 
 * <p>Java class for FacilityOutstandingsPositionStatement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FacilityOutstandingsPositionStatement"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}SyndicatedLoanStatement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="facilityOutstandingsPosition" type="{http://www.fpml.org/FpML-5/confirmation}FacilityOutstandingsPosition"/&gt;
 *         &lt;element name="positionPartyReference" type="{http://www.fpml.org/FpML-5/confirmation}PartyReference" minOccurs="0"/&gt;
 *         &lt;sequence&gt;
 *           &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}FacilityDetails.model"/&gt;
 *           &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}LoanContractDetails.model" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;element name="party" type="{http://www.fpml.org/FpML-5/confirmation}Party" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FacilityOutstandingsPositionStatement", propOrder = {
    "facilityOutstandingsPosition",
    "positionPartyReference",
    "facilityIdentifier",
    "facilitySummary",
    "loanContractDetailsModel",
    "party"
})
public class FacilityOutstandingsPositionStatement
    extends SyndicatedLoanStatement
{

    @XmlElement(required = true)
    protected FacilityOutstandingsPosition facilityOutstandingsPosition;
    protected PartyReference positionPartyReference;
    protected FacilityIdentifier facilityIdentifier;
    protected FacilitySummary facilitySummary;
    @XmlElements({
        @XmlElement(name = "contractIdentifier", type = FacilityContractIdentifier.class),
        @XmlElement(name = "contractSummary", type = LoanContractSummary.class),
        @XmlElement(name = "contract", type = LoanContract.class)
    })
    protected List<Object> loanContractDetailsModel;
    protected List<Party> party;

    /**
     * Gets the value of the facilityOutstandingsPosition property.
     * 
     * @return
     *     possible object is
     *     {@link FacilityOutstandingsPosition }
     *     
     */
    public FacilityOutstandingsPosition getFacilityOutstandingsPosition() {
        return facilityOutstandingsPosition;
    }

    /**
     * Sets the value of the facilityOutstandingsPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link FacilityOutstandingsPosition }
     *     
     */
    public void setFacilityOutstandingsPosition(FacilityOutstandingsPosition value) {
        this.facilityOutstandingsPosition = value;
    }

    /**
     * Gets the value of the positionPartyReference property.
     * 
     * @return
     *     possible object is
     *     {@link PartyReference }
     *     
     */
    public PartyReference getPositionPartyReference() {
        return positionPartyReference;
    }

    /**
     * Sets the value of the positionPartyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyReference }
     *     
     */
    public void setPositionPartyReference(PartyReference value) {
        this.positionPartyReference = value;
    }

    /**
     * Gets the value of the facilityIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link FacilityIdentifier }
     *     
     */
    public FacilityIdentifier getFacilityIdentifier() {
        return facilityIdentifier;
    }

    /**
     * Sets the value of the facilityIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link FacilityIdentifier }
     *     
     */
    public void setFacilityIdentifier(FacilityIdentifier value) {
        this.facilityIdentifier = value;
    }

    /**
     * Gets the value of the facilitySummary property.
     * 
     * @return
     *     possible object is
     *     {@link FacilitySummary }
     *     
     */
    public FacilitySummary getFacilitySummary() {
        return facilitySummary;
    }

    /**
     * Sets the value of the facilitySummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link FacilitySummary }
     *     
     */
    public void setFacilitySummary(FacilitySummary value) {
        this.facilitySummary = value;
    }

    /**
     * Gets the value of the loanContractDetailsModel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the loanContractDetailsModel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLoanContractDetailsModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FacilityContractIdentifier }
     * {@link LoanContractSummary }
     * {@link LoanContract }
     * 
     * 
     */
    public List<Object> getLoanContractDetailsModel() {
        if (loanContractDetailsModel == null) {
            loanContractDetailsModel = new ArrayList<Object>();
        }
        return this.loanContractDetailsModel;
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

}