package net.finmath.smartcontract.service.controllers;

import jakarta.xml.bind.JAXBException;
import net.finmath.rootfinder.BisectionSearch;
import net.finmath.smartcontract.api.PlainSwapEditorApi;
import net.finmath.smartcontract.model.CashflowPeriod;
import net.finmath.smartcontract.model.PlainSwapOperationRequest;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.xml.PlainSwapEditorHandler;
import net.finmath.smartcontract.valuation.MarginCalculator;
import net.finmath.util.TriFunction;
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
import java.util.function.DoubleUnaryOperator;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PlainSwapEditorController implements PlainSwapEditorApi {

    private final String schemaPath = "schemas/sdc-schemas/sdcml-contract.xsd"; //may be changed to allow for different versions of the schema
    private final String marketDataPath = "net.finmath.smartcontract.client" + File.separator + "md_testset2.json";  // will be changed when we will accept market data from external sources
    @Value("${hostname}")
    private String hostname;

    @Override
    public ResponseEntity<String> generatePlainSwapSdcml(PlainSwapOperationRequest plainSwapOperationRequest) {
        try {
            return ResponseEntity.ok( new PlainSwapEditorHandler(plainSwapOperationRequest, plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString());
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }


    }

    @Override
    public ResponseEntity<ValueResult> evaluateFromPlainSwapEditor(PlainSwapOperationRequest plainSwapOperationRequest) {
        String sdcmlBody;
        try {
            sdcmlBody = new PlainSwapEditorHandler(plainSwapOperationRequest, plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString();
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        String marketData;
        try {
            marketData = (new ClassPathResource(marketDataPath)).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        ValueResult valueResult;
        try {
            valueResult = (new MarginCalculator()).getValue(marketData, sdcmlBody);
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Valuation error.");
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        return ResponseEntity.ok(valueResult);
    }

    @Override
    public ResponseEntity<List<CashflowPeriod>> getFixedSchedule(PlainSwapOperationRequest plainSwapOperationRequest) {
        String marketData;
        try {
            marketData = (new ClassPathResource(marketDataPath)).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        try {
            return ResponseEntity.ok(new PlainSwapEditorHandler(plainSwapOperationRequest, plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getSchedule(PlainSwapEditorHandler.LegSelector.FIXED_LEG, marketData));
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (CloneNotSupportedException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.VALUATION_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    @Override
    public ResponseEntity<List<CashflowPeriod>> getFloatingSchedule(PlainSwapOperationRequest plainSwapOperationRequest) {
        String marketData;
        try {
            marketData = (new ClassPathResource(marketDataPath)).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        try {
            return ResponseEntity.ok(new PlainSwapEditorHandler(plainSwapOperationRequest, plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getSchedule(PlainSwapEditorHandler.LegSelector.FLOATING_LEG, marketData));
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (CloneNotSupportedException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.VALUATION_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    @Override
    public ResponseEntity<Double> getParRate(PlainSwapOperationRequest plainSwapOperationRequest) {
        TriFunction<DoubleUnaryOperator, Double, Double, Double> getRoot = (valueOperator, xMin, xMax) -> {

            BisectionSearch rootFinder = new BisectionSearch(xMin, xMax);
            while (rootFinder.getAccuracy() > 1E-12 && !rootFinder.isDone()) {
                final double x = rootFinder.getNextPoint();
                final double y = valueOperator.applyAsDouble(x);
                rootFinder.setValue(y);
            }
            return rootFinder.getBestPoint();
        };

        String marketData;
        try {
            marketData = (new ClassPathResource(marketDataPath)).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        try {

            DoubleUnaryOperator swapValue = (swapRate) -> {
                plainSwapOperationRequest.fixedRate(swapRate);
                try {
                    return (new MarginCalculator()).getValue(marketData, new PlainSwapEditorHandler(plainSwapOperationRequest.notionalAmount(1E7), plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString()).getValue().doubleValue();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            return ResponseEntity.ok(getRoot.apply(swapValue, -100.0, 100.0));

        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Valuation error.");
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
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
