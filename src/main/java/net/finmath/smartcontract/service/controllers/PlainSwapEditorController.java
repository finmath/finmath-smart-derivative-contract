package net.finmath.smartcontract.service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import jakarta.xml.bind.JAXBException;
import net.finmath.rootfinder.BisectionSearch;
import net.finmath.smartcontract.api.PlainSwapEditorApi;
import net.finmath.smartcontract.marketdata.adapters.LiveFeedAdapter;
import net.finmath.smartcontract.marketdata.adapters.ReactiveMarketDataUpdater;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.database.DatabaseConnector;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.PlainSwapEditorHandler;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.MarginCalculator;
import net.finmath.util.TriFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "${serviceUrl}"}, allowCredentials = "true")
public class PlainSwapEditorController implements PlainSwapEditorApi {

    private static final Logger logger = LoggerFactory.getLogger(PlainSwapEditorController.class);
    /*
    Routine for calculation of par rates through the bisection algorithm
     */
    private static final TriFunction<DoubleUnaryOperator, Double, Double, Double> getRoot
            = (valueOperator, xMin, xMax) -> {

        BisectionSearch rootFinder = new BisectionSearch(xMin, xMax);
        while (rootFinder.getAccuracy() > 1E-7 && !rootFinder.isDone()) {
            final double x = rootFinder.getNextPoint();
            final double y = valueOperator.applyAsDouble(x);
            rootFinder.setValue(y);
        }
        return rootFinder.getBestPoint();
    };
    private final String schemaPath = "schemas/sdc-schemas/sdcml-contract.xsd";
    //may be changed to allow for different versions of the schema
    @Autowired
    private DatabaseConnector databaseConnector;

    @Value("${hostname:localhost:8080}")
    private String hostname;

    @Value("${storage.internals.refinitivConnectionPropertiesFile}")
    private String refinitivConnectionPropertiesFile;

    @Value("${storage.basedir}")
    private String storageBaseDir;


    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;


    /**
     * Controller that handles requests for generation of a SDCmL document.
     *
     * @param plainSwapOperationRequest, the specification for the contract as gathered from the user forms
     * @return a string containing the SDCmL document
     */
    @Override
    public ResponseEntity<String> generatePlainSwapSdcml(PlainSwapOperationRequest plainSwapOperationRequest) {
        try {
            return ResponseEntity.ok(new PlainSwapEditorHandler(plainSwapOperationRequest,
                                                                plainSwapOperationRequest.getCurrentGenerator(),
                                                                schemaPath).getContractAsXmlString());
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            /*
            You may see this recurring snippet of code in other controller methods as well. Its goal is to report the stack
            trace to the client application in case of errors. This is handy for debugging sessions where there's no access
            to the Spring console. Furthermore, it provides a facility for the frontend to explain the error to a non-technical user,
            i.e. use the error code to redirect the user to a page with an explanation of what happened. This is not in place as of 10 Juli 2023,
            the errors just get dumped in the browser console. Requires the flags server.error.include-message and server.error.include-stacktrace
            in application.yml
             */
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
    }

    /**
     * Controller that handles requests for contract valuation.
     *
     * @param plainSwapOperationRequest the specification for the contract as gathered from the user forms
     * @return a result object containing value, currency and reference timestamp for the dataset used
     */
    @Override
    public ResponseEntity<ValueResult> evaluateFromPlainSwapEditor(PlainSwapOperationRequest plainSwapOperationRequest) {
        String sdcmlBody;
        try {
            sdcmlBody = new PlainSwapEditorHandler(plainSwapOperationRequest,
                                                   plainSwapOperationRequest.getCurrentGenerator(),
                                                   schemaPath).getContractAsXmlString();
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        String marketDataString;
        MarketDataTransferMessage marketData;
        try {

            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(
                                                              env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")
                                                      .getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.MARKET_DATA_ERROR_DETAIL);
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

    /**
     * Controller that handles requests for payment schedule generation relative to the fixed leg.
     *
     * @param plainSwapOperationRequest the specification for the contract as gathered from the user forms
     * @return a list of cashflows matched with their reference period
     */
    @Override
    public ResponseEntity<List<CashflowPeriod>> getFixedSchedule(PlainSwapOperationRequest plainSwapOperationRequest) {
        String marketDataString;
        MarketDataTransferMessage marketData;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(
                                                              env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")
                                                      .getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        try {
            return ResponseEntity.ok(new PlainSwapEditorHandler(plainSwapOperationRequest,
                                                                plainSwapOperationRequest.getCurrentGenerator(),
                                                                schemaPath).getSchedule(
                    PlainSwapEditorHandler.LegSelector.FIXED_LEG, marketData));
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (CloneNotSupportedException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.VALUATION_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    /**
     * Controller that handles requests for payment schedule generation relative to the floating leg.
     *
     * @param plainSwapOperationRequest the specification for the contract as gathered from the user forms
     * @return a list of cashflows matched with their reference period
     */
    @Override
    public ResponseEntity<List<CashflowPeriod>> getFloatingSchedule(PlainSwapOperationRequest plainSwapOperationRequest) {
        String marketDataString;
        MarketDataTransferMessage marketData;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(
                                                              env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")
                                                      .getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        try {
            return ResponseEntity.ok(new PlainSwapEditorHandler(plainSwapOperationRequest,
                                                                plainSwapOperationRequest.getCurrentGenerator(),
                                                                schemaPath).getSchedule(
                    PlainSwapEditorHandler.LegSelector.FLOATING_LEG, marketData));
        } catch (JAXBException | IOException | DatatypeConfigurationException | SAXException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (CloneNotSupportedException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.VALUATION_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    /**
     * Controller that echoes the active dataset to the client
     *
     * @return a market data transfer message that matches the contents of the active dataset
     */
    @Override
    public ResponseEntity<MarketDataTransferMessage> grabMarketData() {
        String marketDataString;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(
                                                              env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")
                                                      .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        MarketDataTransferMessage marketData;
        try {
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
        } catch (JsonProcessingException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        return ResponseEntity.ok(marketData);
    }


    /**
     * Controller that handles requests for refresh of the market data
     *
     * @return the valuation result obtained with the refreshed data
     */
    @Override
    public ResponseEntity<ValueResult> refreshMarketData(PlainSwapOperationRequest plainSwapOperationRequest) {
        SmartDerivativeContractDescriptor sdc;
        try {
            sdc = SDCXMLParser.parse(new PlainSwapEditorHandler(plainSwapOperationRequest,
                                                                plainSwapOperationRequest.getCurrentGenerator(),
                                                                schemaPath).getContractAsXmlString());
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (SAXException | JAXBException | ParserConfigurationException | DatatypeConfigurationException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.JAXB_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.JAXB_ERROR_URI));
            pd.setTitle(ErrorDetails.JAXB_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        List<CalibrationDataItem.Spec> calibrationDataItemSpecs = sdc.getMarketdataItemList();

        /* Load connection properties*/
        Properties refinitivConnectionProperties = new Properties();
        try {
            refinitivConnectionProperties.load(new FileInputStream(refinitivConnectionPropertiesFile));
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        /* Init Websocket Connection*/
        WebSocketConnector socketConnector;
        WebSocket webSocket;
        try {
            socketConnector = new WebSocketConnector(refinitivConnectionProperties);
            webSocket = socketConnector.getWebSocket();
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.WEBSOCKET_ERROR_URI));
            pd.setTitle(ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        /* Market Data Adapter*/
        final LiveFeedAdapter<MarketDataTransferMessage> emitter = new ReactiveMarketDataUpdater(
                socketConnector.getAuthJson(), socketConnector.getPosition(), calibrationDataItemSpecs);
        webSocket.addListener(emitter);
        try {
            webSocket.connect();
        } catch (WebSocketException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.WEBSOCKET_ERROR_URI));
            pd.setTitle(ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        /* This checks the SDCmL to see if the ESTR rate is to be considered quoted or fixed */
        boolean isOvernightFixing = calibrationDataItemSpecs.stream().anyMatch(
                x -> (x.getProductName().equals("Fixing") && x.getKey().equals("EUROSTR=")));
        try {
            /* TODO: maybe there's a way to avoid writing everything to a file before Postgre imports the JSON file.
                     Possibly using PSQL client side facilities?
             */
            emitter.writeDataset(Objects.requireNonNull(env.getProperty("storage.importdir")),
                                 emitter.asObservable().blockingFirst(), isOvernightFixing);
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.WEBSOCKET_ERROR_URI));
            pd.setTitle(ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        try {
            File importBaseFolder = new File(Objects.requireNonNull(env.getProperty("storage.importdir")));
            File importTargetFile = new File(importBaseFolder, "import_candidate.json");
            databaseConnector.updateDatabase(importTargetFile.getAbsolutePath());
            List<String> fixingSymbols = new ArrayList<>();
            for (CalibrationDataItem.Spec calibrationDataItemSpec : calibrationDataItemSpecs) {
                if (calibrationDataItemSpec.getProductName().equals("Fixing"))
                    fixingSymbols.add(calibrationDataItemSpec.getKey());
            }
            databaseConnector.fetchFromDatabase(Objects.requireNonNull(env.getProperty("storage.basedir")),
                                                fixingSymbols);
            logger.info("Refresh complete.");
            emitter.closeStreamsAndLogoff(webSocket); // very very very important, do not forget this!
        } catch (SQLException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.DATABASE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.DATABASE_ERROR_URI));
            pd.setTitle(ErrorDetails.DATABASE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        /*
        TODO: maybe we can wrap the websocket class into an AutoCloseable. Would make things cleaner.
         */
        webSocket.sendClose();
        webSocket.disconnect();

        return this.evaluateFromPlainSwapEditor(plainSwapOperationRequest);
    }

    /**
     * Controller method that handles requests for calculating the par rate
     *
     * @param plainSwapOperationRequest the specification for the contract as gathered from the user forms
     * @return the par rate
     */
    @Override
    public ResponseEntity<Double> getParRate(PlainSwapOperationRequest plainSwapOperationRequest) {

        String marketDataString;
        MarketDataTransferMessage marketData;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(
                                                              env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")
                                                      .getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.MARKET_DATA_ERROR_URI));
            pd.setTitle(ErrorDetails.MARKET_DATA_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        try {

            /*
            TODO: this could be made faster by avoiding regeneration of the SDCml at every iteration.
                  I just don't know how to keep into account business calendars and fixing dates in an elegant way
             */
            DoubleUnaryOperator swapValue = (swapRate) -> {
                plainSwapOperationRequest.fixedRate(swapRate);
                try {
                    return (new MarginCalculator()).getValue(marketData, new PlainSwapEditorHandler(
                                                           plainSwapOperationRequest.notionalAmount(1E15),
                                                           plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString())
                                                   .getValue().doubleValue();
                } catch (Exception e) {
                    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                        ErrorDetails.VALUATION_ERROR_DETAIL);
                    pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
                    pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
                    throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
                }
            };

            /* Seems reasonable that the par rate is in between -100% and 100%.
            If this is not a good way of finding par rates let me know -Luca
             */
            return ResponseEntity.ok(getRoot.apply(swapValue, -100.0, 100.0));

        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Valuation error.");
            pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
            pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

    }

    /**
     * Controller that handles requests for the contents of the user contract storage.
     *
     * @return a list of file names
     */
    @Override
    public ResponseEntity<List<String>> getSavedContracts() {
        List<String> savedContractsFilenames = new ArrayList<>();
        Resource[] savedContracts;
        try {
            savedContracts = resourcePatternResolver.getResources("file:///" + Objects.requireNonNull(
                    env.getProperty("storage.basedir")) + "/user1.savedcontracts/*");
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        for (final Resource savedContract : savedContracts) {
            savedContractsFilenames.add(savedContract.getFilename());
        }
        return ResponseEntity.ok(savedContractsFilenames);
    }

    /**
     * Controller that handles requests for performing the hotswap of the active dataset
     *
     * @param fileName the source file name or the USELIVE directive
     * @return a brief status message
     */
    @Override
    public ResponseEntity<String> changeDataset(String fileName) {
        if (fileName.equals("USELIVE")) {
            return ResponseEntity.ok("idle ok");
        }

        try (FileInputStream sourceInputStream = new FileInputStream(
                Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/" + fileName);
             FileOutputStream destinationInputStream = new FileOutputStream(Objects.requireNonNull(
                     env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")) {
            FileChannel source = sourceInputStream.getChannel();
            FileChannel destination = destinationInputStream.getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        return ResponseEntity.ok("ok");


    }

    /**
     * Controller that handles requests for the contents of the user market data storage.
     *
     * @return a list of file names
     */
    @Override
    public ResponseEntity<List<String>> getSavedMarketData() {
        List<String> savedContractsFilenames = new ArrayList<>();
        Resource[] savedContracts;
        try {
            savedContracts = resourcePatternResolver.getResources(
                    "file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/*");
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        for (final Resource savedContract : savedContracts) {
            savedContractsFilenames.add(savedContract.getFilename());
        }
        return ResponseEntity.ok(savedContractsFilenames);
    }

    /**
     * Controller that handles requests for loading a stored contract.
     *
     * @param requestedFilename the name of the file containing the stored contract
     * @return a contract specification
     */
    @Override
    public ResponseEntity<PlainSwapOperationRequest> loadContract(String requestedFilename) {
        Resource[] savedContracts;
        try {
            savedContracts = resourcePatternResolver.getResources("file:///" + Objects.requireNonNull(
                    env.getProperty("storage.basedir")) + "/user1.savedcontracts/*");
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        Optional<Resource> requestedContractOptional = Arrays.stream(savedContracts)
                                                             .filter(contract -> Objects.requireNonNull(
                                                                     contract.getFilename()).contentEquals(
                                                                     requestedFilename)).findFirst();
        Resource requestedContract = null;
        if (requestedContractOptional.isPresent())
            requestedContract = requestedContractOptional.get();
        try {
            PlainSwapOperationRequest requestedRequest = objectMapper.readValue(
                    Objects.requireNonNull(requestedContract).getContentAsString(StandardCharsets.UTF_8),
                    PlainSwapOperationRequest.class);
            return ResponseEntity.ok(requestedRequest);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
    }

    /**
     * Controller that handles requests for saving a contract.
     *
     * @param saveContractRequest an object containing contract specification and destination file name
     * @return a brief status message
     */
    @Override
    public ResponseEntity<String> saveContract(SaveContractRequest saveContractRequest) {
        String regex = "^[A-za-z0-9]{1,255}$"; // only alphanumerical characters allowed!
        LocalDate date = LocalDate.now();
        if (saveContractRequest.getName().matches(regex)) {
            File baseFolder = new File(
                    Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.savedcontracts/");
            File targetFile = new File(baseFolder, date + saveContractRequest.getName() + ".json");
            try {
                boolean creationResult = targetFile.createNewFile();
                if (creationResult)
                    logger.info("New file created at " + targetFile.getAbsolutePath());
                else
                    logger.info("Attempting overwrite of file " + targetFile.getAbsolutePath());

            } catch (IOException e) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                    ErrorDetails.STORAGE_ERROR_DETAIL);
                pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
                pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
                throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
            }
            try {
                objectMapper.writeValue(targetFile, saveContractRequest.getPlainSwapOperationRequest());
            } catch (IOException e) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                    ErrorDetails.STORAGE_ERROR_DETAIL);
                pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
                pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
                throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
            }
            return ResponseEntity.ok(date + saveContractRequest.getName() + ".json");
        }
        return ResponseEntity.ok("Request not fulfilled.");
    }


    /**
     * Controller that handles requests for saving market data.
     *
     * @param marketData a multipart file wrapping the source JSON file
     * @return a brief status message
     */
    @Override
    public ResponseEntity<String> uploadMarketData(MultipartFile marketData) {
        File importBaseFolder = new File(Objects.requireNonNull(env.getProperty("storage.importdir")));
        File importTargetFile = new File(importBaseFolder, "import_candidate.json");
        File storageBaseFolder = new File(
                Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/");
        File storageTargetFile = new File(storageBaseFolder, Objects.requireNonNull(marketData.getOriginalFilename()));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(importTargetFile);
            fileOutputStream.write(marketData.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(storageTargetFile);
            fileOutputStream.write(marketData.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            databaseConnector.updateDatabase(importTargetFile.getAbsolutePath());
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (SQLException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                                ErrorDetails.DATABASE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.DATABASE_ERROR_URI));
            pd.setTitle(ErrorDetails.DATABASE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        return ResponseEntity.ok("ok");

    }

    private static final class ErrorDetails {

        static final String JAXB_ERROR_DETAIL = "JAXB Error";
        static final String VALUATION_ERROR_DETAIL = "Valuation Error";
        static final String MARKET_DATA_ERROR_DETAIL = "Market Data Error";
        static final String DATABASE_ERROR_DETAIL = "Database Error";
        static final String WEBSOCKET_ERROR_DETAIL = "Websocket Error";
        static final String STORAGE_ERROR_DETAIL = "Storage Error";

    }

    /*
    In the future the frontend app might include pages explaining the error to the user
     */
    private static final class ErrorTypeURI {

        static final String JAXB_ERROR_URI = "/jaxb-error";
        static final String VALUATION_ERROR_URI = "/valuation-error";
        static final String MARKET_DATA_ERROR_URI = "/market-data-error";
        static final String DATABASE_ERROR_URI = "/database-error";
        static final String WEBSOCKET_ERROR_URI = "/websocket-error";
        static final String STORAGE_ERROR_URI = "/storage-error";

    }
}
