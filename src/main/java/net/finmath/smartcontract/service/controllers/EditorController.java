package net.finmath.smartcontract.service.controllers;

import jakarta.xml.bind.JAXBException;
import net.finmath.smartcontract.api.EditorApi;
import net.finmath.smartcontract.model.SdcXmlRequest;
import net.finmath.smartcontract.model.SdcXmlResponse;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.xml.TradeXmlGenerator;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EditorController implements EditorApi {

    Logger logger = LoggerFactory.getLogger(EditorController.class);

    @Override
    public ResponseEntity<SdcXmlResponse> generateXml(SdcXmlRequest sdcXmlRequest) {
        logger.info("Accepted XML generation request. Allocating response...");
        SdcXmlResponse xmlResponse = new SdcXmlResponse();
        logger.info("...done. Parsing request...");
        String xmlBody = null;
        try {
            xmlBody = (new TradeXmlGenerator()).marshallTradeDescriptorOntoXml(sdcXmlRequest);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        logger.info("...done. Sending response to client.");
        xmlResponse.setXmlBody(xmlBody);
        return ResponseEntity.ok(xmlResponse);
    }

    @Override
    public ResponseEntity<ValueResult> evaluateFromEditor(SdcXmlRequest sdcXmlRequest) {
        String xmlBody = null;
        try {
            xmlBody = (new TradeXmlGenerator()).marshallTradeDescriptorOntoXml(sdcXmlRequest);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        String marketData = null;
        try {
            marketData = (new ClassPathResource("net.finmath.smartcontract.client"+ File.separator +"md_testset2.json")).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ValueResult wrappedResult = null;
        try {
            wrappedResult = (new MarginCalculator()).getValue(marketData,xmlBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(wrappedResult);
    }
}
