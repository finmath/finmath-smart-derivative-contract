package net.finmath.smartcontract.descriptor.xmlparser;

import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.ScheduleDescriptor;
import net.finmath.modelling.descriptor.xmlparser.XMLParser;
import net.finmath.smartcontract.descriptor.TradeDescriptor;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.ScheduleGenerator.DaycountConvention;
import net.finmath.time.ScheduleGenerator.Frequency;
import net.finmath.time.ScheduleGenerator.ShortPeriodConvention;
import net.finmath.time.businessdaycalendar.AbstractBusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar.DateRollConvention;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Class for parsing trades saved in FpML to product and trade descriptors.
 *
 * @author Christian Fries
 * @author Roland Bachl
 * @author Dietmar Schnabel
 *
 */
public class FPMLParser  implements XMLParser{

	private final String homePartyId;
	private final String discountCurveName;
	private final String forwardCurveName;

	private Document doc;
	private double notional;
	private LocalDate startDate;
	private LocalDate maturityDate;
	private final HashMap productFields = new HashMap();
	private TradeDescriptor tradeDescriptor = null;
	private ProductDescriptor productDescriptor = null;

	private final AbstractBusinessdayCalendar abstractBusinessdayCalendar = new BusinessdayCalendarExcludingTARGETHolidays();
	private final ShortPeriodConvention shortPeriodConvention= ScheduleGenerator.ShortPeriodConvention.LAST;
	
	/**
	 * Construct the parser.
	 *
	 * @param homePartyId Id of the agent doing the valuation.
	 * @param discountCurveName Name of the discount curve to be given to the descriptors.
	 * @param forwardCurveName Name of the forward curve to be given to the descriptors.
	 * @param fpmlString FPML string containing the trade
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	 */
	public FPMLParser(String homePartyId, String discountCurveName, String forwardCurveName, String fpmlString) throws SAXException, IOException, ParserConfigurationException  {
		
		this.homePartyId = homePartyId;
		this.discountCurveName = discountCurveName;
		this.forwardCurveName = forwardCurveName;
		parseFPML(fpmlString);
		getProductDescriptorFromString(fpmlString);
	}
	
	/**
	 * Construct the parser.
	 *
	 * @param homePartyId Id of the agent doing the valuation.
	 * @param discountCurveName Name of the discount curve to be given to the descriptors.
	 * @param forwardCurveName Name of the forward curve to be given to the descriptors.
	 * @param fpmlFile FPML file containing the trade
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	 */
	public FPMLParser(String homePartyId, String discountCurveName, String forwardCurveName, File fpmlFile) throws SAXException, IOException, ParserConfigurationException  {
		
		this.homePartyId = homePartyId;
		this.discountCurveName = discountCurveName;
		this.forwardCurveName = forwardCurveName;
		parseFPML(fpmlFile);
		getProductDescriptorFromFile(fpmlFile);
	}

	public FPMLParser(String homePartyId, String discountCurveName, String forwardCurveName) {
		this.homePartyId = homePartyId;
		this.discountCurveName = discountCurveName;
		this.forwardCurveName = forwardCurveName;
	}

	/**
	 * Parses the FPML file.
	 * @param file FPML file.
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	*/
	private void parseFPML(File file) throws SAXException, IOException, ParserConfigurationException {
		
		this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		this.doc.getDocumentElement().normalize();
	}
	
	/**
	 * Parses the FPML string.
	 *
	 * @param fpmlString FPML string.
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	*/
	private void parseFPML(String fpmlString) throws SAXException, IOException, ParserConfigurationException {
		
		
		InputStream fpmlInputStream = new ByteArrayInputStream(fpmlString.getBytes());
		this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fpmlInputStream);
		this.doc.getDocumentElement().normalize();
	}
	
	/**
	 * Generates a product descriptor from a  FPML file.
	 *
	 * @param file FPML file.
	 * @return ProductDescriptor
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	 * 
	*/
	private ProductDescriptor getProductDescriptorFromFile(File file) throws SAXException, IOException, ParserConfigurationException{
		
		ProductDescriptor productDescriptor = generateProductDescriptor();
		generateTradeDescriptor();
		return productDescriptor; 
	}
	
	/**
	 * Generates a product descriptor from a  FPML string.
	 *
	 * @param fpmlString FPML string.
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	 * @return ProductDescriptor
	*/
	private ProductDescriptor getProductDescriptorFromString(String fpmlString) throws SAXException, IOException, ParserConfigurationException{
		generateTradeDescriptor();
		return generateProductDescriptor();
	}

	/**
	 * Generates a product descriptor from an already existing Document.
	 *
	 * @return ProductDescriptor
	 * @throws IllegalArgumentException Thrown id the document is not an FpML 5 document.
	 */
	public ProductDescriptor getProductDescriptor(Node node)  {


		//Check compatibility and assign proper parser
		if(! node.getNodeName().equalsIgnoreCase("dataDocument")) {
			throw new IllegalArgumentException("This parser is meant for XML of type dataDocument, according to FpML 5, but file is "+doc.getDocumentElement().getNodeName()+".");
		}

		if(! node.getAttributes().getNamedItem("fpmlVersion").getNodeValue().split("-")[0].equals("5")) {
			throw new IllegalArgumentException("This parser is meant for FpML of version 5.*, file is version "+ doc.getDocumentElement().getAttribute("fpmlVersion"));
		}

		//Isolate trade node
		Element trade = null;
		String tradeName = null;

		NodeList tradeWrapper = node.getOwnerDocument().getElementsByTagName("trade").item(0).getChildNodes();
		for(int index = 0; index < tradeWrapper.getLength(); index++) {
			if(tradeWrapper.item(index).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if(tradeWrapper.item(index).getNodeName().equalsIgnoreCase("tradeHeader")) {
				continue;
			}
			trade = (Element) tradeWrapper.item(index);
			tradeName		= trade.getNodeName().toUpperCase();
			break;
		}


		switch (tradeName) {
			case "SWAP" : return getSwapProductDescriptor(trade);
			default: throw new IllegalArgumentException("This FpML parser is not set up to process trades of type "+tradeName+".");
		}

	}

	/**
	 * Generates a product descriptor from an already existing Document.
	 * 
	 * @return ProductDescriptor
	 * @throws IllegalArgumentException Thrown id the document is not an FpML 5 document.
	*/
	private ProductDescriptor generateProductDescriptor()  {

		
		//Check compatibility and assign proper parser
		if(! doc.getDocumentElement().getNodeName().equalsIgnoreCase("dataDocument")) {
			throw new IllegalArgumentException("This parser is meant for XML of type dataDocument, according to FpML 5, but file is "+doc.getDocumentElement().getNodeName()+".");
		}

		if(! doc.getDocumentElement().getAttribute("fpmlVersion").split("-")[0].equals("5")) {
			throw new IllegalArgumentException("This parser is meant for FpML of version 5.*, file is version "+ doc.getDocumentElement().getAttribute("fpmlVersion"));
		}

		//Isolate trade node
		Element trade = null;
		String tradeName = null;

		NodeList tradeWrapper = doc.getElementsByTagName("trade").item(0).getChildNodes();
		for(int index = 0; index < tradeWrapper.getLength(); index++) {
			if(tradeWrapper.item(index).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if(tradeWrapper.item(index).getNodeName().equalsIgnoreCase("tradeHeader")) {
				continue;
			}
			trade = (Element) tradeWrapper.item(index);
			tradeName		= trade.getNodeName().toUpperCase();
			break;
		}


		switch (tradeName) {
		case "SWAP" : return getSwapProductDescriptor(trade);
		default: throw new IllegalArgumentException("This FpML parser is not set up to process trades of type "+tradeName+".");
		}

	}
	
	/**
	 * Returns the TradeDescriptor (transactional details) of a FPML product.
	 * @return TradeDescriptor
	*/
	public TradeDescriptor getTradeDescriptor() {
		return this.tradeDescriptor;
	}
	
	/**
	 * Returns the ProductDescriptor of a FPML product.
	 * @return TradeDescriptor
	*/
	public ProductDescriptor getProductDescriptor() {
		return this.productDescriptor;
	}
	
	/**
	 * Returns the notional of a FPML product.
	 * @return Notional of the swap
	*/
	public double  getNotional() {
		return this.notional;
	}
	
	/**
	 * Returns the start date of the product.
	 * @return Start date of the product
	*/
	public LocalDate  getStartDate() {
		return this.startDate;
	}
	
	/**
	 * Returns the maturity date of the product.
	 * @return Maturity date of the product
	*/
	public LocalDate  getMaturityDate() {
		return this.maturityDate;
	}
	
	/**Generates the trade descriptor with tarnsactional details like counterparty and external references.
	 * 
	 * @throws SAXException Thrown by the xml parser.
	 * @throws IOException Thrown if the file in not found or another IO error occured.
	 * @throws ParserConfigurationException Thrown by the xml parser.
	 */
	private void generateTradeDescriptor() throws SAXException, IOException, ParserConfigurationException {
		
		TradeDescriptor tradeDescriptor = new TradeDescriptor();
		
		String tradeDate = doc.getElementsByTagName("tradeDate").item(0).getTextContent();
		tradeDescriptor.setTradeDate(LocalDate.parse(tradeDate));
		
		
		// TradeId/externalRefernce
		
		NodeList partyTradeIdentifier = doc.getElementsByTagName("partyTradeIdentifier");
		
		String party=null;
		String extRef=null;
		HashMap<String,String> legalEntitiesExternalReferences = new HashMap<>();
		
		for(int j=0;j<2;j++) {
			NodeList childList =  partyTradeIdentifier.item(j).getChildNodes();

			for(int i=0; i < childList.getLength(); i++) {
				Node child = childList.item(i);
				if(child.getNodeName().equals("partyReference")) {
					party = ((Element)child).getAttribute("href");
				} 
				if(child.getNodeName().equals("tradeId")) {
					extRef = child.getTextContent();
				}
				
			}
			legalEntitiesExternalReferences.putIfAbsent(party, extRef);
		}
		
		tradeDescriptor.setLegalEntitiesExternalReferences(legalEntitiesExternalReferences);
		
		// CounterpartyNames
		HashMap<String,HashMap<String,String>> legalEntitiesNames = new HashMap<>();
		NodeList parties = doc.getElementsByTagName("party");
		
		party=null;
		for(int j=0;j<2;j++) {
			HashMap<String,String> idName = new HashMap<>();
			Node nextParty = parties.item(j);

			party = ((Element)nextParty).getAttribute("id");
			NodeList childList = nextParty.getChildNodes();
			for(int i=0; i < childList.getLength(); i++) {
				Node child = childList.item(i);
				if(child.getNodeName().contentEquals("partyId")||child.getNodeName().contentEquals("partyName")) {
					idName.putIfAbsent(child.getNodeName(), child.getTextContent()); 
				}	
			}
			legalEntitiesNames.putIfAbsent(party, idName);
		}
						
		tradeDescriptor.setLegalEntitiesNames(legalEntitiesNames);
		
		this.tradeDescriptor = tradeDescriptor;
		
		return;
		
	}
	
	/**
	 * Construct an InterestRateSwapProductDescriptor from a node in a FpML file.
	 *
	 * @param trade The node containing the swap.
	 * @return Descriptor of the swap.
	 */
	private ProductDescriptor getSwapProductDescriptor(Element trade) {

		InterestRateSwapLegProductDescriptor legReceiver = null;
		InterestRateSwapLegProductDescriptor legPayer = null;

		NodeList legs = trade.getElementsByTagName("swapStream");
		for(int legIndex = 0; legIndex < legs.getLength(); legIndex++) {
			Element leg = (Element) legs.item(legIndex);

			boolean isPayer = leg.getElementsByTagName("payerPartyReference").item(0).getAttributes().getNamedItem("href").getNodeValue().equals(homePartyId);

			/*
			if(isPayer) {
				tradeDescriptor.setLegReceiver("party2");
				legPayer = getSwapLegProductDescriptor(leg);
			} else {
				tradeDescriptor.setLegReceiver("party1");
				legReceiver = getSwapLegProductDescriptor(leg);
			}
			*/
		}
		
		productDescriptor = new InterestRateSwapProductDescriptor(legReceiver, legPayer);
		return productDescriptor;
	}

	/**
	 * Construct an InterestRateSwapLegProductDescriptor from a node in a FpML file.
	 *
	 * @param leg The node containing the leg.
	 * @return Descriptor of the swap leg.
	 */
	private InterestRateSwapLegProductDescriptor getSwapLegProductDescriptor(Element leg) {

		//is this a fixed rate leg?
		boolean isFixed = leg.getElementsByTagName("calculationPeriodDates").item(0).getAttributes().getNamedItem("id").getTextContent().equalsIgnoreCase("fixedCalcPeriodDates");

		//get start and end dates of contract
		LocalDate startDate		= LocalDate.parse(((Element) leg.getElementsByTagName("effectiveDate").item(0)).getElementsByTagName("unadjustedDate").item(0).getTextContent());
		LocalDate maturityDate	= LocalDate.parse(((Element) leg.getElementsByTagName("terminationDate").item(0)).getElementsByTagName("unadjustedDate").item(0).getTextContent());
		this.startDate = startDate;
		this.maturityDate = maturityDate;
		
		//determine fixing/payment offset if available
		int fixingOffsetDays = 0;
		if(leg.getElementsByTagName("fixingDates").getLength() > 0) {
			fixingOffsetDays = Integer.parseInt(((Element) leg.getElementsByTagName("fixingDates").item(0)).getElementsByTagName("periodMultiplier").item(0).getTextContent());
		}
		int paymentOffsetDays = 0;
		if(leg.getElementsByTagName("paymentDaysOffset").getLength() > 0) {
			paymentOffsetDays = Integer.parseInt(((Element) leg.getElementsByTagName("paymentDaysOffset").item(0)).getElementsByTagName("periodMultiplier").item(0).getTextContent());
		}

		//Crop xml date roll convention to match internal format
		String xmlInput = ((Element) leg.getElementsByTagName("calculationPeriodDatesAdjustments").item(0)).getElementsByTagName("businessDayConvention").item(0).getTextContent();
		xmlInput = xmlInput.replaceAll("ING", "");
		DateRollConvention dateRollConvention = DateRollConvention.getEnum(xmlInput);

		//get daycount convention
		DaycountConvention daycountConvention = DaycountConvention.getEnum(leg.getElementsByTagName("dayCountFraction").item(0).getTextContent());

		//get trade frequency
		Frequency frequency = null;
		Element calcNode = (Element) leg.getElementsByTagName("calculationPeriodFrequency").item(0);
		int multiplier = Integer.parseInt(calcNode.getElementsByTagName("periodMultiplier").item(0).getTextContent());
		switch(calcNode.getElementsByTagName("period").item(0).getTextContent().toUpperCase()) {
		case "D" : if(multiplier == 1) {frequency = Frequency.DAILY;} break;
		case "Y" : if(multiplier == 1) {frequency = Frequency.ANNUAL;} break;
		case "M" : switch(multiplier) {
		case 1 : frequency = Frequency.MONTHLY;
		case 3 : frequency = Frequency.QUARTERLY;
		case 6 : frequency = Frequency.SEMIANNUAL;
		}
		}

		//build schedule
		ScheduleDescriptor schedule = new ScheduleDescriptor(startDate, maturityDate, frequency, daycountConvention, shortPeriodConvention,
				dateRollConvention, abstractBusinessdayCalendar, fixingOffsetDays, paymentOffsetDays);

		// get notional
		double _notional = Double.parseDouble(((Element) leg.getElementsByTagName("notionalSchedule").item(0)).getElementsByTagName("initialValue").item(0).getTextContent());
		this.notional =_notional;
		// get fixed rate and forward curve if applicable
		double spread = 0;
		String forwardCurve = "";
		if(isFixed) {
			spread = Double.parseDouble(((Element) leg.getElementsByTagName("fixedRateSchedule").item(0)).getElementsByTagName("initialValue").item(0).getTextContent());
		} else {
		//	forwardCurveName = leg.getElementsByTagName("floatingRateIndex").item(0).getTextContent();
			forwardCurve = this.forwardCurveName;
		}

		// return new InterestRateSwapLegProductDescriptor(forwardCurveName, discountCurveName, schedule, notional, spread, false);
		return  new InterestRateSwapLegProductDescriptor(forwardCurve, discountCurveName, schedule, 1.0, spread, false);
		
		
	}
	
	/*
	* @param file FPML file.
	* @return ProductDescriptor of the swap.
	*/
	@Override
	public ProductDescriptor getProductDescriptor(File file)
			throws SAXException, IOException, ParserConfigurationException {
		// TODO refactor this.
		return null;
	}
}
