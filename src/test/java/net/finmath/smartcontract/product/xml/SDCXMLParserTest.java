package net.finmath.smartcontract.product.xml;

import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.settlement.Settlement;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SDCXMLParserTest {

	@Test
	void testParser() throws IOException, SAXException, ParserConfigurationException {

		String sdcXML = new String(SDCXMLParserTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);

		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);

		// Get the date
		LocalDateTime date = sdc.getTradeDate();
		System.out.println(date);

		Assertions.assertEquals("UTI12345", sdc.getUniqueTradeIdentifier());
		Assertions.assertEquals("ID-Test123", sdc.getDltTradeId());
		Assertions.assertEquals("0x000000001", sdc.getDltAddress());
		Assertions.assertEquals("EUR", sdc.getCurrency());
		Assertions.assertEquals("internal", sdc.getMarketDataProvider());

		// Get parties
		List<SmartDerivativeContractDescriptor.Party> parties = sdc.getCounterparties();
		parties.stream().forEach(System.out::println);

		// Get receiver party
		String receiverParty = sdc.getUnderlyingReceiverPartyID();
		System.out.println(receiverParty);
		Assertions.assertEquals("party1", receiverParty, "Reciever party ID.");

		System.out.println("Adress party 1: " + sdc.getCounterparties().get(0).getAddress());
		System.out.println("Adress party 2: " + sdc.getCounterparties().get(1).getAddress());

		// Get the underlying
		Node underlying = sdc.getUnderlying();
		// This needs cleaning
		ProductDescriptor productDescriptor = new FPMLParser("party1", "forward-EUR-3M", "discount-EUR-OIS").getProductDescriptor(underlying);
		System.out.println(productDescriptor.name());

		InterestRateSwapProductDescriptor irs = (InterestRateSwapProductDescriptor) productDescriptor;
		System.out.println(irs.getLegPayer());
		System.out.println(irs.getLegReceiver());
	}

	@Test
	void parseFpmlByGenericParser() throws IOException {
		final String fpml = new String(SDCXMLParserTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);

		Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(fpml, Smartderivativecontract.class);

		System.out.println(sdc.getSettlement().getSettlementDateInitial().trim());

		assertEquals("net.finmath", sdc.getValuation().getArtefact().getGroupId().trim());
		assertEquals("UTI12345", sdc.getUniqueTradeIdentifier().trim());
		assertEquals("party1", sdc.getReceiverPartyID().trim());
	}

    @Test
    void unmarshalXml() throws IOException {
		final String marketDataXml = new String(Objects.requireNonNull(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml")).readAllBytes(), StandardCharsets.UTF_8);
		final MarketDataList marketData = SDCXMLParser.unmarshalXml(marketDataXml, MarketDataList.class);

		Assertions.assertNotNull(marketData);
    }

	@Test
	void unmarshalXml_wrongInput() throws IOException {
		final String marketDataXml = new String(Objects.requireNonNull(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml")).readAllBytes(), StandardCharsets.UTF_8);
		assertThrows(SDCException.class, () -> SDCXMLParser.unmarshalXml(marketDataXml, Object.class));
	}

    @Test
    void marshalClassToXMLString() {
		Settlement newSettlement = new Settlement();

		MarketDataList marketDataList = new MarketDataList();
		MarketDataPoint marketDataPoint = new MarketDataPoint();
		marketDataPoint.setValue(3.14);
		marketDataPoint.setId("EUR example symbol");
		marketDataPoint.setTimeStamp(LocalDateTime.now().minusHours(2));
		marketDataList.getPoints().add(marketDataPoint);
		marketDataList.setRequestTimeStamp(LocalDateTime.now());
		newSettlement.setMarketData(marketDataList);
		newSettlement.setCurrency("EUR");
		newSettlement.setSettlementType(Settlement.SettlementType.REGULAR);

		String xmlString = SDCXMLParser.marshalClassToXMLString(newSettlement);

		Assertions.assertTrue(xmlString.contains("xml version"));
		Assertions.assertTrue(xmlString.contains("<settlement>"));
		Assertions.assertTrue(xmlString.contains("</marketData>"));
    }

	@Test
	void marshalSDCToXMLString() throws IOException, ParserConfigurationException, SAXException {
		//given
		String fpml = new String(SDCXMLParserTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(fpml, Smartderivativecontract.class);

		//when
		String xmlString = SDCXMLParser.marshalSDCToXMLString(sdc);
		String xmlStringNameSpaceTags = SDCXMLParser.marshalClassToXMLString(sdc);
		SmartDerivativeContractDescriptor sdcDescriptor = SDCXMLParser.parse(xmlString);

		//then
		//xml based
		Assertions.assertTrue(xmlStringNameSpaceTags.contains("<fpml:dataDocument fpmlVersion=\"5-9\">"));
		Assertions.assertEquals(1, StringUtils.countMatches(xmlStringNameSpaceTags,"<fpml:dataDocument fpmlVersion=\"5-9\">"));
		Assertions.assertFalse(xmlString.contains("<fpml:dataDocument fpmlVersion=\"5-9\">"));
		Assertions.assertTrue(xmlString.contains("<dataDocument fpmlVersion=\"5-9\" xmlns=\"http://www.fpml.org/FpML-5/confirmation\">"));
		Assertions.assertTrue(xmlStringNameSpaceTags.contains("</fpml:"));
		Assertions.assertFalse(xmlString.contains("</fpml:"));

		//parsed back
		Assertions.assertEquals("UTI12345", sdcDescriptor.getUniqueTradeIdentifier());
		Assertions.assertEquals("ID-Test123", sdcDescriptor.getDltTradeId());
		Assertions.assertEquals("0x000000001", sdcDescriptor.getDltAddress());
		Assertions.assertEquals("EUR", sdcDescriptor.getCurrency());
		Assertions.assertEquals("internal", sdcDescriptor.getMarketDataProvider());
	}

}