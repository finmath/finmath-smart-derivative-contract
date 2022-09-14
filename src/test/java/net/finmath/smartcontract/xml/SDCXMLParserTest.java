package net.finmath.smartcontract.xml;

import net.finmath.modelling.ProductDescriptor;
import net.finmath.smartcontract.descriptor.xmlparser.FPMLParser;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

class SDCXMLParserTest {

	@Test
	void testParser() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {

		String sdcXML = FileUtils.readFileToString(new File(SDCXMLParserTest.class.getClassLoader().getResource("smartderivativecontract.xml").toURI()), StandardCharsets.UTF_8);

		SDCXMLParser.SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(sdcXML);
		
		String date = sdc.getTradeDate().toString();
		System.out.println(date);

		Node underlying = sdc.getUnderlying();

		// This needs cleaning
		ProductDescriptor productDescriptor = new FPMLParser("", "", "").generateProductDescriptor(underlying);
	}
}