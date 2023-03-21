package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.finmath.smartcontract.model.TradeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public final class TradeXmlGenerator {

    private static Logger logger = LoggerFactory.getLogger(TradeXmlGenerator.class);
    private static void setSdcSettlementHeader(
            final TradeDescriptor tradeDescriptor,
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
        settlementHeader.marketdata.marketdataitems =
                new Smartderivativecontract.Settlement.Marketdata.Marketdataitems();
        sdc.setSettlement(settlementHeader);
    }

    private static void setSdcPartiesHeader(
            final TradeDescriptor tradeDescriptor,
            final Smartderivativecontract smartDerivativeContract) {
        logger.info("Setting SDC header of reponse.");
        Smartderivativecontract.Parties parties =
                new Smartderivativecontract.Parties();
        List<Smartderivativecontract.Parties.Party> partyList =
                new ArrayList<>();

        Smartderivativecontract.Parties.Party party1 =
                new Smartderivativecontract.Parties.Party();
        logger.info("Setting id 'party1' for party " + tradeDescriptor.getFirstCounterparty());
        party1.setName(tradeDescriptor.getFirstCounterparty());
        party1.setId("party1");
        Smartderivativecontract.Parties.Party.MarginAccount marginAccount1 =
                new Smartderivativecontract.Parties.Party.MarginAccount();
        marginAccount1.setType("constant");
        marginAccount1.setValue(tradeDescriptor.getMarginBufferAmount());
        Smartderivativecontract.Parties.Party.PenaltyFee penaltyFee1 =
                new Smartderivativecontract.Parties.Party.PenaltyFee();
        penaltyFee1.setType("constant");
        penaltyFee1.setValue(tradeDescriptor.getTerminationFeeAmount());
        party1.setAddress("0x0");

        logger.info("Setting id 'party2' for party " + tradeDescriptor.getSecondCounterparty());
        Smartderivativecontract.Parties.Party party2 =
                new Smartderivativecontract.Parties.Party();
        party2.setName(tradeDescriptor.getSecondCounterparty());
        party2.setId("party2");
        Smartderivativecontract.Parties.Party.MarginAccount marginAccount2 =
                new Smartderivativecontract.Parties.Party.MarginAccount();
        marginAccount2.setType("constant");
        marginAccount2.setValue(tradeDescriptor.getMarginBufferAmount());
        Smartderivativecontract.Parties.Party.PenaltyFee penaltyFee2 =
                new Smartderivativecontract.Parties.Party.PenaltyFee();
        penaltyFee2.setType("constant");
        penaltyFee2.setValue(tradeDescriptor.getTerminationFeeAmount());
        party2.setAddress("0x0");

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
            final TradeDescriptor tradeDescriptor) throws JAXBException,
                                                          FileNotFoundException,
                                                          DatatypeConfigurationException {
        JAXBContext jaxbContext =
                JAXBContext.newInstance(
                        "net.finmath.sdcbackend.xml",
                        this.getClass().getClassLoader()); //needs the standard classloader, prevents tomcat from overriding this
        Marshaller marshaller = jaxbContext.createMarshaller();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        logger.info("Accepted incoming request with body:");
        logger.info(tradeDescriptor.toString());
        // create new SDCmL file as object
        Smartderivativecontract smartDerivativeContract =
                new Smartderivativecontract();
        File testXML = ResourceUtils.getFile(
                "classpath:references/sample_xml_file.xml");
        Smartderivativecontract contract =
                (Smartderivativecontract) unmarshaller.unmarshal(testXML);

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
                               GregorianCalendar.from(tradeDescriptor.getTradeDate()
                                                     )
                                               );
        formattedTradeDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        trade.tradeHeader.tradeDate.setValue(formattedTradeDate);

        Swap swap = ((Swap) smartDerivativeContract.underlyings.underlying.dataDocument.trade.get(
                0).getProduct().getValue());
        InterestRateStream floatingLeg = swap.swapStream.get(0);
        InterestRateStream fixedLeg = swap.swapStream.get(1);

        // for each swapstream... (index is the order of appearance in the template)
        XMLGregorianCalendar formattedEffectiveDate = DatatypeFactory.newInstance()
                                                                 .newXMLGregorianCalendar(
                                                                         GregorianCalendar.from(tradeDescriptor.getEffectiveDate()
                                                                                               )
                                                                                         );
        formattedEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        XMLGregorianCalendar formattedTerminationDate = DatatypeFactory.newInstance()
                                                                 .newXMLGregorianCalendar(
                                                                         GregorianCalendar.from(tradeDescriptor.getTerminationDate()
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
                            tradeDescriptor.getNotionalAmount())));
            swap.swapStream.get(
                    i).calculationPeriodAmount.calculation.notionalSchedule.notionalStepSchedule.currency.value =
                    tradeDescriptor.getCurrency();
            swap.swapStream.get(
                    i).paymentDates.paymentFrequency.periodMultiplier =
                    BigInteger.valueOf(Long.parseLong(
                            tradeDescriptor.getFloatingPaymentFrequency()));
        }

        if (tradeDescriptor.getFloatingPayingParty()
                           .equals(smartDerivativeContract
                                           .parties
                                           .party
                                           .get(0)
                                           .name)
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
                BigDecimal.valueOf(Double.parseDouble(Float.toString(tradeDescriptor.getFixedRate())))
                          .divide(BigDecimal.valueOf(100L));
        floatingLeg.resetDates.fixingDates.periodMultiplier =
                BigInteger.valueOf(
                        Integer.parseInt(
                                tradeDescriptor.getFloatingFixingDayOffset()));
        floatingLeg.calculationPeriodAmount.calculation.dayCountFraction.value =
                tradeDescriptor.getFloatingDayCountFraction();
        fixedLeg.calculationPeriodAmount.calculation.dayCountFraction.value =
                tradeDescriptor.getFixedDayCountFraction();
        ((FloatingRateCalculation) floatingLeg.calculationPeriodAmount
                .calculation.getRateCalculation()
                            .getValue()).floatingRateIndex.value =
                tradeDescriptor.getFloatingRateIndex();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // marshall xml out
        marshaller.marshal(smartDerivativeContract, outputStream);
        return outputStream.toString();

    }

}
