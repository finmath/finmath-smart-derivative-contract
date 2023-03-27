package net.finmath.smartcontract.valuation.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBException;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.model.TradeDescriptor;
import net.finmath.smartcontract.model.ValueRequest;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.model.XmlResponse;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.GenerateXmlController;
import net.finmath.smartcontract.product.xml.TradeXmlGenerator;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestPricingController {
    protected Logger logger = LoggerFactory.getLogger(GenerateXmlController.class);

    @GetMapping("/pricefromfrontend")
    public String testPrice(){
        return "Ready to price something";
    }

    @PostMapping("/pricefromfrontend")
    public String testPrice(HttpServletResponse response, @RequestBody TradeDescriptor tradeDescription) throws Exception {
        String xmlBody = (new TradeXmlGenerator()).marshallTradeDescriptorOntoXml(tradeDescription);
        String marketData = (new ClassPathResource("references"+ File.separator +"singledataset.json")).getContentAsString(StandardCharsets.UTF_8);
        ValueResult wrappedResult = (new MarginCalculator()).getValue(marketData,xmlBody);
        return "Value is reported to be " + wrappedResult.getValue().toString() + " " + wrappedResult.getCurrency();
    }
}
