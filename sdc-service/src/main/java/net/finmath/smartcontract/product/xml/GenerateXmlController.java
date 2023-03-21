package net.finmath.smartcontract.product.xml;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBException;
import net.finmath.smartcontract.model.TradeDescriptor;
import net.finmath.smartcontract.model.XmlResponse;
import net.finmath.smartcontract.product.xml.TradeXmlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class GenerateXmlController {
    protected Logger logger = LoggerFactory.getLogger(GenerateXmlController.class);

    @PostMapping("/generatexml")
    public XmlResponse generateXml(HttpServletResponse response, @RequestBody TradeDescriptor tradeDescription) throws JAXBException, IOException, DatatypeConfigurationException {
        response.addHeader("Access-Control-Allow-Origin", "localhost:4200");
        logger.info("Accepted XML generation request. Allocating response...");
        XmlResponse xmlResponse = new XmlResponse();
        logger.info("...done. Parsing request...");
        String xmlBody = (new TradeXmlGenerator()).marshallTradeDescriptorOntoXml(tradeDescription);
        logger.info("...done. Sending response to client.");
        xmlResponse.setXmlBody(xmlBody);
        return xmlResponse;
    }
}
