package net.finmath.smartcontract.util;

import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SDCXMLUtil {
	
	public static String getXMLElement(String xml, String xp, String namespace) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		SimpleNamespaceContext nsCtx = new SimpleNamespaceContext();
		factory.setNamespaceAware(true);
		String result  = null;
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			Document doc = builder.parse(is);
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			
			if(namespace!=null) {
    			
				xpath.setNamespaceContext(nsCtx);
				nsCtx.bindNamespaceUri("ns", namespace);
        		xp = xp.replaceAll("/", "/ns:");	
    		} else {
    			xp = xp.replaceAll("ns:", "");
    		}
			XPathExpression expr = xpath.compile(xp + "/text()");
			result  = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
		
	public static String getXMLElement(String xml, String xp) {
		return getXMLElement(xml, xp, SDCProperties.getProperty(SDCConstants.FPML_NAMESPACE));
	}
}
