package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.*;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.settlement.Settlements;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
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

    private SDCXMLParser() {
    }

    public static SmartDerivativeContractDescriptor parse(String sdcxml) throws ParserConfigurationException, IOException, SAXException {

        LocalDateTime settlementDateInitial;

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(sdcxml.getBytes(StandardCharsets.UTF_8)));
        document.getDocumentElement().normalize();

        String tradeDateString = document.getElementsByTagName("settlementDateInitial").item(0).getTextContent();
        settlementDateInitial = LocalDateTime.parse(tradeDateString.trim());

        String uniqueTradeIdentifier = document.getElementsByTagName("uniqueTradeIdentifier").item(0).getTextContent();

		/*
		Market Data
		 */
        List<CalibrationDataItem.Spec> marketdataItems = new ArrayList<>();
        List<Node> itemNodes = nodeChildsByName(document.getElementsByTagName("marketdataitems").item(0), "item");
        for (Node itemNode : itemNodes) {
            String symbol = nodeValueByName(itemNode, "symbol", String.class);
            String curve = nodeValueByName(itemNode, "curve", String.class);
            String type = nodeValueByName(itemNode, "type", String.class);
            String tenor = nodeValueByName(itemNode, "tenor", String.class);
            CalibrationDataItem.Spec spec = new CalibrationDataItem.Spec(symbol, curve, type, tenor);
            marketdataItems.add(spec);
        }

        /*
         * Counterparties
         */
        List<SmartDerivativeContractDescriptor.Party> parties = new ArrayList<>();
        Map<String, Double> marginAccountInitialByPartyID = new HashMap<>();
        Map<String, Double> penaltyFeeInitialByPartyID = new HashMap<>();

        List<Node> partyNodes = nodeChildsByName(document.getElementsByTagName("parties").item(0), "party");
        for (Node partyNode : partyNodes) {

            SmartDerivativeContractDescriptor.Party party = new SmartDerivativeContractDescriptor.Party(
                    nodeValueByName(partyNode, "id", String.class),
                    nodeValueByName(partyNode, "name", String.class),
                    null,
                    nodeValueByName(partyNode, "address", String.class)
            );

            Double marginAccountInitial = nodeValueByName(nodeChildByName(partyNode, "marginAccount"), "value", Double.class);
            Double penaltyFeeInitial = nodeValueByName(nodeChildByName(partyNode, "penaltyFee"), "value", Double.class);

            parties.add(party);
            marginAccountInitialByPartyID.put(party.getId(), marginAccountInitial);
            penaltyFeeInitialByPartyID.put(party.getId(), penaltyFeeInitial);
        }


        // Receiver party ID
        String receiverPartyID = document.getElementsByTagName("receiverPartyID").item(0).getTextContent().trim();

        // TODO The parser needs to check that the field receiverPartyID of the SDC matched the field <receiverPartyReference href="party2"/> in the FPML

        // TODO Support multiple underlyings

        Node underlying = document
                .getElementsByTagName("underlying")
                .item(0)
                .getFirstChild();
        if (!underlying.getNodeName().equals("dataDocument")) {
            underlying = underlying.getNextSibling();
        }

        return new SmartDerivativeContractDescriptor(uniqueTradeIdentifier, settlementDateInitial, parties, marginAccountInitialByPartyID, penaltyFeeInitialByPartyID, receiverPartyID, underlying, marketdataItems);
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
        } catch (java.lang.Exception e) {
            throw new SDCException(ExceptionId.SDC_007, e.getMessage());
        }
    }

    public static <T> String marshalClassToXMLString(T t) {
        try {
            JAXBContext jaxbContextSettlement = JAXBContext.newInstance(Settlements.class);
            Marshaller jaxbMarshaller = jaxbContextSettlement.createMarshaller();
            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(t, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new SDCException(ExceptionId.SDC_006, e.getMessage());
        }
    }
}
