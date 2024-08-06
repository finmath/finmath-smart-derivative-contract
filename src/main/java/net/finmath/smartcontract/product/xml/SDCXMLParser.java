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
import org.w3c.dom.NodeList;
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

        LocalDateTime settlementDateInitial;

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(sdcxml.getBytes(StandardCharsets.UTF_8)));
        document.getDocumentElement().normalize();

        settlementDateInitial = LocalDateTime.parse(sdc.getSettlement().settlementDateInitial.trim());

        String uniqueTradeIdentifier = sdc.getUniqueTradeIdentifier().trim();
        String dltAddress = sdc.getDltAddress() == null ? "" : sdc.getDltAddress().trim();
        String dltTradeId = sdc.getDltTradeId() == null ? "" : sdc.getDltTradeId().trim();

		/*
		Market Data
		 */
        List<CalibrationDataItem.Spec> marketdataItems = new ArrayList<>();
        //List<Node> itemNodes = nodeChildsByName(document.getElementsByTagName("marketdataitems").item(0), "item");
        //for (Node itemNode : itemNodes) {
        for(Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item item : sdc.getSettlement().getMarketdata().getMarketdataitems().getItem()){
            //String symbol = nodeValueByName(itemNode, "symbol", String.class);
            String symbol = item.getSymbol().get(0).trim();
            //String curve = nodeValueByName(itemNode, "curve", String.class);
            String curve = item.getCurve().get(0).trim();
            //String type = nodeValueByName(itemNode, "type", String.class);
            String type = item.getType().get(0).trim();
            //String tenor = nodeValueByName(itemNode, "tenor", String.class);
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

        Node underlying = document
                .getElementsByTagName("underlying")
                .item(0)
                .getFirstChild();
        if (!underlying.getNodeName().equals("dataDocument")) {
            underlying = underlying.getNextSibling();
        }

        return new SmartDerivativeContractDescriptor(dltTradeId, dltAddress, uniqueTradeIdentifier, settlementDateInitial, parties, marginAccountInitialByPartyID, penaltyFeeInitialByPartyID, receiverPartyID, underlying, marketdataItems);
    }

    /*
     * Private helpers
     */

    private static List<Node> nodeChildsByName(Node node, String name) {
        // Iterate
        List<Node> nodes = new ArrayList<>();
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node childNode = childs.item(i);
            if (name.equals(childNode.getNodeName())) {
                nodes.add(childNode);
            }
        }
        return nodes;
    }

    private static Node nodeChildByName(Node node, String name) {
        // Iterate
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (name.equals(childNode.getNodeName())) {
                return childNode;
            }
        }

        throw new IllegalArgumentException("Node not found");
    }

    private static <T> T nodeValueByName(Node node, String name, Class<T> type) {

        // Iterate
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (name.equals(childNode.getNodeName())) {
                String value = childNode.getTextContent();
                if (type.equals(String.class)) {
                    return type.cast(value);
                } else if (type.equals(Double.class)) {
                    return type.cast(Double.valueOf(value));
                } else {
                    throw new IllegalArgumentException("Type not supported");
                }
            }
        }

        throw new IllegalArgumentException("Node not found");
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
