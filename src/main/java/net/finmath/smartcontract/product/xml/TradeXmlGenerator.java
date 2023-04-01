package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.finmath.smartcontract.model.SdcXmlRequest;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public final class TradeXmlGenerator {

    static Logger logger = LoggerFactory.getLogger(TradeXmlGenerator.class);
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

    public String marshallTradeDescriptorOntoXml(
            final SdcXmlRequest tradeDescriptor) throws JAXBException,
            IOException,
            DatatypeConfigurationException {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = factory.newSchema((new ClassPathResource("schemas"+File.separator+"sdc-schemas"+File.separator+"sdcml-contract.xsd")).getURL());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        JAXBContext jaxbContext =
                JAXBContext.newInstance(
                        "net.finmath.smartcontract.product.xml",
                        this.getClass().getClassLoader()); //needs the standard classloader, prevents tomcat from overriding this
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(schema);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        logger.info("Accepted incoming request with body:");
        logger.info(tradeDescriptor.toString());
        // create new SDCmL file as object
        Smartderivativecontract smartDerivativeContract =
                new Smartderivativecontract();
        ClassPathResource testXml = new ClassPathResource("references/sample_xml_file.xml");
        Smartderivativecontract contract =
                (Smartderivativecontract) unmarshaller.unmarshal(testXml.getInputStream());

        // set the SDC specific stuff in the helper methods
        setSdcValuationHeader(smartDerivativeContract);
        setSdcPartiesHeader(tradeDescriptor, smartDerivativeContract);
        setSdcSettlementHeader(tradeDescriptor, smartDerivativeContract);

        // set the FPmL body... FPmL is tough!
        // clone the template
        smartDerivativeContract.setUnderlyings(contract.getUnderlyings());

        Trade trade = smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(0);
        XMLGregorianCalendar formattedTradeDate = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                        GregorianCalendar.from(tradeDescriptor.getTradeDate().toZonedDateTime())
                );
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
        InterestRateStream floatingLeg = swap.swapStream.get(0);
        InterestRateStream fixedLeg = swap.swapStream.get(1);

        // for each swapstream... (index is the order of appearance in the template)
        XMLGregorianCalendar formattedEffectiveDate = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                        GregorianCalendar.from(tradeDescriptor.getEffectiveDate().toZonedDateTime()
                        )
                );
        formattedEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        XMLGregorianCalendar formattedTerminationDate = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                        GregorianCalendar.from(tradeDescriptor.getTerminationDate().toZonedDateTime()
                        )
                );
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
                            tradeDescriptor.getNotionalAmount().floatValue())));
            swap.swapStream.get(
                    i).calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.currency.value =
                    tradeDescriptor.getCurrency();
            swap.swapStream.get(
                    i).paymentDates.paymentFrequency.periodMultiplier =
                    BigInteger.valueOf(tradeDescriptor.getFloatingPaymentFrequency().longValue());
        }

        if (tradeDescriptor.getFloatingPayingParty().getFullName().equals(smartDerivativeContract.parties.party.get(0).name)
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



        fixedLeg.calculationPeriodAmount.calculation.fixedRateSchedule.initialValue =
                BigDecimal.valueOf(tradeDescriptor.getFixedRate()).divide(BigDecimal.valueOf(100L), RoundingMode.HALF_EVEN);
        floatingLeg.resetDates.fixingDates.periodMultiplier =
                BigInteger.valueOf(tradeDescriptor.getFloatingFixingDayOffset().longValue());
        floatingLeg.calculationPeriodAmount.calculation.dayCountFraction.value =
                tradeDescriptor.getFloatingDayCountFraction();
        fixedLeg.calculationPeriodAmount.calculation.dayCountFraction.value =
                tradeDescriptor.getFixedDayCountFraction();
        ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount
                .calculation.getRateCalculation()
                .getValue()).floatingRateIndex.value =
                tradeDescriptor.getFloatingRateIndex();

        smartDerivativeContract.settlement.marketdata.marketdataitems = contract.getSettlement().getMarketdata().getMarketdataitems();
        smartDerivativeContract.receiverPartyID = "party1";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(smartDerivativeContract, outputStream);
        //logger.info(outputStream.toString());
        // marshall xml out
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(outputStream.toByteArray())));
            logger.info("Validation successful!");
            // return outputStream.toString();

            // LOOK AT THIS UGLINESS!!! NOT NICE!!!!!!!
            logger.info("XML was correclty generated, will now do some ugliness.");
            return outputStream.toString()
                    .replaceAll("<fpml:dataDocument fpmlVersion=\"5-9\">","<dataDocument fpmlVersion=\"5-9\" xmlns=\"http://www.fpml.org/FpML-5/confirmation\">")
                    .replaceAll("fpml:","");

        } catch (IOException | SAXException e) {
            logger.error("Exception: "+e.getMessage());
            return "";
        }

    }

}
