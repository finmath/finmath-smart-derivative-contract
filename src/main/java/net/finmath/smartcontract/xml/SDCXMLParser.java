package net.finmath.smartcontract.xml;

import net.finmath.smartcontract.descriptor.TradeDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

	public static class Party {

		private final String id;
		private final String name;
		private final String href;

		public Party(String id, String name, String href) {
			this.id = id;
			this.name = name;
			this.href = href;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getHref() {
			return href;
		}
	}

	public static class SmartDerivativeContractDescriptor {

		private final LocalDateTime tradeDate;
		private final List<Party> counterparties;
		private final Map<String, Double>  marginAccountInitialByPartyID;
		private final Map<String, Double> penaltyFeeInitialByPartyID;

		private final Node underlying;

		public SmartDerivativeContractDescriptor(LocalDateTime tradeDate, List<Party> counterparties, Map<String, Double> marginAccountInitialByPartyID, Map<String, Double> penaltyFeeInitialByPartyID, Node underlying) {
			this.tradeDate = tradeDate;
			this.counterparties = counterparties;
			this.marginAccountInitialByPartyID = marginAccountInitialByPartyID;
			this.penaltyFeeInitialByPartyID = penaltyFeeInitialByPartyID;
			this.underlying = underlying;
		}

		public LocalDateTime getTradeDate() {
			return tradeDate;
		}

		public List<Party> getCounterparties() {
			return counterparties;
		}

		public Double getMarginAccount(String partyID) {
			return marginAccountInitialByPartyID.get(partyID);
		}

		public Double getPenaltyFee(String partyID) {
			return penaltyFeeInitialByPartyID.get(partyID);
		}

		public Node getUnderlying() {
			return underlying;
		}
	}

	private SDCXMLParser() {};

	public static SmartDerivativeContractDescriptor parse(String sdcxml) throws ParserConfigurationException, IOException, SAXException {

		LocalDateTime settlementDateInitial;

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(sdcxml.getBytes(StandardCharsets.UTF_8)));
		document.getDocumentElement().normalize();

		TradeDescriptor tradeDescriptor = new TradeDescriptor();

		String tradeDateString = document.getElementsByTagName("settlementDateInitial").item(0).getTextContent();

		settlementDateInitial = LocalDateTime.parse(tradeDateString.trim());


		/*
		 * Counterparties
		 */
		List<Party> parties = new ArrayList<>();
		Map<String, Double> marginAccountInitialByPartyID = new HashMap<>();
		Map<String, Double> penaltyFeeInitialByPartyID = new HashMap<>();

		NodeList partiesNodeList = document.getElementsByTagName("parties").item(0).getChildNodes();
		List<Node> partyNodes = nodeChildsByName(document.getElementsByTagName("parties").item(0), "party");
		for(Node partyNode : partyNodes) {

			Party party = new Party(
					nodeValueByName(partyNode, "name", String.class),
					nodeValueByName(partyNode, "id", String.class),
					null
					);

			Double marginAccountInitial = nodeValueByName(nodeChildByName(partyNode, "marginAccount"),"value", Double.class);
			Double penaltyFeeInitial = nodeValueByName(nodeChildByName(partyNode, "penaltyFee"),"value", Double.class);

			parties.add(party);
			marginAccountInitialByPartyID.put(party.getId(), marginAccountInitial);
			penaltyFeeInitialByPartyID.put(party.getId(), penaltyFeeInitial);
		}

		Node underlying = document.getElementsByTagName("underlying").item(0);
		return new SmartDerivativeContractDescriptor(settlementDateInitial, parties, marginAccountInitialByPartyID, penaltyFeeInitialByPartyID, underlying);
	}

	private static List<Node> nodeChildsByName(Node node, String name) {
		// Iterate
		List<Node> nodes = new ArrayList<>();
		NodeList childs = node.getChildNodes();
		for(int i=0; i<childs.getLength(); i++) {
			Node childNode = childs.item(i);
			if(name.equals(childNode.getNodeName())) {
				nodes.add(childNode);
			}
		}
		return nodes;
	}

	private static Node nodeChildByName(Node node, String name) {
		// Iterate
		NodeList nodes = node.getChildNodes();
		for(int i=0; i<nodes.getLength(); i++) {
			Node childNode = nodes.item(i);
			if(name.equals(childNode.getNodeName())) {
				return childNode;
			}
		}

		throw new IllegalArgumentException("Node not found");
	}

	private static <T> T nodeValueByName(Node node, String name, Class<T> type) {

		// Iterate
		NodeList nodes = node.getChildNodes();
		for(int i=0; i<nodes.getLength(); i++) {
			Node childNode = nodes.item(i);
			if(name.equals(childNode.getNodeName())) {
				String value = childNode.getTextContent();
				if(type.equals(String.class)) {
					return (T)value;
				}
				else if(type.equals(Double.class)) {
					return (T)Double.valueOf(value);
				}
				else {
					throw new IllegalArgumentException("Type not supported");
				}
			}
		}

		throw new IllegalArgumentException("Node not found");
	}
}
