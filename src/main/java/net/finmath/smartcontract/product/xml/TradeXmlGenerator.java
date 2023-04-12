package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.*;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.ForwardCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurveWithFixings;
import net.finmath.modelling.descriptor.ScheduleDescriptor;
import net.finmath.smartcontract.marketdata.curvecalibration.*;
import net.finmath.smartcontract.model.CashflowPeriod;
import net.finmath.smartcontract.model.SdcXmlRequest;
import net.finmath.smartcontract.model.ValueResult;
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
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;


public final class TradeXmlGenerator { //TODO: this code needs some cleaning up

    static Logger logger = LoggerFactory.getLogger(TradeXmlGenerator.class);
    private final Smartderivativecontract smartDerivativeContract;
    private final Schema sdcmlSchema;
    private final Marshaller marshaller;
    private final InterestRateStream floatingLeg;
    private final InterestRateStream fixedLeg;

    public TradeXmlGenerator(final SdcXmlRequest sdcXmlRequest) throws IOException, SAXException, JAXBException, DatatypeConfigurationException {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            sdcmlSchema = schemaFactory.newSchema((new ClassPathResource("schemas" + File.separator + "sdc-schemas" + File.separator + "sdcml-contract.xsd")).getURL());
        } catch (IOException | SAXException e) {
            logger.error("Failed to recover XSD schema. The file '" + "schemas" + File.separator + "sdc-schemas" + File.separator + "sdcml-contract.xsd" + "' is missing, unreachable or invalid.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance("net.finmath.smartcontract.product.xml", this.getClass().getClassLoader());
        } catch (JAXBException e) {
            logger.error("Failed to load JAXB context.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            logger.error("Failed to load JAXB marshaller.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        marshaller.setSchema(sdcmlSchema);
        Unmarshaller unmarshaller;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("Failed to load JAXB unmarshaller.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        unmarshaller.setSchema(sdcmlSchema);
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (PropertyException e) {
            logger.error("Failed to configure JAXB marshaller.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }

        logger.info("Accepted incoming request with body:");
        logger.info(sdcXmlRequest.toString());
        // create new SDCmL file as object
        smartDerivativeContract = new Smartderivativecontract();
        Smartderivativecontract templateContract;
        try {
            ClassPathResource templateXmlResource = new ClassPathResource("references/template2.xml");
            templateContract = (Smartderivativecontract) unmarshaller.unmarshal(templateXmlResource.getInputStream());
        } catch (JAXBException e) {
            logger.error("Failed to unmarshall the XML template file.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        } catch (IOException e) {
            logger.error("An IO error occured while unmarshalling the template file..");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }

        // set the SDC specific stuff in the helper methods
        setSdcValuationHeader(smartDerivativeContract);
        setSdcPartiesHeader(sdcXmlRequest, smartDerivativeContract);
        setSdcSettlementHeader(sdcXmlRequest, smartDerivativeContract);

        // set the FPmL body... FPmL is tough!
        // clone the template
        smartDerivativeContract.setUnderlyings(templateContract.getUnderlyings());

        Trade trade = smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0);
        XMLGregorianCalendar formattedTradeDate;
        try {
            formattedTradeDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(
                            GregorianCalendar.from(sdcXmlRequest.getTradeDate().toZonedDateTime())
                    );
        } catch (DatatypeConfigurationException e) {
            logger.error("Failed to convert OffsetDateTime to XMLGregorianCalendar. This occured while processing field tradeDate");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
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

        Swap swap = ((Swap) smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(
                0).getProduct().getValue());

        Optional<InterestRateStream> fixedLegOptional = swap.swapStream.stream().filter(TradeXmlGenerator::isFixedLeg).findFirst();
        if (fixedLegOptional.isEmpty())
            throw new IllegalStateException("The template has issues: failed to find valid candidate for fixed leg swapStream definition. I will fail now, sorry! :(");
        fixedLeg = fixedLegOptional.get();

        Optional<InterestRateStream> floatingLegOptional = swap.swapStream.stream().filter(TradeXmlGenerator::isFloatingLeg).findFirst();
        if (floatingLegOptional.isEmpty())
            throw new IllegalStateException("The template has issues: failed to find valid candidate for floating leg swapStream definition. I will fail now, sorry! :(");
        floatingLeg = floatingLegOptional.get();

        // for each swapstream... (index is the order of appearance in the template)
        XMLGregorianCalendar formattedEffectiveDate;
        try {
            formattedEffectiveDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(
                            GregorianCalendar.from(sdcXmlRequest.getEffectiveDate().toZonedDateTime()
                            )
                    );
        } catch (DatatypeConfigurationException e) {
            logger.error("Failed to convert ZonedDateTime to XMLGregorianCalendar. This occured while processing field effectiveDate");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        formattedEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        XMLGregorianCalendar formattedTerminationDate;
        try {
            formattedTerminationDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(
                            GregorianCalendar.from(sdcXmlRequest.getTerminationDate().toZonedDateTime()
                            )
                    );
        } catch (DatatypeConfigurationException e) {
            logger.error("Failed to convert ZonedDateTime to XMLGregorianCalendar. This occured while processing field terminationDate");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        formattedTerminationDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        for (int i = 0; i < 2; i++) {

            swap.swapStream.get(i).calculationPeriodDates
                    .effectiveDate
                    .unadjustedDate.setValue(formattedEffectiveDate);
            swap.swapStream.get(i).calculationPeriodDates
                    .terminationDate
                    .unadjustedDate.setValue(formattedTerminationDate);


            swap.swapStream.get(
                    i).calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.initialValue =
                    BigDecimal.valueOf(Double.parseDouble(Float.toString(
                            sdcXmlRequest.getNotionalAmount().floatValue())));
            swap.swapStream.get(
                    i).calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.currency.value =
                    sdcXmlRequest.getCurrency();

        }

        if (sdcXmlRequest.getFloatingPayingParty().getFullName().equals(smartDerivativeContract.parties.party.get(0).name)
        ) {
            floatingLeg.payerPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
            floatingLeg.receiverPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
            fixedLeg.payerPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
            fixedLeg.receiverPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
        } else {
            floatingLeg.payerPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
            floatingLeg.receiverPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
            fixedLeg.payerPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(0);
            fixedLeg.receiverPartyReference.href =
                    smartDerivativeContract.underlyings.underlying.dataDocument.party.get(1);
        }


        floatingLeg.resetDates.fixingDates.periodMultiplier =
                BigInteger.valueOf(sdcXmlRequest.getFloatingFixingDayOffset().longValue());
        logger.info("Reading back floating fixing date offset: " + floatingLeg.resetDates.fixingDates.periodMultiplier);
        floatingLeg.calculationPeriodAmount.calculation.dayCountFraction.value =
                sdcXmlRequest.getFloatingDayCountFraction();
        logger.info("Reading back floating day count fraction: " + floatingLeg.calculationPeriodAmount.calculation.dayCountFraction.value);
        floatingLeg.paymentDates.paymentFrequency.periodMultiplier =
                BigInteger.valueOf(sdcXmlRequest.getFloatingPaymentFrequency().getPeriodMultiplier().longValue());
        logger.info("Reading back floating payment frequency period multiplier: " + floatingLeg.paymentDates.paymentFrequency.periodMultiplier);
        floatingLeg.paymentDates.paymentFrequency.setPeriod(sdcXmlRequest.getFloatingPaymentFrequency().getPeriod());
        logger.info("Reading back floating payment frequency period:  " + floatingLeg.paymentDates.paymentFrequency.period);
        ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount
                .calculation.getRateCalculation()
                .getValue()).floatingRateIndex.value =
                sdcXmlRequest.getFloatingRateIndex();
        logger.info("Reading back floating rate index: " + ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount
                .calculation.getRateCalculation()
                .getValue()).floatingRateIndex.value);
        fixedLeg.calculationPeriodAmount.calculation.dayCountFraction.value =
                sdcXmlRequest.getFixedDayCountFraction();
        logger.info("Reading back fixed day count fraction " + fixedLeg.calculationPeriodAmount.calculation.dayCountFraction.value);
        fixedLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue =
                BigDecimal.valueOf(sdcXmlRequest.getFixedRate()).setScale(32, RoundingMode.HALF_EVEN).divide(BigDecimal.valueOf(100L).setScale(5, RoundingMode.HALF_EVEN), RoundingMode.HALF_EVEN);
        logger.info("Reading back notional amount: " + fixedLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue);
        fixedLeg.paymentDates.paymentFrequency.periodMultiplier =
                BigInteger.valueOf(sdcXmlRequest.getFixedPaymentFrequency().getPeriodMultiplier().longValue());
        logger.info("Reading back fixed period multiplier: " + fixedLeg.paymentDates.paymentFrequency.periodMultiplier);
        fixedLeg.paymentDates.paymentFrequency.setPeriod(sdcXmlRequest.getFixedPaymentFrequency().getPeriod());
        logger.info("Reading back fixed period: " + fixedLeg.paymentDates.paymentFrequency.period);

        //TODO: ask people who know more about FPmL if the next lines are actually needed
        fixedLeg.calculationPeriodDates.calculationPeriodFrequency.periodMultiplier =
                BigInteger.valueOf(sdcXmlRequest.getFixedPaymentFrequency().getPeriodMultiplier().longValue());
        fixedLeg.calculationPeriodDates.calculationPeriodFrequency.setPeriod(sdcXmlRequest.getFixedPaymentFrequency().getPeriod());
        floatingLeg.calculationPeriodDates.calculationPeriodFrequency.periodMultiplier =
                BigInteger.valueOf(sdcXmlRequest.getFloatingPaymentFrequency().getPeriodMultiplier().longValue());
        floatingLeg.calculationPeriodDates.calculationPeriodFrequency.setPeriod(sdcXmlRequest.getFloatingPaymentFrequency().getPeriod());
        floatingLeg.calculationPeriodDates.calculationPeriodFrequency.setRollConvention("EOM");
        floatingLeg.resetDates.resetFrequency.periodMultiplier =
                BigInteger.valueOf(sdcXmlRequest.getFloatingPaymentFrequency().getPeriodMultiplier().longValue());
        floatingLeg.resetDates.resetFrequency.period = sdcXmlRequest.getFloatingPaymentFrequency().getPeriod();
        ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount
                .calculation.getRateCalculation()
                .getValue()).indexTenor.periodMultiplier = BigInteger.valueOf(sdcXmlRequest.getFloatingPaymentFrequency().getPeriodMultiplier().longValue());
        ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount
                .calculation.getRateCalculation()
                .getValue()).indexTenor.period = PeriodEnum.valueOf(sdcXmlRequest.getFloatingPaymentFrequency().getPeriod());
        // end of dubious lines


        smartDerivativeContract.settlement.marketdata.marketdataitems = templateContract.getSettlement().getMarketdata().getMarketdataitems();
        smartDerivativeContract.receiverPartyID = "party2";

        logger.info("Instance built!");

    }

    private static boolean isFloatingLeg(InterestRateStream swapStream) {
        return swapStream.getCalculationPeriodAmount().getCalculation().getRateCalculation().getDeclaredType().equals(FloatingRateCalculation.class) && Objects.isNull(swapStream.getCalculationPeriodAmount().getCalculation().getFixedRateSchedule());
    }

    private static boolean isFixedLeg(InterestRateStream swapStream) {
        return !Objects.isNull(swapStream.getCalculationPeriodAmount().getCalculation().getFixedRateSchedule()) && Objects.isNull(swapStream.getCalculationPeriodAmount().getCalculation().getRateCalculation());
    }

    private static void setSdcSettlementHeader(
            final SdcXmlRequest tradeDescriptor,
            final Smartderivativecontract sdc) {
        Smartderivativecontract.Settlement settlementHeader =
                new Smartderivativecontract.Settlement();

        settlementHeader.setSettlementDateInitial(
                tradeDescriptor.getTradeDate().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T12:00:00");
        settlementHeader.settlementTime =
                new Smartderivativecontract.Settlement.SettlementTime();
        settlementHeader.settlementTime.value = "17:00"; //taken from the template
        settlementHeader.settlementTime.type = "daily";
        settlementHeader.marketdata =
                new Smartderivativecontract.Settlement.Marketdata();
        settlementHeader.marketdata.provider = "refinitiv";
        sdc.setSettlement(settlementHeader);
    }

    private static void setSdcPartiesHeader(
            final SdcXmlRequest tradeDescriptor,
            final Smartderivativecontract smartDerivativeContract) {
        logger.info("Setting SDC header of reponse.");
        Smartderivativecontract.Parties parties =
                new Smartderivativecontract.Parties();
        List<Smartderivativecontract.Parties.Party> partyList =
                new ArrayList<>();

        Smartderivativecontract.Parties.Party party1 =
                new Smartderivativecontract.Parties.Party();
        logger.info("Setting id party1 for party " + tradeDescriptor.getFirstCounterparty());
        party1.setName(tradeDescriptor.getFirstCounterparty().getFullName());
        party1.setId("party1");
        Smartderivativecontract.Parties.Party.MarginAccount marginAccount1 =
                new Smartderivativecontract.Parties.Party.MarginAccount();
        marginAccount1.setType("constant");
        marginAccount1.setValue(tradeDescriptor.getMarginBufferAmount().floatValue());
        Smartderivativecontract.Parties.Party.PenaltyFee penaltyFee1 =
                new Smartderivativecontract.Parties.Party.PenaltyFee();
        penaltyFee1.setType("constant");
        penaltyFee1.setValue(tradeDescriptor.getTerminationFeeAmount().floatValue());
        party1.setAddress("0x0");

        logger.info("Setting id party2 for party " + tradeDescriptor.getSecondCounterparty());
        Smartderivativecontract.Parties.Party party2 =
                new Smartderivativecontract.Parties.Party();
        party2.setName(tradeDescriptor.getSecondCounterparty().getFullName());
        party2.setId("party2");
        Smartderivativecontract.Parties.Party.MarginAccount marginAccount2 =
                new Smartderivativecontract.Parties.Party.MarginAccount();
        marginAccount2.setType("constant");
        marginAccount2.setValue(tradeDescriptor.getMarginBufferAmount().floatValue());
        Smartderivativecontract.Parties.Party.PenaltyFee penaltyFee2 =
                new Smartderivativecontract.Parties.Party.PenaltyFee();
        penaltyFee2.setType("constant");
        penaltyFee2.setValue(tradeDescriptor.getTerminationFeeAmount().floatValue());
        party2.setAddress("0x0");

        party1.setMarginAccount(marginAccount1);
        party1.setPenaltyFee(penaltyFee1);
        party2.setMarginAccount(marginAccount2);
        party2.setPenaltyFee(penaltyFee2);

        partyList.add(party1);
        partyList.add(party2);
        parties.party = partyList;

        smartDerivativeContract.setParties(parties);
    }

    private static void setSdcValuationHeader(
            final Smartderivativecontract smartDerivativeContract) {
        Smartderivativecontract.Valuation valuationHeader =
                new Smartderivativecontract.Valuation();
        Smartderivativecontract.Valuation.Artefact artifactHeader =
                new Smartderivativecontract.Valuation.Artefact();
        artifactHeader.setGroupId("net.finmath");
        artifactHeader.setArtifactId("finmath-smart-derivative-contract");
        artifactHeader.setVersion("0.1.8");
        valuationHeader.setArtefact(artifactHeader);
        smartDerivativeContract.setValuation(valuationHeader);
    }

    public String getContractAsXmlString() throws IOException, SAXException, JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(smartDerivativeContract, outputStream);
        } catch (JAXBException e) {
            logger.error("Failed to marshall out the generated XML. Check your inputs.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw e;
        }
        // marshall xml out
        try {
            Validator validator = sdcmlSchema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(outputStream.toByteArray())));
            logger.info("Validation successful!");
            // return outputStream.toString();

            // LOOK AT THIS UGLINESS!!! NOT NICE!!!!!!!
            logger.info("XML was correclty generated, will now do some ugliness.");
            return outputStream.toString()
                    .replaceAll("<fpml:dataDocument fpmlVersion=\"5-9\">", "<dataDocument fpmlVersion=\"5-9\" xmlns=\"http://www.fpml.org/FpML-5/confirmation\">")
                    .replaceAll("fpml:", "");

        } catch (SAXException e) {
            logger.error("Failed to validate the generated XML or some unrecoverable error occurred while validating.");
            logger.error("Details: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.error("Failed to marshall out the generated XML file.");
            logger.error("Details: " + e.getMessage());
            throw e;
        }

    }

    public Smartderivativecontract getContract() {
        return this.smartDerivativeContract;

    }

    public List<CashflowPeriod> getSchedule(LegSelector legSelector, String marketData) {
        InterestRateStream swapLeg;
        switch (legSelector) {
            case FIXED_LEG -> {
                swapLeg = fixedLeg;
                logger.info("Fixed leg detected.");
            }
            case FLOATING_LEG -> {
                swapLeg = floatingLeg;
                logger.info("Floating leg detected.");
                //throw new UnsupportedOperationException("Not yet implemented!");
            }
            default -> throw new IllegalArgumentException("Failed to detect leg type");
        }
        final LocalDate startDate = swapLeg.getCalculationPeriodDates().getEffectiveDate().getUnadjustedDate().getValue().toGregorianCalendar().toZonedDateTime().toLocalDate();
        logger.info("Start date detected: " + startDate.toString());
        final LocalDate maturityDate = swapLeg.getCalculationPeriodDates().getTerminationDate().getUnadjustedDate().getValue().toGregorianCalendar().toZonedDateTime().toLocalDate();
        logger.info("Maturity date detected: " + maturityDate.toString());
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
            default ->
                    throw new IllegalArgumentException("Unrecognized date roll convention: " + swapLeg.getPaymentDates().getPaymentDatesAdjustments().getBusinessDayConvention());
        }

        logger.info("Date roll convention detected: " + dateRollConvention);

        final ScheduleGenerator.DaycountConvention daycountConvention = ScheduleGenerator.DaycountConvention.getEnum(swapLeg.getCalculationPeriodAmount().getCalculation().getDayCountFraction().getValue());
        ScheduleGenerator.Frequency frequency = null;
        final int multiplier = swapLeg.getPaymentDates().getPaymentFrequency().getPeriodMultiplier().intValue();

        logger.info("Reading period symbol: " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriod());
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
                default ->
                        throw new IllegalArgumentException("Unknown periodMultiplier " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriodMultiplier().intValue() + ".");
            };
            default ->
                    throw new IllegalArgumentException("Unknown period " + swapLeg.getPaymentDates().getPaymentFrequency().getPeriod() + ".");
        }

        //build schedule
        logger.info("Payment frequency detected: " + Objects.requireNonNull(frequency));
        final ScheduleDescriptor scheduleDescriptor = new ScheduleDescriptor(startDate, maturityDate, frequency, daycountConvention, ScheduleGenerator.ShortPeriodConvention.LAST,
                dateRollConvention, new BusinessdayCalendarExcludingTARGETHolidays(), fixingOffsetDays, paymentOffsetDays);


        List<CashflowPeriod> cashflowPeriods = new ArrayList<>();
        Schedule schedule = scheduleDescriptor.getSchedule(this.smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0).tradeHeader.tradeDate.value.toGregorianCalendar().toZonedDateTime().toLocalDate());
        double notional = swapLeg.calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.initialValue.doubleValue();
        //double rate = swapLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue.doubleValue();

        // begin copypasted code. TODO: ask someone to review this
        List<CalibrationDataset> marketDataSets;
        try {
            marketDataSets = CalibrationParserDataItems.getScenariosFromJsonString(marketData);
        } catch (IOException e) {
            logger.error("Failed to load market data.");
            logger.error("I will now rethrow the exception and fail. Sorry! :(");
            throw new RuntimeException(e);
        }
        Validate.isTrue(marketDataSets.size() == 1, "Parameter marketData should be only a single market data set");

        LocalDateTime marketDataTime = marketDataSets.get(0).getDate();

        final Optional<CalibrationDataset> optionalScenario = marketDataSets.stream().filter(scenario -> scenario.getDate().equals(marketDataTime)).findAny();
        final CalibrationDataset scenario = optionalScenario.get();
        final LocalDate referenceDate = marketDataTime.toLocalDate();

        final CalibrationParserDataItems parser = new CalibrationParserDataItems();
        final Calibrator calibrator = new Calibrator();

        final Stream<CalibrationSpecProvider> allCalibrationItems = scenario.getDataAsCalibrationDataPointStream(parser);


        final Optional<CalibrationResult> optionalCalibrationResult;
        try {
            optionalCalibrationResult = calibrator.calibrateModel(allCalibrationItems, new CalibrationContextImpl(referenceDate, 1E-9));
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        AnalyticModel calibratedModel = optionalCalibrationResult.get().getCalibratedModel();

        /* Check the product */
        String forwardCurveID = "forward-EUR-6M"; // TODO: ask Christian why these IDs are needed otherwise everything crashes
        String discountCurveID = "discount-EUR-OIS";


        Set<CalibrationDataItem> pastFixings = scenario.getFixingDataItems();

        // @Todo what if we have no past fixing provided
        // @Todo what when we are exactly on the fixing date but before 11:00 am.
        ForwardCurveInterpolation fixedCurve = this.getCurvePastFixings("fixedCurve", referenceDate, calibratedModel, discountCurveID, pastFixings);//ForwardCurveInterpolation.createForwardCurveFromForwards("pastFixingCurve", pastFixingTimeArray, pastFixingArray, paymentOffset);
        Curve forwardCurveWithFixings = new ForwardCurveWithFixings(calibratedModel.getForwardCurve(forwardCurveID), fixedCurve, schedule.getFixing(0), 0.0);
        Curve[] finalCurves = {calibratedModel.getDiscountCurve(discountCurveID), calibratedModel.getForwardCurve(forwardCurveID), forwardCurveWithFixings};
        calibratedModel = new AnalyticModelFromCurvesAndVols(referenceDate, finalCurves);


        //end copypasted code
        int i = 0;
        for (Period schedulePeriod : schedule) {
            double rate = legSelector.equals(LegSelector.FIXED_LEG) ? swapLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue.doubleValue() :
                    calibratedModel.getForwardCurve(forwardCurveID).getForward(calibratedModel, schedule.getFixing(i));
            double homePartyisPayerPartyFactor = ((Party) swapLeg.payerPartyReference.href).id.equals(this.smartDerivativeContract.receiverPartyID) ? 1.0 : -1.0;

            cashflowPeriods.add(new CashflowPeriod()
                    .cashflow(new ValueResult().currency("EUR")
                            .valuationDate(new Date().toString())
                            .value(BigDecimal.valueOf(homePartyisPayerPartyFactor * schedule.getPeriodLength(i) * notional * rate)))
                    .fixingDate(OffsetDateTime.of(schedulePeriod.getFixing(), LocalTime.NOON, ZoneOffset.UTC))
                    .paymentDate(OffsetDateTime.of(schedulePeriod.getPayment(), LocalTime.NOON, ZoneOffset.UTC))
                    .periodStart(OffsetDateTime.of(schedulePeriod.getPeriodStart(), LocalTime.NOON, ZoneOffset.UTC))
                    .periodEnd(OffsetDateTime.of(schedulePeriod.getPeriodEnd(), LocalTime.NOON, ZoneOffset.UTC)));
            i++;
        }

        return cashflowPeriods;


    }

    private ForwardCurveInterpolation getCurvePastFixings(final String curveID, LocalDate referenceDate, AnalyticModel model, String discountCurveName, final Set<CalibrationDataItem> pastFixings) {
        Map<Double, Double> fixingMap = new LinkedHashMap<>();
        pastFixings.stream().forEach(item -> fixingMap.put(FloatingpointDate.getFloatingPointDateFromDate(referenceDate, item.getDate()), item.getQuote()));
        double[] pastFixingTimes = fixingMap.keySet().stream().mapToDouble(time -> time).toArray();
        double[] pastFixingsValues = Arrays.stream(pastFixingTimes).map(time -> fixingMap.get(time)).toArray();
        ForwardCurveInterpolation.InterpolationEntityForward interpolationEntityForward = ForwardCurveInterpolation.InterpolationEntityForward.FORWARD;
        ForwardCurveInterpolation fixedCurve = ForwardCurveInterpolation.createForwardCurveFromForwards(curveID, referenceDate, "offsetcode", interpolationEntityForward, discountCurveName, model, pastFixingTimes, pastFixingsValues);
        return fixedCurve;
    }

    public enum LegSelector {
        FIXED_LEG,
        FLOATING_LEG
    }

}
