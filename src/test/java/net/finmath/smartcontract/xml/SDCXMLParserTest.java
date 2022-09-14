package net.finmath.smartcontract.xml;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
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
		
		String party1 = sdc.getTradeDate().toString();
		System.out.println(party1);
	}
}