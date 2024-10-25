package net.finmath.smartcontract.valuation.service.utils;

import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;
import net.finmath.smartcontract.settlement.Settlement;
import net.finmath.smartcontract.settlement.SettlementGenerator;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;
import net.finmath.smartcontract.valuation.marketdata.generators.MarketDataGeneratorLauncher;
import net.finmath.smartcontract.valuation.marketdata.generators.MarketDataGeneratorScenarioList;
import net.finmath.smartcontract.valuation.service.config.RefinitivConfig;
import net.finmath.smartcontract.valuation.service.config.ValuationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SettlementService {
	private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);
	public static final String NEW_MARKET_DATA_STRING = "newMarketDataString: {}";
	public static final String SETTLEMENT_REQUEST_INFO = "sdc trade id: {}, product marketdata provider: {}, valuation service marketdata provider: {}";

	private final MarginCalculator marginCalculator = new MarginCalculator();

	private final RefinitivConfig refinitivConfig;
	private final ValuationConfig valuationConfig;
	private final MarketDataGeneratorScenarioList marketDataServiceScenarioList;

	public SettlementService(RefinitivConfig refinitivConfig, ValuationConfig valuationConfig) {
		this.refinitivConfig = refinitivConfig;
		this.valuationConfig = valuationConfig;
		this.marketDataServiceScenarioList = new MarketDataGeneratorScenarioList();
	}

	public RegularSettlementResult generateRegularSettlementResult(RegularSettlementRequest regularSettlementRequest) {
		logger.info("Generating regular settlement result, liveData: {}, now parsing trade data", valuationConfig.isLiveMarketData());
		SmartDerivativeContractDescriptor sdc = parseProductData(regularSettlementRequest.getTradeData());
		logger.info(SETTLEMENT_REQUEST_INFO, sdc.getDltTradeId(), sdc.getMarketDataProvider(), valuationConfig.getLiveMarketDataProvider());

		String newMarketDataString;

		if (regularSettlementRequest.getNewProvidedMarketData() == null) {
			MarketDataList newMarketDataList = retrieveMarketData(sdc);
			includeFixingsOfLastSettlement(regularSettlementRequest, newMarketDataList);
			newMarketDataString = SDCXMLParser.marshalClassToXMLString(newMarketDataList);
		} else {
			logger.info("provided custom marketData per string in regularSettlementRequest");
			newMarketDataString = regularSettlementRequest.getNewProvidedMarketData();
			checkMarketDataString(newMarketDataString);
			logger.info("provided marketData in regularSettlementRequest passed the marketData check");
		}
		logger.info(NEW_MARKET_DATA_STRING, newMarketDataString);

		Settlement settlementLast = SDCXMLParser.unmarshalXml(regularSettlementRequest.getSettlementLast(), Settlement.class);
		String marketDataLastString = SDCXMLParser.marshalClassToXMLString(settlementLast.getMarketData());

		ZonedDateTime settlementTimeNext = ZonedDateTime.now().plusDays(1);

		ValueResult settlementValueNext = getValuationValueAtTime(
				newMarketDataString, regularSettlementRequest.getTradeData(), settlementTimeNext.toLocalDateTime());

		BigDecimal margin = getMargin(marketDataLastString, newMarketDataString, regularSettlementRequest.getTradeData());

		String newSettlement = new SettlementGenerator()
				.generateRegularSettlementXml(
						newMarketDataString,
						sdc,
						margin)
				.marginLimits(settlementLast.getMarginLimits())
				.settlementNPV(getValue(newMarketDataString, regularSettlementRequest.getTradeData()))
				.settlementNPVPrevious(settlementLast.getSettlementNPV())
				.settlementTimeNext(settlementTimeNext)
				.settlementNPVNext(settlementValueNext.getValue())
				.build();

		return new RegularSettlementResult()
				.generatedRegularSettlement(newSettlement)
				.currency(valuationConfig.getSettlementCurrency())
				.marginValue(margin)
				.valuationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
	}

	public InitialSettlementResult generateInitialSettlementResult(InitialSettlementRequest initialSettlementRequest) {
		logger.info("Generating initial settlement result, liveData: {}, now parsing trade data", valuationConfig.isLiveMarketData());
		SmartDerivativeContractDescriptor sdc = parseProductData(initialSettlementRequest.getTradeData());
		logger.info(SETTLEMENT_REQUEST_INFO, sdc.getDltTradeId(), sdc.getMarketDataProvider(), valuationConfig.getLiveMarketDataProvider());
		String newMarketDataString;

		if (initialSettlementRequest.getNewProvidedMarketData() == null) {
			MarketDataList newMarketDataList = retrieveMarketData(sdc);
			newMarketDataString = SDCXMLParser.marshalClassToXMLString(newMarketDataList);
		} else {
			logger.info("provided custom marketData per string in initialSettlementRequest");
			newMarketDataString = initialSettlementRequest.getNewProvidedMarketData();
			checkMarketDataString(newMarketDataString);
			logger.info("provided marketData in initialSettlementRequest passed the marketData check");
		}
		logger.info(NEW_MARKET_DATA_STRING, newMarketDataString);

		ZonedDateTime settlementTimeNext = ZonedDateTime.now().plusDays(1);

		ValueResult settlementValueNext = getValuationValueAtTime(
				newMarketDataString, initialSettlementRequest.getTradeData(), settlementTimeNext.toLocalDateTime());

		List<BigDecimal> marginLimits = new ArrayList<>();
		sdc.getCounterparties().forEach(party -> marginLimits.add(BigDecimal.valueOf(sdc.getMarginAccount(party.getId()))));

		String newSettlement = new SettlementGenerator()
				.generateInitialSettlementXml(newMarketDataString, sdc)
				.marginLimits(marginLimits)
				.settlementNPV(getValue(newMarketDataString, initialSettlementRequest.getTradeData()))
				//.settlementValuePrevious(BigDecimal.ZERO)
				.settlementTimeNext(settlementTimeNext)
				.settlementNPVNext(settlementValueNext.getValue())
				.build();

		return new InitialSettlementResult()
				.generatedInitialSettlement(newSettlement)
				.currency(valuationConfig.getSettlementCurrency())
				.marginValue(BigDecimal.ZERO)
				.valuationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
	}

	/**
	 * checks the format of a providedMarketDataString, if not valid it will throw an exception
	 * @param newMarketDataString XML marketDataList as string
	 */
	private void checkMarketDataString(String newMarketDataString) {
		try {
			SDCXMLParser.unmarshalXml(newMarketDataString, MarketDataList.class);
		} catch (SDCException e){
			logger.error("MarketDataString is invalid, error while unmarshalling string: " + newMarketDataString, e);
			throw new SDCException(ExceptionId.SDC_XML_PARSE_ERROR, "provided MarketDataString is invalid, please check input", 400);
		} catch (Exception e) {
			logger.error("unknown error recognized while unmarshalling string: " + newMarketDataString, e);
			throw e;
		}
	}

	private static SmartDerivativeContractDescriptor parseProductData(String tradeData) {
		try {
			return SDCXMLParser.parse(tradeData);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			logger.error("error parsing product data ", e);
			throw new SDCException(ExceptionId.SDC_XML_PARSE_ERROR, "product data format incorrect, could not parse xml", 400);
		}
	}

	private MarketDataList retrieveMarketData(SmartDerivativeContractDescriptor sdc) {
		AtomicReference<MarketDataList> marketDataList = new AtomicReference<>(new MarketDataList());
		logger.info("retrieveMarketData started for trade: {}", sdc.getDltTradeId());

		if (sdc.getMarketDataProvider().equals(valuationConfig.getLiveMarketDataProvider()) && valuationConfig.isLiveMarketData()) {
			logger.info("using live market data provider");
			marketDataList.set(MarketDataGeneratorLauncher.instantiateMarketDataGeneratorWebsocket(initConnectionProperties(), sdc));
		} else if (sdc.getMarketDataProvider().equals(valuationConfig.getInternalMarketDataProvider())) {
			logger.info("using internal market data provider");
			//includes provider internal or no liveMarketData activated
			final io.reactivex.rxjava3.functions.Consumer<MarketDataList> marketDataWriter = marketDataList::set;
			marketDataServiceScenarioList.asObservable().subscribe(marketDataWriter,                        //onNext
					throwable -> logger.error("unable to generate marketData from files ", throwable),        //onError
					() -> logger.info("on complete, simulated marketData generated from files"));            //onComplete
		} else {
			logger.error("unable to retrieve marketData for {}", sdc.getDltTradeId());
			throw new SDCException(ExceptionId.SDC_WRONG_INPUT,
					"Product data XML is not compatible with valuation service configuration, see logs for further investigation", 400);
		}
		return marketDataList.get();
	}

	private Properties initConnectionProperties() {
		try {
			Properties connectionProperties = new Properties();
			connectionProperties.put("USER", refinitivConfig.getUser());
			connectionProperties.put("PASSWORD", refinitivConfig.getPassword());
			connectionProperties.put("CLIENTID", refinitivConfig.getClientId());
			connectionProperties.put("HOSTNAME", refinitivConfig.getHostName());
			connectionProperties.put("PORT", refinitivConfig.getPort());
			connectionProperties.put("AUTHURL", refinitivConfig.getAuthUrl());
			connectionProperties.put("USEPROXY", refinitivConfig.getUseProxy());
			connectionProperties.put("PROXYHOST", refinitivConfig.getProxyHost());
			connectionProperties.put("PROXYPORT", refinitivConfig.getProxyPort());
			connectionProperties.put("PROXYUSER", refinitivConfig.getProxyUser());
			connectionProperties.put("PROXYPASS", refinitivConfig.getProxyPassword());

			return connectionProperties;
		} catch (NullPointerException e) {
			logger.error("refinitiv connection properties not set", e);
			throw new SDCException(ExceptionId.SDC_NO_DATA_FOUND, "missing connection properties", 400);
		}
	}

	private ValueResult getValuationValueAtTime(String marketData, String tradeData, LocalDateTime valuationDate) {
		try {
			return marginCalculator.getValueAtEvaluationTime(marketData, tradeData, valuationDate);
		} catch (Exception e) {
			logger.error("unable to get valueAtTime for market data ", e);
			throw new SDCException(ExceptionId.SDC_VALUE_CALCULATION_ERROR, "error in MarginCalculator getValueAtTime");
		}
	}

	private BigDecimal getValue(String marketData, String tradeData) {
		try {
			return marginCalculator.getValue(marketData, tradeData).getValue();
		} catch (Exception e) {
			logger.error("unable to get value for market data ", e);
			throw new SDCException(ExceptionId.SDC_VALUE_CALCULATION_ERROR, "error in MarginCalculator getValue");
		}
	}

	private BigDecimal getMargin(String marketDataStart, String marketDataEnd, String tradeData) {
		try {
			return marginCalculator.getValue(marketDataStart, marketDataEnd, tradeData).getValue();
		} catch (Exception e) {
			logger.error("unable to get margin for market data ", e);
			throw new SDCException(ExceptionId.SDC_VALUE_CALCULATION_ERROR, "error in MarginCalculator getMargin");
		}
	}

	private void includeFixingsOfLastSettlement(RegularSettlementRequest regularSettlementRequest, MarketDataList newMarketDataList) {
		//searching for Fixings in the sdc product data XML
		Smartderivativecontract sdc = SDCXMLParser.unmarshalXml(regularSettlementRequest.getTradeData(), Smartderivativecontract.class);
		Optional<Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item> symbolsOptional = sdc.getSettlement().getMarketdata()
				.getMarketdataitems().getItem().stream().filter(
						item -> item.getType().get(0).equalsIgnoreCase(valuationConfig.getProductFixingType()))
				.findAny();
		List<String> symbols;

		if (symbolsOptional.isPresent()) {symbols = symbolsOptional.get().getSymbol();} else {
			logger.warn("no Fixings found in SDC product data XML, marketDataList not changed");
			return;
		}
		logger.info("found symbols in product data XML: {}", symbols);

		//matching symbols from product data xml to last settlement xml marketdataPoints
		Settlement settlementLast = SDCXMLParser.unmarshalXml(regularSettlementRequest.getSettlementLast(), Settlement.class);
		List<MarketDataPoint> marketDataPointsLastSettlement = settlementLast.getMarketData().getPoints().stream().filter(marketDataPoint -> {
			for (String symbol : symbols) {
				if (marketDataPoint.getId().equalsIgnoreCase(symbol)) return true;
			}
			return false;
		}).findAny().stream().toList();

		//add matching marketdataPoints to the new marketdata
		logger.info("add matching marketdataPoints to product symbols: {}", marketDataPointsLastSettlement);
		for (MarketDataPoint marketDataPoint : marketDataPointsLastSettlement) {
			newMarketDataList.add(marketDataPoint);
		}
	}
}
