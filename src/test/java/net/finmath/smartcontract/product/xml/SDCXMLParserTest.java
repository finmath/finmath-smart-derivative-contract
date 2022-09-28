package net.finmath.smartcontract.product.xml;

import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.smartcontract.descriptor.xmlparser.FPMLParser;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

class SDCXMLParserTest {

	@Test
	void testParser() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {

		String sdcXML = Files.readString(Path.of(SDCXMLParserTest.class.getClassLoader().getResource("smartderivativecontract.xml").getPath()), StandardCharsets.UTF_8);

		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);

		// Get the date
		LocalDateTime date = sdc.getTradeDate();
		System.out.println(date);

		// Get parties
		List<SDCXMLParser.Party> parties = sdc.getCounterparties();
		parties.stream().map(SDCXMLParser.Party::getName).forEach(System.out::println);

		// Get receiver party
		String receiverParty = sdc.getUnderlyingReceiverPartyID();
		System.out.println(receiverParty);
		Assertions.assertEquals("party2", receiverParty, "Reciever party ID.");

		// Get the underlying
		Node underlying = sdc.getUnderlying();
		// This needs cleaning
		ProductDescriptor productDescriptor = new FPMLParser("party1", "discount-EUR-OIS", "forward-EUR-3M").getProductDescriptor(underlying.getFirstChild().getNextSibling());
		System.out.println(productDescriptor.name());

		InterestRateSwapProductDescriptor irs = (InterestRateSwapProductDescriptor) productDescriptor;
		System.out.println(irs.getLegPayer());
		System.out.println(irs.getLegReceiver());
	}
}