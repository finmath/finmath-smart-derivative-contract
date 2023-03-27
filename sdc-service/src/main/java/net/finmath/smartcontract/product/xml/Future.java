//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * An exchange traded future contract.
 * 
 * <p>Java class for Future complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Future"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.fpml.org/FpML-5/confirmation}ExchangeTraded"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="multiplier" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="futureContractReference" type="{http://www.fpml.org/FpML-5/confirmation}String" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="maturity" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *           &lt;element name="contractYearMonth" type="{http://www.w3.org/2001/XMLSchema}gYearMonth"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Future", propOrder = {
    "multiplier",
    "futureContractReference",
    "maturity",
    "contractYearMonth"
})
public class Future
    extends ExchangeTraded
{

    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger multiplier;
    protected String futureContractReference;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar maturity;
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar contractYearMonth;

    /**
     * Gets the value of the multiplier property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMultiplier() {
        return multiplier;
    }

    /**
     * Sets the value of the multiplier property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMultiplier(BigInteger value) {
        this.multiplier = value;
    }

    /**
     * Gets the value of the futureContractReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFutureContractReference() {
        return futureContractReference;
    }

    /**
     * Sets the value of the futureContractReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFutureContractReference(String value) {
        this.futureContractReference = value;
    }

    /**
     * Gets the value of the maturity property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMaturity() {
        return maturity;
    }

    /**
     * Sets the value of the maturity property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMaturity(XMLGregorianCalendar value) {
        this.maturity = value;
    }

    /**
     * Gets the value of the contractYearMonth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getContractYearMonth() {
        return contractYearMonth;
    }

    /**
     * Sets the value of the contractYearMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setContractYearMonth(XMLGregorianCalendar value) {
        this.contractYearMonth = value;
    }

}