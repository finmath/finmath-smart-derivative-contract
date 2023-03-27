//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A structure describing an option expiring (i.e. passing its last exercise time and becoming worthless.)
 * 
 * <p>Java class for OptionExpiry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OptionExpiry"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}AbstractEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="originalTrade" type="{http://www.fpml.org/FpML-5/confirmation}Trade"/&gt;
 *           &lt;element name="tradeIdentifier" type="{http://www.fpml.org/FpML-5/confirmation}PartyTradeIdentifier" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}time"/&gt;
 *         &lt;element name="exerciseProcedure" type="{http://www.fpml.org/FpML-5/confirmation}ExerciseProcedureOption" minOccurs="0"/&gt;
 *         &lt;element name="actionOnExpiration" type="{http://www.fpml.org/FpML-5/confirmation}ActionOnExpiration" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionExpiry", propOrder = {
    "originalTrade",
    "tradeIdentifier",
    "date",
    "time",
    "exerciseProcedure",
    "actionOnExpiration"
})
public class OptionExpiry
    extends AbstractEvent
{

    protected Trade originalTrade;
    protected List<PartyTradeIdentifier> tradeIdentifier;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlElement(required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar time;
    protected ExerciseProcedureOption exerciseProcedure;
    protected ActionOnExpiration actionOnExpiration;

    /**
     * Gets the value of the originalTrade property.
     * 
     * @return
     *     possible object is
     *     {@link Trade }
     *     
     */
    public Trade getOriginalTrade() {
        return originalTrade;
    }

    /**
     * Sets the value of the originalTrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trade }
     *     
     */
    public void setOriginalTrade(Trade value) {
        this.originalTrade = value;
    }

    /**
     * Gets the value of the tradeIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the tradeIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTradeIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartyTradeIdentifier }
     * 
     * 
     */
    public List<PartyTradeIdentifier> getTradeIdentifier() {
        if (tradeIdentifier == null) {
            tradeIdentifier = new ArrayList<PartyTradeIdentifier>();
        }
        return this.tradeIdentifier;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Gets the value of the exerciseProcedure property.
     * 
     * @return
     *     possible object is
     *     {@link ExerciseProcedureOption }
     *     
     */
    public ExerciseProcedureOption getExerciseProcedure() {
        return exerciseProcedure;
    }

    /**
     * Sets the value of the exerciseProcedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExerciseProcedureOption }
     *     
     */
    public void setExerciseProcedure(ExerciseProcedureOption value) {
        this.exerciseProcedure = value;
    }

    /**
     * Gets the value of the actionOnExpiration property.
     * 
     * @return
     *     possible object is
     *     {@link ActionOnExpiration }
     *     
     */
    public ActionOnExpiration getActionOnExpiration() {
        return actionOnExpiration;
    }

    /**
     * Sets the value of the actionOnExpiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionOnExpiration }
     *     
     */
    public void setActionOnExpiration(ActionOnExpiration value) {
        this.actionOnExpiration = value;
    }

}