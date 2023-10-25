package net.finmath.smartcontract.product.xml;

import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

class SDCXMLParserTest {

	@Test
	void testParser() throws IOException, SAXException, ParserConfigurationException {

		String sdcXML = new String(SDCXMLParserTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);

		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);

		// Get the date
		LocalDateTime date = sdc.getTradeDate();
		System.out.println(date);

		Assertions.assertEquals(sdc.getUniqueTradeIdentifier(),"UTI12345");

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
}