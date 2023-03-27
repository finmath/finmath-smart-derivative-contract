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
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * A single valued point with a set of coordinates that define an arbitrary number of indentifying indexes (0 or more). Note that the collection of coordinates/coordinate references for a PricingStructurePoint must not define a given dimension (other than "generic") more than once. This is to avoid ambiguity.
 * 
 * <p>Java class for PricingStructurePoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PricingStructurePoint"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}PricingCoordinateOrReference.model" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}UnderlyingAssetOrReference.model" minOccurs="0"/&gt;
 *         &lt;group ref="{http://www.fpml.org/FpML-5/confirmation}Quotation.model"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PricingStructurePoint", propOrder = {
    "pricingCoordinateOrReferenceModel",
    "underlyingAsset",
    "underlyingAssetReference",
    "value",
    "measureType",
    "quoteUnits",
    "side",
    "currency",
    "currencyType",
    "timing",
    "businessCenter",
    "exchangeId",
    "informationSource",
    "pricingModel",
    "time",
    "valuationDate",
    "expiryTime",
    "cashflowType"
})
public class PricingStructurePoint {

    @XmlElements({
        @XmlElement(name = "coordinate", type = PricingDataPointCoordinate.class),
        @XmlElement(name = "coordinateReference", type = PricingDataPointCoordinateReference.class)
    })
    protected List<Object> pricingCoordinateOrReferenceModel;
    @XmlElementRef(name = "underlyingAsset", namespace = "http://www.fpml.org/FpML-5/confirmation", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends Asset> underlyingAsset;
    protected AssetReference underlyingAssetReference;
    protected BigDecimal value;
    protected AssetMeasureType measureType;
    protected PriceQuoteUnits quoteUnits;
    @XmlSchemaType(name = "token")
    protected QuotationSideEnum side;
    protected Currency currency;
    protected ReportingCurrencyType currencyType;
    protected QuoteTiming timing;
    protected BusinessCenter businessCenter;
    protected ExchangeId exchangeId;
    protected List<InformationSource> informationSource;
    protected PricingModel pricingModel;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar valuationDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expiryTime;
    protected CashflowType cashflowType;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the pricingCoordinateOrReferenceModel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the pricingCoordinateOrReferenceModel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPricingCoordinateOrReferenceModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PricingDataPointCoordinate }
     * {@link PricingDataPointCoordinateReference }
     * 
     * 
     */
    public List<Object> getPricingCoordinateOrReferenceModel() {
        if (pricingCoordinateOrReferenceModel == null) {
            pricingCoordinateOrReferenceModel = new ArrayList<Object>();
        }
        return this.pricingCoordinateOrReferenceModel;
    }

    /**
     * An underlying asset that defines the meaning of the value, i.e. the product that the value corresponds to. For example, this could be a caplet or simple european swaption.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Index }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityAsset }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExchangeTradedFund }{@code >}
     *     {@link JAXBElement }{@code <}{@link MutualFund }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mortgage }{@code >}
     *     {@link JAXBElement }{@code <}{@link Commodity }{@code >}
     *     {@link JAXBElement }{@code <}{@link Cash }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConvertibleBond }{@code >}
     *     {@link JAXBElement }{@code <}{@link Future }{@code >}
     *     {@link JAXBElement }{@code <}{@link Bond }{@code >}
     *     {@link JAXBElement }{@code <}{@link Loan }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExchangeTradedOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link Basket }{@code >}
     *     {@link JAXBElement }{@code <}{@link Asset }{@code >}
     *     
     */
    public JAXBElement<? extends Asset> getUnderlyingAsset() {
        return underlyingAsset;
    }

    /**
     * Sets the value of the underlyingAsset property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Index }{@code >}
     *     {@link JAXBElement }{@code <}{@link EquityAsset }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExchangeTradedFund }{@code >}
     *     {@link JAXBElement }{@code <}{@link MutualFund }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mortgage }{@code >}
     *     {@link JAXBElement }{@code <}{@link Commodity }{@code >}
     *     {@link JAXBElement }{@code <}{@link Cash }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConvertibleBond }{@code >}
     *     {@link JAXBElement }{@code <}{@link Future }{@code >}
     *     {@link JAXBElement }{@code <}{@link Bond }{@code >}
     *     {@link JAXBElement }{@code <}{@link Loan }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExchangeTradedOption }{@code >}
     *     {@link JAXBElement }{@code <}{@link Basket }{@code >}
     *     {@link JAXBElement }{@code <}{@link Asset }{@code >}
     *     
     */
    public void setUnderlyingAsset(JAXBElement<? extends Asset> value) {
        this.underlyingAsset = value;
    }

    /**
     * Gets the value of the underlyingAssetReference property.
     * 
     * @return
     *     possible object is
     *     {@link AssetReference }
     *     
     */
    public AssetReference getUnderlyingAssetReference() {
        return underlyingAssetReference;
    }

    /**
     * Sets the value of the underlyingAssetReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetReference }
     *     
     */
    public void setUnderlyingAssetReference(AssetReference value) {
        this.underlyingAssetReference = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the measureType property.
     * 
     * @return
     *     possible object is
     *     {@link AssetMeasureType }
     *     
     */
    public AssetMeasureType getMeasureType() {
        return measureType;
    }

    /**
     * Sets the value of the measureType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetMeasureType }
     *     
     */
    public void setMeasureType(AssetMeasureType value) {
        this.measureType = value;
    }

    /**
     * Gets the value of the quoteUnits property.
     * 
     * @return
     *     possible object is
     *     {@link PriceQuoteUnits }
     *     
     */
    public PriceQuoteUnits getQuoteUnits() {
        return quoteUnits;
    }

    /**
     * Sets the value of the quoteUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriceQuoteUnits }
     *     
     */
    public void setQuoteUnits(PriceQuoteUnits value) {
        this.quoteUnits = value;
    }

    /**
     * Gets the value of the side property.
     * 
     * @return
     *     possible object is
     *     {@link QuotationSideEnum }
     *     
     */
    public QuotationSideEnum getSide() {
        return side;
    }

    /**
     * Sets the value of the side property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuotationSideEnum }
     *     
     */
    public void setSide(QuotationSideEnum value) {
        this.side = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency(Currency value) {
        this.currency = value;
    }

    /**
     * Gets the value of the currencyType property.
     * 
     * @return
     *     possible object is
     *     {@link ReportingCurrencyType }
     *     
     */
    public ReportingCurrencyType getCurrencyType() {
        return currencyType;
    }

    /**
     * Sets the value of the currencyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportingCurrencyType }
     *     
     */
    public void setCurrencyType(ReportingCurrencyType value) {
        this.currencyType = value;
    }

    /**
     * Gets the value of the timing property.
     * 
     * @return
     *     possible object is
     *     {@link QuoteTiming }
     *     
     */
    public QuoteTiming getTiming() {
        return timing;
    }

    /**
     * Sets the value of the timing property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuoteTiming }
     *     
     */
    public void setTiming(QuoteTiming value) {
        this.timing = value;
    }

    /**
     * Gets the value of the businessCenter property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessCenter }
     *     
     */
    public BusinessCenter getBusinessCenter() {
        return businessCenter;
    }

    /**
     * Sets the value of the businessCenter property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessCenter }
     *     
     */
    public void setBusinessCenter(BusinessCenter value) {
        this.businessCenter = value;
    }

    /**
     * Gets the value of the exchangeId property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeId }
     *     
     */
    public ExchangeId getExchangeId() {
        return exchangeId;
    }

    /**
     * Sets the value of the exchangeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeId }
     *     
     */
    public void setExchangeId(ExchangeId value) {
        this.exchangeId = value;
    }

    /**
     * Gets the value of the informationSource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the informationSource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInformationSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InformationSource }
     * 
     * 
     */
    public List<InformationSource> getInformationSource() {
        if (informationSource == null) {
            informationSource = new ArrayList<InformationSource>();
        }
        return this.informationSource;
    }

    /**
     * Gets the value of the pricingModel property.
     * 
     * @return
     *     possible object is
     *     {@link PricingModel }
     *     
     */
    public PricingModel getPricingModel() {
        return pricingModel;
    }

    /**
     * Sets the value of the pricingModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link PricingModel }
     *     
     */
    public void setPricingModel(PricingModel value) {
        this.pricingModel = value;
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
     * Gets the value of the valuationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValuationDate() {
        return valuationDate;
    }

    /**
     * Sets the value of the valuationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValuationDate(XMLGregorianCalendar value) {
        this.valuationDate = value;
    }

    /**
     * Gets the value of the expiryTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryTime() {
        return expiryTime;
    }

    /**
     * Sets the value of the expiryTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryTime(XMLGregorianCalendar value) {
        this.expiryTime = value;
    }

    /**
     * Gets the value of the cashflowType property.
     * 
     * @return
     *     possible object is
     *     {@link CashflowType }
     *     
     */
    public CashflowType getCashflowType() {
        return cashflowType;
    }

    /**
     * Sets the value of the cashflowType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CashflowType }
     *     
     */
    public void setCashflowType(CashflowType value) {
        this.cashflowType = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
