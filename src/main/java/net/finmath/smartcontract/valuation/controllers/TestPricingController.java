package net.finmath.smartcontract.valuation.controllers;

import net.finmath.smartcontract.model.TradeDescriptor;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.xml.GenerateXmlController;
import net.finmath.smartcontract.product.xml.TradeXmlGenerator;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestPricingController {
    protected Logger logger = LoggerFactory.getLogger(GenerateXmlController.class);

    @GetMapping("/pricefromfrontend")
    public String testPrice(){
        return "Ready to price something";
    }

    @PostMapping("/pricefromfrontend")
    public String testPrice(@RequestBody TradeDescriptor tradeDescription) throws Exception {
        String xmlBody = (new TradeXmlGenerator()).marshallTradeDescriptorOntoXml(tradeDescription);
        String marketData = (new ClassPathResource("references"+ File.separator +"singledataset.json")).getContentAsString(StandardCharsets.UTF_8);
        ValueResult wrappedResult = (new MarginCalculator()).getValue(marketData,xmlBody);
        return "Value is reported to be " + wrappedResult.getValue().toString() + " " + wrappedResult.getCurrency();
    }
}
