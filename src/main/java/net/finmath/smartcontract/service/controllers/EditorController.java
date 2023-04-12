package net.finmath.smartcontract.service.controllers;

import jakarta.xml.bind.JAXBException;
import net.finmath.smartcontract.api.EditorApi;
import net.finmath.smartcontract.model.CashflowPeriod;
import net.finmath.smartcontract.model.SdcXmlRequest;
import net.finmath.smartcontract.model.SdcXmlResponse;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.xml.TradeXmlGenerator;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EditorController implements EditorApi {

    Logger logger = LoggerFactory.getLogger(EditorController.class);

    @Value("${hostname}")
    private String hostname;

    @Override
    public ResponseEntity<SdcXmlResponse> generateXml(SdcXmlRequest sdcXmlRequest) {
        logger.info("Accepted XML generation request. Allocating response...");
        SdcXmlResponse xmlResponse = new SdcXmlResponse();
        logger.info("...done. Parsing request...");
        String xmlBody;
        try {
            xmlBody = new TradeXmlGenerator(sdcXmlRequest).getContractAsXmlString();
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        logger.info("...done. Sending response to client.");
        xmlResponse.setXmlBody(xmlBody);
        return ResponseEntity.ok(xmlResponse);
    }

    @Override
    public ResponseEntity<ValueResult> evaluateFromEditor(SdcXmlRequest sdcXmlRequest) {
        String xmlBody;
        try {
            xmlBody = new TradeXmlGenerator(sdcXmlRequest).getContractAsXmlString();
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        String marketData;
        try {
            marketData = (new ClassPathResource("net.finmath.smartcontract.client" + File.separator + "md_testset2.json")).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        ValueResult valueResult;
        try {
            valueResult = (new MarginCalculator()).getValue(marketData, xmlBody);
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Valuation error.");
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        return ResponseEntity.ok(valueResult);
    }

    @Override
    public ResponseEntity<List<CashflowPeriod>> getFixedSchedule(SdcXmlRequest sdcXmlRequest) {
        String marketData;
        try {
            marketData = (new ClassPathResource("net.finmath.smartcontract.client" + File.separator + "md_testset2.json")).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        try {
            return ResponseEntity.ok(new TradeXmlGenerator(sdcXmlRequest).getSchedule(TradeXmlGenerator.LegSelector.FIXED_LEG,marketData));
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    @Override
    public ResponseEntity<List<CashflowPeriod>> getFloatingSchedule(SdcXmlRequest sdcXmlRequest) {
        String marketData;
        try {
            marketData = (new ClassPathResource("net.finmath.smartcontract.client" + File.separator + "md_testset2.json")).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        try {
            return ResponseEntity.ok(new TradeXmlGenerator(sdcXmlRequest).getSchedule(TradeXmlGenerator.LegSelector.FLOATING_LEG,marketData));
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    private static final class ErrorDetails {

        static final String JAXB_ERROR_DETAIL = "JAXB Error";
        static final String VALUATION_ERROR_DETAIL = "Valuation Error";
        static final String MARKET_DATA_ERROR_DETAIL = "Market Data Error";

    }

    private static final class ErrorTypeURI {

        static final String JAXB_ERROR_URI = "/jaxb-error";
        static final String VALUATION_ERROR_URI = "/valuation-error";
        static final String MARKET_DATA_ERROR_URI = "/market-data-error";

    }
}
