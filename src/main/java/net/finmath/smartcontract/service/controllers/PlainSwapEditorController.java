package net.finmath.smartcontract.service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import jakarta.xml.bind.JAXBException;
import net.finmath.rootfinder.BisectionSearch;
import net.finmath.smartcontract.api.PlainSwapEditorApi;
import net.finmath.smartcontract.marketdata.adapters.ReactiveMarketDataUpdater;
import net.finmath.smartcontract.marketdata.adapters.WebSocketConnector;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.PlainSwapEditorHandler;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.marketdata.database.DatabaseConnector;
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

    private final String schemaPath = "schemas/sdc-schemas/sdcml-contract.xsd"; //may be changed to allow for different versions of the schema

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


    @Override
    public ResponseEntity<String> generatePlainSwapSdcml(PlainSwapOperationRequest plainSwapOperationRequest) {
        try {
            return ResponseEntity.ok(new PlainSwapEditorHandler(plainSwapOperationRequest, plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString());
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
        String marketDataString;
        MarketDataTransferMessage marketData;
        try {

            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json").getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
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
        String marketDataString;
        MarketDataTransferMessage marketData;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json").getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
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
        String marketDataString;
        MarketDataTransferMessage marketData;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json").getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
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
    public ResponseEntity<MarketDataTransferMessage> grabMarketData() {
        String marketDataString;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json").getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        MarketDataTransferMessage marketData;
        try {
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
        } catch (JsonProcessingException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        return ResponseEntity.ok(marketData);
    }

    @Override
    public ResponseEntity<ValueResult> refreshMarketData(PlainSwapOperationRequest plainSwapOperationRequest) {
        SmartDerivativeContractDescriptor sdc;
        try {
            sdc = SDCXMLParser.parse(new PlainSwapEditorHandler(plainSwapOperationRequest, plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString());
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (SAXException | JAXBException | ParserConfigurationException | DatatypeConfigurationException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.JAXB_ERROR_DETAIL);
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
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        /* Init Websockect Connection*/
        WebSocketConnector socketConnector;
        WebSocket webSocket;
        try {
            socketConnector = new WebSocketConnector(refinitivConnectionProperties);
            webSocket = socketConnector.getWebSocket();
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.WEBSOCKET_ERROR_URI));
            pd.setTitle(ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        /* Market Data Adapter*/
        final ReactiveMarketDataUpdater emitter = new ReactiveMarketDataUpdater(socketConnector.getAuthJson(), socketConnector.getPosition(), calibrationDataItemSpecs);
        webSocket.addListener(emitter);
        try {
            webSocket.connect();
        } catch (WebSocketException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.WEBSOCKET_ERROR_URI));
            pd.setTitle(ErrorDetails.WEBSOCKET_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        boolean isOvernightFixing = calibrationDataItemSpecs.stream().anyMatch(x -> (x.getProductName().equals("Fixing") && x.getKey().equals("EUROSTR=")));
        try {
            emitter.writeDataset(Objects.requireNonNull(env.getProperty("storage.importdir")), emitter.asObservable().blockingFirst(), isOvernightFixing);
        } catch (Exception e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.WEBSOCKET_ERROR_DETAIL);
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
            databaseConnector.fetchFromDatabase(Objects.requireNonNull(env.getProperty("storage.basedir")), fixingSymbols);
            logger.info("Refresh complete.");
            emitter.closeStreamsAndLogoff(webSocket);
        } catch (SQLException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.DATABASE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.DATABASE_ERROR_URI));
            pd.setTitle(ErrorDetails.DATABASE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        webSocket.sendClose();
        webSocket.disconnect();

        return this.evaluateFromPlainSwapEditor(plainSwapOperationRequest);
    }

    @Override
    public ResponseEntity<Double> getParRate(PlainSwapOperationRequest plainSwapOperationRequest) {
        TriFunction<DoubleUnaryOperator, Double, Double, Double> getRoot = (valueOperator, xMin, xMax) -> {

            BisectionSearch rootFinder = new BisectionSearch(xMin, xMax);
            while (rootFinder.getAccuracy() > 1E-7 && !rootFinder.isDone()) {
                final double x = rootFinder.getNextPoint();
                final double y = valueOperator.applyAsDouble(x);
                rootFinder.setValue(y);
            }
            return rootFinder.getBestPoint();
        };

        String marketDataString;
        MarketDataTransferMessage marketData;
        try {
            marketDataString = resourcePatternResolver.getResource("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json").getContentAsString(StandardCharsets.UTF_8);
            marketData = objectMapper.readValue(marketDataString, MarketDataTransferMessage.class);
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
                    return (new MarginCalculator()).getValue(marketData, new PlainSwapEditorHandler(plainSwapOperationRequest.notionalAmount(1E15), plainSwapOperationRequest.getCurrentGenerator(), schemaPath).getContractAsXmlString()).getValue().doubleValue();
                } catch (Exception e) {
                    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.VALUATION_ERROR_DETAIL);
                    pd.setType(URI.create(hostname + ErrorTypeURI.VALUATION_ERROR_URI));
                    pd.setTitle(ErrorDetails.VALUATION_ERROR_DETAIL);
                    throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
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

    @Override
    public ResponseEntity<List<String>> getSavedContracts() {
        List<String> savedContractsFilenames = new ArrayList<>();
        Resource[] savedContracts;
        try {
            savedContracts = resourcePatternResolver.getResources("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.savedcontracts/*");
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        for (final Resource savedContract : savedContracts) {
            savedContractsFilenames.add(savedContract.getFilename());
        }
        return ResponseEntity.ok(savedContractsFilenames);
    }

    @Override
    public ResponseEntity<String> changeDataset(String body) {
        if (body.equals("USELIVE")) {
            return ResponseEntity.ok("idle ok");
        }

        try (FileInputStream sourceInputStream = new FileInputStream(Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/" + body); FileOutputStream destinationInputStream = new FileOutputStream(Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/active_dataset.json")) {
            FileChannel source = sourceInputStream.getChannel();
            FileChannel destination = destinationInputStream.getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }

        return ResponseEntity.ok("ok");


    }

    @Override
    public ResponseEntity<List<String>> getSavedMarketData() {
        List<String> savedContractsFilenames = new ArrayList<>();
        Resource[] savedContracts;
        try {
            savedContracts = resourcePatternResolver.getResources("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/*");
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        for (final Resource savedContract : savedContracts) {
            savedContractsFilenames.add(savedContract.getFilename());
        }
        return ResponseEntity.ok(savedContractsFilenames);
    }

    @Override
    public ResponseEntity<PlainSwapOperationRequest> loadContract(String requestedFilename) {
        Resource[] savedContracts;
        try {
            savedContracts = resourcePatternResolver.getResources("file:///" + Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.savedcontracts/*");
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
        Optional<Resource> requestedContractOptional = Arrays.stream(savedContracts).filter(contract -> Objects.requireNonNull(contract.getFilename()).contentEquals(requestedFilename)).findFirst();
        Resource requestedContract = null;
        if (requestedContractOptional.isPresent()) requestedContract = requestedContractOptional.get();
        try {
            PlainSwapOperationRequest requestedRequest = objectMapper.readValue(Objects.requireNonNull(requestedContract).getContentAsString(StandardCharsets.UTF_8), PlainSwapOperationRequest.class);
            return ResponseEntity.ok(requestedRequest);
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        }
    }

    @Override
    public ResponseEntity<String> saveContract(SaveContractRequest saveContractRequest) {
        String regex = "^[A-za-z0-9]{1,255}$";
        LocalDate date = LocalDate.now();
        if (saveContractRequest.getName().matches(regex)) {
            File baseFolder = new File(Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.savedcontracts/");
            File targetFile = new File(baseFolder, date + saveContractRequest.getName() + ".json");
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
                pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
                pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
                throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
            }
            try {
                objectMapper.writeValue(targetFile, saveContractRequest.getPlainSwapOperationRequest());
            } catch (IOException e) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
                pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
                pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
                throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
            }
            return ResponseEntity.ok(date + saveContractRequest.getName() + ".json");
        }
        return ResponseEntity.ok("Request not fulfilled.");
    }

    @Override
    public ResponseEntity<String> uploadMarketData(MultipartFile tradeData) {
        File importBaseFolder = new File(Objects.requireNonNull(env.getProperty("storage.importdir")));
        File importTargetFile = new File(importBaseFolder, "import_candidate.json");
        File storageBaseFolder = new File(Objects.requireNonNull(env.getProperty("storage.basedir")) + "/user1.marketdata/");
        File storageTargetFile = new File(storageBaseFolder, Objects.requireNonNull(tradeData.getOriginalFilename()));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(importTargetFile);
            fileOutputStream.write(tradeData.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            fileOutputStream = new FileOutputStream(storageTargetFile);
            fileOutputStream.write(tradeData.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            databaseConnector.updateDatabase(importTargetFile.getAbsolutePath());
        } catch (IOException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.STORAGE_ERROR_DETAIL);
            pd.setType(URI.create(hostname + ErrorTypeURI.STORAGE_ERROR_URI));
            pd.setTitle(ErrorDetails.STORAGE_ERROR_DETAIL);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, pd, e);
        } catch (SQLException e) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDetails.DATABASE_ERROR_DETAIL);
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

    private static final class ErrorTypeURI {

        static final String JAXB_ERROR_URI = "/jaxb-error";
        static final String VALUATION_ERROR_URI = "/valuation-error";
        static final String MARKET_DATA_ERROR_URI = "/market-data-error";
        static final String DATABASE_ERROR_URI = "/database-error";
        static final String WEBSOCKET_ERROR_URI = "/websocket-error";
        static final String STORAGE_ERROR_URI = "/storage-error";

    }
}
