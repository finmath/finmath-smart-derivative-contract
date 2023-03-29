package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.JAXBException;
import net.finmath.smartcontract.model.TradeDescriptor;
import net.finmath.smartcontract.model.XmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class GenerateXmlController {
    protected Logger logger = LoggerFactory.getLogger(GenerateXmlController.class);

    @GetMapping("/generatexml")
    public String generateXml(){
        return "Ready to generate some XMLs!";
    }

    @PostMapping("/generatexml")
    public XmlResponse generateXml(@RequestBody TradeDescriptor tradeDescription) throws JAXBException, IOException, DatatypeConfigurationException {
        logger.info("Accepted XML generation request. Allocating response...");
        XmlResponse xmlResponse = new XmlResponse();
        logger.info("...done. Parsing request...");
        String xmlBody = (new TradeXmlGenerator()).marshallTradeDescriptorOntoXml(tradeDescription);
        logger.info("...done. Sending response to client.");
        xmlResponse.setXmlBody(xmlBody);
        return xmlResponse;
    }
}
