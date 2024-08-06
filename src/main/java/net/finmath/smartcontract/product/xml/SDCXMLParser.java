package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.*;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A lean XML parser for the SDC XML format. See smartderivativecontract.xsd
 *
 * @author Christian Fries
 */
public class SDCXMLParser {

    private static final Logger logger = LoggerFactory.getLogger(SDCXMLParser.class);

    private SDCXMLParser() {
    }

    public static SmartDerivativeContractDescriptor parse(String sdcxml) throws ParserConfigurationException, IOException, SAXException {

        Smartderivativecontract sdc = unmarshalXml(sdcxml, Smartderivativecontract.class);

        LocalDateTime settlementDateInitial = LocalDateTime.parse(sdc.getSettlement().settlementDateInitial.trim());

        String uniqueTradeIdentifier = sdc.getUniqueTradeIdentifier().trim();
        String dltAddress = sdc.getDltAddress() == null ? "" : sdc.getDltAddress().trim();
        String dltTradeId = sdc.getDltTradeId() == null ? "" : sdc.getDltTradeId().trim();

		/*
		Market Data
		 */
        List<CalibrationDataItem.Spec> marketdataItems = new ArrayList<>();
        for(Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item item : sdc.getSettlement().getMarketdata().getMarketdataitems().getItem()){
            String symbol = item.getSymbol().get(0).trim();
            String curve = item.getCurve().get(0).trim();
            String type = item.getType().get(0).trim();
            String tenor = item.getTenor().get(0).trim();
            CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec(symbol, curve, type, tenor);
            marketdataItems.add(spec);
        }

        /*
         * Counterparties
         */
        List<SmartDerivativeContractDescriptor.Party> parties = new ArrayList<>();
        Map<String, Double> marginAccountInitialByPartyID = new HashMap<>();
        Map<String, Double> penaltyFeeInitialByPartyID = new HashMap<>();

        for(Smartderivativecontract.Parties.Party p : sdc.getParties().getParty()){
            SmartDerivativeContractDescriptor.Party party = new SmartDerivativeContractDescriptor.Party(
                    p.getId().trim(),
                    p.getName().trim(),
                    null,
                    p.getAddress().trim()
            );
            parties.add(party);
            marginAccountInitialByPartyID.put(party.getId(), p.getMarginAccount().getValue());
            penaltyFeeInitialByPartyID.put(party.getId(), p.getPenaltyFee().getValue());
        }

        // Receiver party ID
        String receiverPartyID = sdc.getReceiverPartyID().trim();

        // TODO The parser needs to check that the field receiverPartyID of the SDC matched the field <receiverPartyReference href="party2"/> in the FPML

        // TODO Support multiple underlyings

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(sdcxml.getBytes(StandardCharsets.UTF_8)));
        document.getDocumentElement().normalize();

        Node underlying = document
                .getElementsByTagName("underlying")
                .item(0)
                .getFirstChild();
        if (!underlying.getNodeName().equals("dataDocument")) {
            underlying = underlying.getNextSibling();
        }

        return new SmartDerivativeContractDescriptor(dltTradeId, dltAddress, uniqueTradeIdentifier, settlementDateInitial, parties, marginAccountInitialByPartyID, penaltyFeeInitialByPartyID, receiverPartyID, underlying, marketdataItems);
    }

    public static <T> T unmarshalXml(String xml, Class<T> t) {
        try {
            StringReader reader = new StringReader(xml);
            JAXBContext jaxbContext = JAXBContext.newInstance(t);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new SDCException(ExceptionId.SDC_JAXB_ERROR, e.getMessage(), 400);
        }
    }

    public static <T> String marshalClassToXMLString(T t) {
        try {
            JAXBContext jaxbContextSettlement = JAXBContext.newInstance(t.getClass());
            Marshaller jaxbMarshaller = jaxbContextSettlement.createMarshaller();
            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(t, writer);
            return writer.toString();
        } catch (JAXBException e) {
            logger.error("jaxb error, ", e);
            throw new SDCException(ExceptionId.SDC_JAXB_ERROR, e.getMessage(), 400);
        }
    }
}
