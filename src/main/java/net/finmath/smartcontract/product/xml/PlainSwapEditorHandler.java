package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.JAXBException;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.ForwardCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurveWithFixings;
import net.finmath.modelling.descriptor.ScheduleDescriptor;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.*;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class that handles incoming requests for generating and interacting with plain swaps descriptors coming from the editor.
 * The API exposed by this class is not definitive and may be subject to changes without notice.
 *
 * @author Luca Bressan
 * @version alpha.1
 */
@SuppressWarnings("java:S125")
public final class PlainSwapEditorHandler {

	private static final Logger logger = LoggerFactory.getLogger(PlainSwapEditorHandler.class);
	private static final String CONSTANT = "constant";
	private static final String FAILED_MODEL_CALIBRATION = "Failed to calibrate model.";
	public static final String DEFAULT_FIXED_PERIOD = "Y";
	public static final String DEFAULT_FLOATING_PERIOD = "M";
	public static final BigInteger DEFAULT_FIXED_PERIOD_MULTIPLIER = BigInteger.ONE;
	public static final BigInteger DEFAULT_FLOATING_PERIOD_MULTIPLIER = BigInteger.valueOf(6);
	private final Smartderivativecontract smartDerivativeContract;
	private final Schema sdcmlSchema;
	private final InterestRateStream floatingLeg;
	private final InterestRateStream fixedLeg;

	/**
	 * Returns the plain swap editor handler.
	 *
	 * @param plainSwapOperationRequest the JSON request that contains the editor info.
	 * @param templateXml              path for the SDCmL template XML to be used
	 * @param schemaPath                path for the SDCmL validation schema
	 * @throws IOException                    when loading settings files fails.
	 * @throws SAXException                   when validation of the generated contract fails.
	 * @throws JAXBException                  when marshalling/unmarshalling of a SDCmL object/file fails.
	 * @throws DatatypeConfigurationException when conversion of dates to the FPmL specifications fails.
	 */
	public PlainSwapEditorHandler(final PlainSwapOperationRequest plainSwapOperationRequest, String templateXml, String schemaPath, String projectVersion) throws IOException, SAXException, JAXBException, DatatypeConfigurationException {
		try {
			final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			sdcmlSchema = schemaFactory.newSchema((new ClassPathResource(schemaPath)).getURL());
		} catch (IOException | SAXException e) {
			logger.error("Failed to recover XSD schema. The file '{}' is missing, unreachable or invalid.", schemaPath);

			throw e;
		}

		// create new SDCmL file as object
		smartDerivativeContract = new Smartderivativecontract();
		smartDerivativeContract.setDltTradeId("tbd");
		smartDerivativeContract.setDltAddress("0x000001");
		String uti = plainSwapOperationRequest.getUniqueTradeIdentifier() != null ? plainSwapOperationRequest.getUniqueTradeIdentifier() : "tbd";
		smartDerivativeContract.setUniqueTradeIdentifier(uti);
		smartDerivativeContract.setSettlementCurrency(plainSwapOperationRequest.getCurrency());
		smartDerivativeContract.setTradeType(plainSwapOperationRequest.getTradeType());
		final Smartderivativecontract templateContract;

		//FileSystemResource templateXmlResource = new FileSystemResource(templatePath);

		//String templateXmlString = new String(templateXmlResource.getInputStream().readAllBytes());
		templateContract = SDCXMLParser.unmarshalXml(templateXml, Smartderivativecontract.class);

		// set the SDC specific stuff in the helper methods
		setSdcValuationHeader(smartDerivativeContract, projectVersion);
		setSdcPartiesHeader(plainSwapOperationRequest, smartDerivativeContract);
		if(plainSwapOperationRequest.getValuationSymbols() != null && !plainSwapOperationRequest.getValuationSymbols().isEmpty()) {
			setSdcSettlementHeaderProvidedSymbols(plainSwapOperationRequest, smartDerivativeContract);
		} else {
			setSdcSettlementHeaderTemplate(plainSwapOperationRequest, smartDerivativeContract, templateContract);
		}

		// clone the template
		smartDerivativeContract.setUnderlyings(templateContract.getUnderlyings());

		final Trade trade = smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0);
		final XMLGregorianCalendar formattedTradeDate;
		try {
			formattedTradeDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(plainSwapOperationRequest.getTradeDate().toZonedDateTime()));
		} catch (DatatypeConfigurationException e) {
			logger.error("Failed to convert OffsetDateTime to XMLGregorianCalendar. This occurred while processing field tradeDate");

			throw e;
		}
		formattedTradeDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		trade.tradeHeader.tradeDate.setValue(formattedTradeDate);
		trade.tradeHeader.partyTradeIdentifier.get(0).tradeId = new TradeId();
		trade.tradeHeader.partyTradeIdentifier.get(0).tradeId.setValue("123456");
		trade.tradeHeader.partyTradeIdentifier.get(0).tradeId.setTradeIdScheme("test");
		trade.tradeHeader.partyTradeIdentifier.get(1).tradeId = new TradeId();
		trade.tradeHeader.partyTradeIdentifier.get(1).tradeId.setValue("123456");
		trade.tradeHeader.partyTradeIdentifier.get(1).tradeId.setTradeIdScheme("test");

		Swap swap = ((Swap) smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0).getProduct().getValue());

		Optional<InterestRateStream> fixedLegOptional = swap.swapStream.stream().filter(PlainSwapEditorHandler::isFixedLeg).findFirst();
		if (fixedLegOptional.isEmpty())
			throw new IllegalStateException("The template has issues: failed to find valid candidate for fixed leg swapStream definition. I will fail now, sorry! :(");
		fixedLeg = fixedLegOptional.get();

		Optional<InterestRateStream> floatingLegOptional = swap.swapStream.stream().filter(PlainSwapEditorHandler::isFloatingLeg).findFirst();
		if (floatingLegOptional.isEmpty())
			throw new IllegalStateException("The template has issues: failed to find valid candidate for floating leg swapStream definition. I will fail now, sorry! :(");
		floatingLeg = floatingLegOptional.get();

		// for each swap stream... (index is the order of appearance in the template)
		final XMLGregorianCalendar formattedEffectiveDate;
		try {
			formattedEffectiveDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(plainSwapOperationRequest.getEffectiveDate().toZonedDateTime()));
		} catch (DatatypeConfigurationException e) {
			logger.error("Failed to convert ZonedDateTime to XMLGregorianCalendar. This occurred while processing field effectiveDate");

			throw e;
		}
		formattedEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

		final XMLGregorianCalendar formattedTerminationDate;
		try {
			formattedTerminationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(plainSwapOperationRequest.getTerminationDate().toZonedDateTime()));
		} catch (DatatypeConfigurationException e) {
			logger.error("Failed to convert ZonedDateTime to XMLGregorianCalendar. This occurred while processing field terminationDate");

			throw e;
		}
		formattedTerminationDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		for (InterestRateStream swapLeg : swap.swapStream) {

			swapLeg.calculationPeriodDates.effectiveDate.unadjustedDate.setValue(formattedEffectiveDate);
			swapLeg.calculationPeriodDates.terminationDate.unadjustedDate.setValue(formattedTerminationDate);


			swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.initialValue = BigDecimal.valueOf(plainSwapOperationRequest.getNotionalAmount());
			swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.currency.value = plainSwapOperationRequest.getCurrency();

		}

		logger.info("reading fixedPayerPartyID: {}", plainSwapOperationRequest.getFixPayerPartyID());
		//TODO clarify if correct and how to improve
		if (plainSwapOperationRequest.getFixPayerPartyID().equals(smartDerivativeContract.parties.party.get(1).getId())) {
			floatingLeg.payerPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
			floatingLeg.receiverPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
			fixedLeg.payerPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
			fixedLeg.receiverPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
		} else {
			floatingLeg.payerPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
			floatingLeg.receiverPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
			fixedLeg.payerPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
			fixedLeg.receiverPartyReference.href = smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
		}

		if(plainSwapOperationRequest.getFloatingFixingDayOffset() != null){
			logger.info("floatingFixingDayOffSet manually set to: '{}'", plainSwapOperationRequest.getFloatingFixingDayOffset());
			floatingLeg.resetDates.fixingDates.periodMultiplier = BigInteger.valueOf(plainSwapOperationRequest.getFloatingFixingDayOffset().longValue());
		}
		logger.info("Reading back floating fixing date offset: {}", floatingLeg.resetDates.fixingDates.periodMultiplier);
		if(plainSwapOperationRequest.getFloatingDayCountFraction() != null && !plainSwapOperationRequest.getFloatingDayCountFraction().isEmpty()) {
			logger.info("floatingDayCountFraction manually set to: '{}'", plainSwapOperationRequest.getFloatingDayCountFraction());
			floatingLeg.calculationPeriodAmount.calculation.dayCountFraction.value = plainSwapOperationRequest.getFloatingDayCountFraction();
		}
		logger.info("Reading back floating day count fraction: {}", floatingLeg.calculationPeriodAmount.calculation.dayCountFraction.value);

		if(plainSwapOperationRequest.getFloatingRateIndex() != null && !plainSwapOperationRequest.getFloatingRateIndex().isEmpty()) {
			logger.info("floatingRateIndex manually set to: '{}'", plainSwapOperationRequest.getFloatingRateIndex());
			((FloatingRateCalculation) floatingLeg.calculationPeriodAmount.calculation.getRateCalculation().getValue()).floatingRateIndex.value = plainSwapOperationRequest.getFloatingRateIndex();
		}
		logger.info("Reading back floating rate index: {}", ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount.calculation.getRateCalculation().getValue()).floatingRateIndex.value);
		if(plainSwapOperationRequest.getFixedDayCountFraction() != null && !plainSwapOperationRequest.getFixedDayCountFraction().isEmpty()) {
			logger.info("fixedDayCountFraction manually set to: '{}'", plainSwapOperationRequest.getFixedDayCountFraction());
			fixedLeg.calculationPeriodAmount.calculation.dayCountFraction.value = plainSwapOperationRequest.getFixedDayCountFraction();
		}
		logger.info("Reading back fixed day count fraction {}", fixedLeg.calculationPeriodAmount.calculation.dayCountFraction.value);
		fixedLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue = BigDecimal.valueOf(plainSwapOperationRequest.getFixedRate()).setScale(12, RoundingMode.HALF_EVEN).divide(BigDecimal.valueOf(100L).setScale(12, RoundingMode.HALF_EVEN), RoundingMode.HALF_EVEN);
		logger.info("Reading back fixed rate: {}", fixedLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue);

		setFloatingPaymentFrequency(plainSwapOperationRequest.getFloatingPaymentFrequency());
		setFixedPaymentFrequency(plainSwapOperationRequest.getFixedPaymentFrequency());


		smartDerivativeContract.receiverPartyID = plainSwapOperationRequest.getReceiverPartyID();

		logger.info("Instance built!");

	}

	private void setFixedPaymentFrequency(PaymentFrequency paymentFrequency) {

		BigInteger periodMultiplier;
		String period;
		if(paymentFrequency != null) {
			periodMultiplier = BigInteger.valueOf(paymentFrequency.getPeriodMultiplier().longValue());
			period = paymentFrequency.getPeriod();
		} else {
			periodMultiplier = DEFAULT_FIXED_PERIOD_MULTIPLIER;
			period = DEFAULT_FIXED_PERIOD;
		}

		fixedLeg.paymentDates.paymentFrequency.setPeriodMultiplier(periodMultiplier);
		logger.info("Reading back fixed period multiplier: {}", periodMultiplier);
		fixedLeg.paymentDates.paymentFrequency.setPeriod(period);
		logger.info("Reading back fixed period: {}", period);
		fixedLeg.calculationPeriodDates.calculationPeriodFrequency.setPeriodMultiplier(periodMultiplier);
		fixedLeg.calculationPeriodDates.calculationPeriodFrequency.setPeriod(period);
	}

	private void setFloatingPaymentFrequency(PaymentFrequency paymentFrequency) {
		BigInteger periodMultiplier;
		String period;
		if(paymentFrequency != null) {
			periodMultiplier = BigInteger.valueOf(paymentFrequency.getPeriodMultiplier().longValue());
			period = paymentFrequency.getPeriod();
		} else {
			periodMultiplier = DEFAULT_FLOATING_PERIOD_MULTIPLIER;
			period = DEFAULT_FLOATING_PERIOD;
		}

		floatingLeg.paymentDates.paymentFrequency.setPeriodMultiplier(periodMultiplier);
		logger.info("Reading back floating period multiplier: {}", periodMultiplier);
		floatingLeg.paymentDates.paymentFrequency.setPeriod(period);
		logger.info("Reading back floating period: {}", period);

		//TODO: ask people who know more about FPmL if the next lines are actually needed

		floatingLeg.calculationPeriodDates.calculationPeriodFrequency.setPeriodMultiplier(periodMultiplier);
		floatingLeg.calculationPeriodDates.calculationPeriodFrequency.setPeriod(period);

		//TODO clarify why it was hardcoded to EOM, now get the value from the template fpml
		//floatingLeg.calculationPeriodDates.calculationPeriodFrequency.setRollConvention("EOM");
		floatingLeg.resetDates.resetFrequency.setPeriodMultiplier(periodMultiplier);
		floatingLeg.resetDates.resetFrequency.setPeriod(period);
		((FloatingRateCalculation) floatingLeg.calculationPeriodAmount.calculation.getRateCalculation().getValue()).indexTenor.periodMultiplier = periodMultiplier;
		((FloatingRateCalculation) floatingLeg.calculationPeriodAmount.calculation.getRateCalculation().getValue()).indexTenor.period = PeriodEnum.valueOf(period);
		// end of dubious lines
	}

	private static boolean isFloatingLeg(InterestRateStream swapStream) {
		return swapStream.getCalculationPeriodAmount().getCalculation().getRateCalculation().getDeclaredType().equals(FloatingRateCalculation.class) && Objects.isNull(swapStream.getCalculationPeriodAmount().getCalculation().getFixedRateSchedule());
	}

	private static boolean isFixedLeg(InterestRateStream swapStream) {
		return !Objects.isNull(swapStream.getCalculationPeriodAmount().getCalculation().getFixedRateSchedule()) && Objects.isNull(swapStream.getCalculationPeriodAmount().getCalculation().getRateCalculation());
	}

	private static void setSdcSettlementHeaderProvidedSymbols(final PlainSwapOperationRequest plainSwapOperationRequest, final Smartderivativecontract sdc) {
		Smartderivativecontract.Settlement settlementHeader = new Smartderivativecontract.Settlement();

		settlementHeader.settlementTime = new Smartderivativecontract.Settlement.SettlementTime();
		settlementHeader.settlementTime.value = plainSwapOperationRequest.getDailySettlementTime(); //taken from the template
		settlementHeader.settlementTime.type = "daily";
		settlementHeader.marketdata = new Smartderivativecontract.Settlement.Marketdata();
		settlementHeader.marketdata.provider = "refinitiv";
		Smartderivativecontract.Settlement.Marketdata.Marketdataitems marketDataItems = new Smartderivativecontract.Settlement.Marketdata.Marketdataitems();
		for (FrontendItemSpec marketDataItem : plainSwapOperationRequest.getValuationSymbols()) {
			Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item newItem = new Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item();
			newItem.curve = new ArrayList<>();
			newItem.symbol = new ArrayList<>();
			newItem.type = new ArrayList<>();
			newItem.tenor = new ArrayList<>();
			newItem.curve.add(marketDataItem.getCurve());
			newItem.symbol.add(marketDataItem.getSymbol());
			newItem.type.add(marketDataItem.getItemType());
			newItem.tenor.add(marketDataItem.getTenor());
			marketDataItems.getItem().add(newItem);
		}
		settlementHeader.marketdata.marketdataitems = marketDataItems;
		sdc.setSettlement(settlementHeader);
	}

	private static void setSdcSettlementHeaderTemplate(final PlainSwapOperationRequest plainSwapOperationRequest, final Smartderivativecontract sdc, Smartderivativecontract templateContract) {
		logger.info("setSdcSettlementHeaderTemplate with template");

		Smartderivativecontract.Settlement settlementHeader = new Smartderivativecontract.Settlement();

		settlementHeader.settlementTime = new Smartderivativecontract.Settlement.SettlementTime();
		settlementHeader.settlementTime.value = plainSwapOperationRequest.getDailySettlementTime(); //taken from the template
		settlementHeader.settlementTime.type = "daily";
		settlementHeader.marketdata = templateContract.getSettlement().getMarketdata();
		sdc.setSettlement(settlementHeader);
	}

	private static void setSdcPartiesHeader(final PlainSwapOperationRequest tradeDescriptor, final Smartderivativecontract smartDerivativeContract) {
		logger.info("Setting SDC header of response.");
		Smartderivativecontract.Parties parties = new Smartderivativecontract.Parties();
		List<Smartderivativecontract.Parties.Party> partyList = new ArrayList<>();

		Smartderivativecontract.Parties.Party party1 = new Smartderivativecontract.Parties.Party();
		logger.info("Setting id party1 for party {}", tradeDescriptor.getFirstCounterparty());
		party1.setName(tradeDescriptor.getFirstCounterparty().getFullName());
		party1.setId("party1");
		Smartderivativecontract.Parties.Party.MarginAccount marginAccount1 = new Smartderivativecontract.Parties.Party.MarginAccount();
		marginAccount1.setType(CONSTANT);
		marginAccount1.setValue(tradeDescriptor.getMarginBufferAmount().floatValue());
		Smartderivativecontract.Parties.Party.PenaltyFee penaltyFee1 = new Smartderivativecontract.Parties.Party.PenaltyFee();
		penaltyFee1.setType(CONSTANT);
		penaltyFee1.setValue(tradeDescriptor.getTerminationFeeAmount().floatValue());
		party1.setAddress(tradeDescriptor.getFirstCounterparty().getDltAddress());

		logger.info("Setting id party2 for party {}", tradeDescriptor.getSecondCounterparty());
		Smartderivativecontract.Parties.Party party2 = new Smartderivativecontract.Parties.Party();
		party2.setName(tradeDescriptor.getSecondCounterparty().getFullName());
		party2.setId("party2");
		Smartderivativecontract.Parties.Party.MarginAccount marginAccount2 = new Smartderivativecontract.Parties.Party.MarginAccount();
		marginAccount2.setType(CONSTANT);
		marginAccount2.setValue(tradeDescriptor.getMarginBufferAmount().floatValue());
		Smartderivativecontract.Parties.Party.PenaltyFee penaltyFee2 = new Smartderivativecontract.Parties.Party.PenaltyFee();
		penaltyFee2.setType(CONSTANT);
		penaltyFee2.setValue(tradeDescriptor.getTerminationFeeAmount().floatValue());
		party2.setAddress(tradeDescriptor.getSecondCounterparty().getDltAddress());

		party1.setMarginAccount(marginAccount1);
		party1.setPenaltyFee(penaltyFee1);
		party2.setMarginAccount(marginAccount2);
		party2.setPenaltyFee(penaltyFee2);

		partyList.add(party1);
		partyList.add(party2);
		parties.party = partyList;

		smartDerivativeContract.setParties(parties);
	}

	private static void setSdcValuationHeader(final Smartderivativecontract smartDerivativeContract, String projectVersion) {
		Smartderivativecontract.Valuation valuationHeader = new Smartderivativecontract.Valuation();
		Smartderivativecontract.Valuation.Artefact artifactHeader = new Smartderivativecontract.Valuation.Artefact();
		artifactHeader.setGroupId("net.finmath");
		artifactHeader.setArtifactId("finmath-smart-derivative-contract");
		artifactHeader.setVersion(projectVersion);
		valuationHeader.setArtefact(artifactHeader);
		smartDerivativeContract.setValuation(valuationHeader);
	}

	/**
	 * Returns the SDCmL string associated with this plain swap handler.
	 *
	 * @return the SDCmL document
	 */
	public String getContractAsXmlString() {
		String xmlString = SDCXMLParser.marshalSDCToXMLString(smartDerivativeContract);
		try {
			Validator validator = sdcmlSchema.newValidator();
			validator.validate(new StreamSource(new ByteArrayInputStream(xmlString.getBytes())));
			logger.info("Validation successful!");

			return xmlString;
		} catch (SAXException | IOException e) {
			logger.error("error in validation", e);
			throw new SDCException(ExceptionId.SDC_JAXB_ERROR, "SDC product XML structure is not validated", 400);
		}
	}

	/**
	 * Getter method for the JAXB representation of the contract
	 *
	 * @return the contract object
	 */
	public Smartderivativecontract getContract() {
		return this.smartDerivativeContract;

	}

	/**
	 * Returns a list of cashflow periods representing the payment streams involved in the plain swap described.
	 *
	 * @param legSelector the leg for which the schedule should be calculated.
	 * @param marketData  the market data used for calibration of the model used for the payments' calculation.
	 * @return the payment schedule.
	 */
	public List<CashflowPeriod> getSchedule(LegSelector legSelector, String marketData) throws IOException, CloneNotSupportedException {
		InterestRateStream swapLeg;
		switch (legSelector) {
			case FIXED_LEG -> {
				swapLeg = fixedLeg;
				logger.info("Fixed leg detected.");
			}
			case FLOATING_LEG -> {
				swapLeg = floatingLeg;
				logger.info("Floating leg detected.");
			}
			default -> throw new IllegalArgumentException("Failed to detect leg type");
		}
		final LocalDate startDate = swapLeg.getCalculationPeriodDates().getEffectiveDate().getUnadjustedDate().getValue().toGregorianCalendar().toZonedDateTime().toLocalDate();
		logger.info("Start date detected: {}", startDate);
		final LocalDate maturityDate = swapLeg.getCalculationPeriodDates().getTerminationDate().getUnadjustedDate().getValue().toGregorianCalendar().toZonedDateTime().toLocalDate();
		logger.info("Maturity date detected: {}", maturityDate);
		int fixingOffsetDays = 0;
		try {
			fixingOffsetDays = swapLeg.getResetDates().getFixingDates().getPeriodMultiplier().intValue();
		} catch (NullPointerException npe) {
			logger.warn("No fixing offset was detected, 0 implied.");

		}
		int paymentOffsetDays = 0;
		try {
			paymentOffsetDays = swapLeg.getPaymentDates().getPaymentDaysOffset().getPeriodMultiplier().intValue();
		} catch (NullPointerException npe) {
			logger.warn("No payment offset was detected, 0 implied.");

		}


		final BusinessdayCalendar.DateRollConvention dateRollConvention;
		switch (swapLeg.getPaymentDates().getPaymentDatesAdjustments().getBusinessDayConvention()) {
			case PRECEDING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.PRECEDING;
			case MODPRECEDING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.MODIFIED_PRECEDING;
			case FOLLOWING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.FOLLOWING;
			case MODFOLLOWING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.MODIFIED_FOLLOWING;
			case NONE -> dateRollConvention = BusinessdayCalendar.DateRollConvention.UNADJUSTED;
			default -> throw new IllegalArgumentException("Unrecognized date roll convention: " + swapLeg.getPaymentDates().getPaymentDatesAdjustments().getBusinessDayConvention());
		}

		logger.info("Date roll convention detected: {}", dateRollConvention);

		final ScheduleGenerator.DaycountConvention daycountConvention = ScheduleGenerator.DaycountConvention.getEnum(swapLeg.getCalculationPeriodAmount().getCalculation().getDayCountFraction().getValue());
		ScheduleGenerator.Frequency frequency = null;
		final int multiplier = swapLeg.getPaymentDates().getPaymentFrequency().getPeriodMultiplier().intValue();

		logger.info("Reading period symbol: {}", swapLeg.getPaymentDates().getPaymentFrequency().getPeriod());
		switch (swapLeg.getPaymentDates().getPaymentFrequency().getPeriod()) {
			case "D" -> {
				if (multiplier == 1) {
					frequency = ScheduleGenerator.Frequency.DAILY;
				}
			}
			case "Y" -> {
				if (multiplier == 1) {
					frequency = ScheduleGenerator.Frequency.ANNUAL;
				}
			}
			case "M" -> frequency = switch (multiplier) {
				case 1 -> ScheduleGenerator.Frequency.MONTHLY;
				case 3 -> ScheduleGenerator.Frequency.QUARTERLY;
				case 6 -> ScheduleGenerator.Frequency.SEMIANNUAL;
				default -> throw new IllegalArgumentException("Unknown periodMultiplier " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriodMultiplier().intValue() + ".");
			};
			default -> throw new IllegalArgumentException("Unknown period " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriod() + ".");
		}

		//build schedule
		logger.info("Payment frequency detected: {}", Objects.requireNonNull(frequency));
		final ScheduleDescriptor scheduleDescriptor = new ScheduleDescriptor(startDate, maturityDate, frequency, daycountConvention, ScheduleGenerator.ShortPeriodConvention.LAST, dateRollConvention, new BusinessdayCalendarExcludingTARGETHolidays(), fixingOffsetDays, paymentOffsetDays);


		List<CashflowPeriod> cashflowPeriods = new ArrayList<>();
		Schedule schedule = scheduleDescriptor.getSchedule(this.smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0).tradeHeader.tradeDate.value.toGregorianCalendar().toZonedDateTime().toLocalDate());
		double notional = swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.initialValue.doubleValue();

		/* Check the product */
		String forwardCurveID = "forward-EUR-6M";
		String discountCurveID = "discount-EUR-OIS";
		AnalyticModel calibratedModel = getAnalyticModel(marketData, schedule, forwardCurveID, discountCurveID);

		String currency = swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.currency.value;
		int i = 0;
		for (Period schedulePeriod : schedule) {
			double rate = legSelector.equals(LegSelector.FIXED_LEG) ? swapLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue.doubleValue() : calibratedModel.getForwardCurve(forwardCurveID).getForward(calibratedModel, schedule.getFixing(i));
			double homePartyIsPayerPartyFactor = ((Party) swapLeg.payerPartyReference.href).id.equals(this.smartDerivativeContract.receiverPartyID) ? 1.0 : -1.0;

			cashflowPeriods.add(new CashflowPeriod().cashflow(new ValueResult().currency(currency).valuationDate(new Date().toString()).value(BigDecimal.valueOf(homePartyIsPayerPartyFactor * schedule.getPeriodLength(i) * notional * rate))).fixingDate(OffsetDateTime.of(schedulePeriod.getFixing(), LocalTime.NOON, ZoneOffset.UTC)).paymentDate(OffsetDateTime.of(schedulePeriod.getPayment(), LocalTime.NOON, ZoneOffset.UTC)).periodStart(OffsetDateTime.of(schedulePeriod.getPeriodStart(), LocalTime.NOON, ZoneOffset.UTC)).periodEnd(OffsetDateTime.of(schedulePeriod.getPeriodEnd(), LocalTime.NOON, ZoneOffset.UTC)).rate(rate));

			i++;
		}
		return cashflowPeriods;
	}

	/**
	 * Returns a list of cashflow periods representing the payment streams involved in the plain swap described.
	 *
	 * @param legSelector the leg for which the schedule should be calculated.
	 * @param marketData  the market data used for calibration of the model used for the payments' calculation.
	 * @return the payment schedule.
	 */
	public List<CashflowPeriod> getSchedule(LegSelector legSelector, MarketDataSet marketData) throws IOException, CloneNotSupportedException {
		InterestRateStream swapLeg;
		switch (legSelector) {
			case FIXED_LEG -> {
				swapLeg = fixedLeg;
				logger.info("Fixed leg detected.");
			}
			case FLOATING_LEG -> {
				swapLeg = floatingLeg;
				logger.info("Floating leg detected.");
			}
			default -> throw new IllegalArgumentException("Failed to detect leg type");
		}
		final LocalDate startDate = swapLeg.getCalculationPeriodDates().getEffectiveDate().getUnadjustedDate().getValue().toGregorianCalendar().toZonedDateTime().toLocalDate();
		logger.info("Start date detected: {}", startDate);
		final LocalDate maturityDate = swapLeg.getCalculationPeriodDates().getTerminationDate().getUnadjustedDate().getValue().toGregorianCalendar().toZonedDateTime().toLocalDate();
		logger.info("Maturity date detected: {}", maturityDate);
		int fixingOffsetDays = 0;
		try {
			fixingOffsetDays = swapLeg.getResetDates().getFixingDates().getPeriodMultiplier().intValue();
		} catch (NullPointerException npe) {
			logger.warn("No fixing offset was detected, 0 implied.");

		}
		int paymentOffsetDays = 0;
		try {
			paymentOffsetDays = swapLeg.getPaymentDates().getPaymentDaysOffset().getPeriodMultiplier().intValue();
		} catch (NullPointerException npe) {
			logger.warn("No payment offset was detected, 0 implied.");

		}


		final BusinessdayCalendar.DateRollConvention dateRollConvention;
		switch (swapLeg.getPaymentDates().getPaymentDatesAdjustments().getBusinessDayConvention()) {
			case PRECEDING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.PRECEDING;
			case MODPRECEDING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.MODIFIED_PRECEDING;
			case FOLLOWING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.FOLLOWING;
			case MODFOLLOWING -> dateRollConvention = BusinessdayCalendar.DateRollConvention.MODIFIED_FOLLOWING;
			case NONE -> dateRollConvention = BusinessdayCalendar.DateRollConvention.UNADJUSTED;
			default -> throw new IllegalArgumentException("Unrecognized date roll convention: " + swapLeg.getPaymentDates().getPaymentDatesAdjustments().getBusinessDayConvention());
		}

		logger.info("Date roll convention detected: {}", dateRollConvention);

		final ScheduleGenerator.DaycountConvention daycountConvention = ScheduleGenerator.DaycountConvention.getEnum(swapLeg.getCalculationPeriodAmount().getCalculation().getDayCountFraction().getValue());
		ScheduleGenerator.Frequency frequency = null;
		final int multiplier = swapLeg.getPaymentDates().getPaymentFrequency().getPeriodMultiplier().intValue();

		logger.info("Reading period symbol: {}", swapLeg.getPaymentDates().getPaymentFrequency().getPeriod());
		switch (swapLeg.getPaymentDates().getPaymentFrequency().getPeriod()) {
			case "D" -> {
				if (multiplier == 1) {
					frequency = ScheduleGenerator.Frequency.DAILY;
				}
			}
			case "Y" -> {
				if (multiplier == 1) {
					frequency = ScheduleGenerator.Frequency.ANNUAL;
				}
			}
			case "M" -> frequency = switch (multiplier) {
				case 1 -> ScheduleGenerator.Frequency.MONTHLY;
				case 3 -> ScheduleGenerator.Frequency.QUARTERLY;
				case 6 -> ScheduleGenerator.Frequency.SEMIANNUAL;
				default -> throw new IllegalArgumentException("Unknown periodMultiplier " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriodMultiplier().intValue() + ".");
			};
			default -> throw new IllegalArgumentException("Unknown period " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriod() + ".");
		}

		//build schedule
		logger.info("Payment frequency detected: {}", Objects.requireNonNull(frequency));
		final ScheduleDescriptor scheduleDescriptor = new ScheduleDescriptor(startDate, maturityDate, frequency, daycountConvention, ScheduleGenerator.ShortPeriodConvention.LAST, dateRollConvention, new BusinessdayCalendarExcludingTARGETHolidays(), fixingOffsetDays, paymentOffsetDays);


		List<CashflowPeriod> cashflowPeriods = new ArrayList<>();
		Schedule schedule = scheduleDescriptor.getSchedule(this.smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0).tradeHeader.tradeDate.value.toGregorianCalendar().toZonedDateTime().toLocalDate());

		double notional = swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.initialValue.doubleValue();

		/* Check the product */
		String forwardCurveID = "forward-EUR-6M";
		String discountCurveID = "discount-EUR-OIS";
		AnalyticModel calibratedModel = getAnalyticModel(marketData, schedule, forwardCurveID, discountCurveID);

		String currency = swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.currency.value;
		double timeDiff = FloatingpointDate.getFloatingPointDateFromDate(
				marketData.getRequestTimestamp().toLocalDate().atStartOfDay(),
				marketData.getRequestTimestamp().toLocalDateTime());
		int i = 0;
		for (Period schedulePeriod : schedule) {
			double rate = legSelector.equals(LegSelector.FIXED_LEG) ? swapLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue.doubleValue() : calibratedModel.getForwardCurve(forwardCurveID).getForward(calibratedModel, schedule.getFixing(i) + timeDiff);

			double homePartyIsPayerPartyFactor = ((Party) swapLeg.payerPartyReference.href).id.equals(this.smartDerivativeContract.receiverPartyID) ? 1.0 : -1.0;

			cashflowPeriods.add(new CashflowPeriod().cashflow(new ValueResult().currency(currency).valuationDate(new Date().toString()).value(BigDecimal.valueOf(homePartyIsPayerPartyFactor * schedule.getPeriodLength(i) * notional * rate))).fixingDate(OffsetDateTime.of(schedulePeriod.getFixing(), LocalTime.NOON, ZoneOffset.UTC)).paymentDate(OffsetDateTime.of(schedulePeriod.getPayment(), LocalTime.NOON, ZoneOffset.UTC)).periodStart(OffsetDateTime.of(schedulePeriod.getPeriodStart(), LocalTime.NOON, ZoneOffset.UTC)).periodEnd(OffsetDateTime.of(schedulePeriod.getPeriodEnd(), LocalTime.NOON, ZoneOffset.UTC)).rate(rate));

			i++;
		}

		return cashflowPeriods;


	}

	private AnalyticModel getAnalyticModel(String marketData, Schedule schedule, String forwardCurveID, String discountCurveID) throws IOException, CloneNotSupportedException {         // TODO: ask Christian or Peter to review this
		List<CalibrationDataset> marketDataSets;
		try {
			marketDataSets = CalibrationParserDataItems.getScenariosFromJsonString(marketData);
		} catch (IllegalArgumentException e) {
			logger.error("Failed to load market data.", e);
			throw e;
		}
		Validate.isTrue(marketDataSets.size() == 1, "Parameter marketData should be only a single market data set");

		LocalDateTime marketDataTime = marketDataSets.get(0).getDate();

		final Optional<CalibrationDataset> optionalScenario = marketDataSets.stream().filter(scenario -> scenario.getDate().equals(marketDataTime)).findAny();
		final CalibrationDataset scenario;
		if (optionalScenario.isPresent()) scenario = optionalScenario.get();
		else throw new IllegalStateException("Failed to load calibration dataset.");

		final LocalDate referenceDate = marketDataTime.toLocalDate();

		final CalibrationParserDataItems parser = new CalibrationParserDataItems();
		final Calibrator calibrator = /*new Calibrator();*/ null;

		final Stream<CalibrationSpecProvider> allCalibrationItems = scenario.getDataAsCalibrationDataPointStream(parser);


		final Optional<CalibrationResult> optionalCalibrationResult;
		try {
			optionalCalibrationResult = calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));
		} catch (CloneNotSupportedException e) {
			logger.error(FAILED_MODEL_CALIBRATION);
			throw e;
		}
		AnalyticModel calibratedModel;
		if (optionalCalibrationResult.isPresent())
			calibratedModel = optionalCalibrationResult.get().getCalibratedModel();
		else throw new IllegalStateException(FAILED_MODEL_CALIBRATION);


		Set<CalibrationDataItem> pastFixings = scenario.getFixingDataItems();

		// @Todo what if we have no past fixing provided
		// @Todo what when we are exactly on the fixing date but before 11:00 am.
		ForwardCurveInterpolation fixedCurve = this.getCurvePastFixings("fixedCurve", referenceDate, calibratedModel, discountCurveID, pastFixings);//ForwardCurveInterpolation.createForwardCurveFromForwards("pastFixingCurve", pastFixingTimeArray, pastFixingArray, paymentOffset);
		Curve forwardCurveWithFixings = new ForwardCurveWithFixings(calibratedModel.getForwardCurve(forwardCurveID), fixedCurve, schedule.getFixing(0), 0.0);
		Curve[] finalCurves = {calibratedModel.getDiscountCurve(discountCurveID), calibratedModel.getForwardCurve(forwardCurveID), forwardCurveWithFixings};
		calibratedModel = new AnalyticModelFromCurvesAndVols(referenceDate, finalCurves);
		return calibratedModel;
	}

	private AnalyticModel getAnalyticModel(MarketDataSet marketData, Schedule schedule, String forwardCurveID, String discountCurveID) throws IOException, CloneNotSupportedException {         // TODO: ask Christian or Peter to review this
		SmartDerivativeContractDescriptor productDescriptor = null;
		try {
			productDescriptor = SDCXMLParser.parse(this.getContractAsXmlString());
		} catch (ParserConfigurationException | SAXException e) {
			throw new SDCException(ExceptionId.SDC_XML_PARSE_ERROR, e.getMessage());
		}

		Set<CalibrationDataItem> cdi = new HashSet<>();


		List<CalibrationDataItem.Spec> mdReferences = productDescriptor.getMarketdataItemList();
		List<MarketDataSetValuesInner> mdValues = marketData.getValues();
		for (CalibrationDataItem.Spec mdr : mdReferences) {
			for (MarketDataSetValuesInner mdv : mdValues) {
				if (mdv.getSymbol().equals(mdr.getKey())) {
					cdi.add(
							new CalibrationDataItem(mdr, mdv.getValue(), mdv.getDataTimestamp().toLocalDateTime())
					);
				}
			}
		}

		List<CalibrationDataset> marketDataSets = new ArrayList<>();
		marketDataSets.add(new CalibrationDataset(cdi, marketData.getRequestTimestamp().toLocalDateTime()));

		LocalDateTime marketDataTime = marketData.getRequestTimestamp().toLocalDateTime();

		final Optional<CalibrationDataset> optionalScenario = marketDataSets.stream().filter(scenario -> scenario.getDate().equals(marketDataTime)).findAny();
		final CalibrationDataset scenario;
		if (optionalScenario.isPresent()) scenario = optionalScenario.get();
		else throw new IllegalStateException("Failed to load calibration dataset.");

		final CalibrationParserDataItems parser = new CalibrationParserDataItems();

		final Stream<CalibrationSpecProvider> allCalibrationItems = scenario.getDataAsCalibrationDataPointStream(parser);
		Calibrator calibrator = new Calibrator(scenario.getDataPoints().stream().filter(
				ci -> ci.getSpec().getProductName().equals("Fixing") || ci.getSpec().getProductName().equals(
						"Deposit")).toList(), new CalibrationContextImpl(marketDataTime, 1E-9));

		final Optional<CalibrationResult> optionalCalibrationResult;

		try {
			optionalCalibrationResult = calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));
		} catch (CloneNotSupportedException e) {
			logger.error(FAILED_MODEL_CALIBRATION);
			throw e;
		}
		AnalyticModel calibratedModel;
		if (optionalCalibrationResult.isPresent())
			calibratedModel = optionalCalibrationResult.get().getCalibratedModel();
		else throw new IllegalStateException(FAILED_MODEL_CALIBRATION);
		return calibratedModel;
	}

	private ForwardCurveInterpolation getCurvePastFixings(final String curveID, LocalDate referenceDate, AnalyticModel model, String discountCurveName, final Set<CalibrationDataItem> pastFixings) {
		Map<Double, Double> fixingMap = new LinkedHashMap<>();
		pastFixings.forEach(item -> fixingMap.put(FloatingpointDate.getFloatingPointDateFromDate(referenceDate, item.getDate()), item.getQuote()));
		double[] pastFixingTimes = fixingMap.keySet().stream().mapToDouble(time -> time).toArray();
		double[] pastFixingsValues = Arrays.stream(pastFixingTimes).map(fixingMap::get).toArray();
		ForwardCurveInterpolation.InterpolationEntityForward interpolationEntityForward = ForwardCurveInterpolation.InterpolationEntityForward.FORWARD;
		return ForwardCurveInterpolation.createForwardCurveFromForwards(curveID, referenceDate, "offsetcode", interpolationEntityForward, discountCurveName, model, pastFixingTimes, pastFixingsValues);
	}

	/**
	 * Enumeration of possible choices for the swap legs.
	 */
	public enum LegSelector {
		FIXED_LEG, FLOATING_LEG
	}

}
