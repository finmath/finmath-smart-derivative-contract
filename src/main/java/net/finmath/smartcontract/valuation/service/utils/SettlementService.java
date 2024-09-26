package net.finmath.smartcontract.valuation.service.utils;

import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.settlement.Settlement;
import net.finmath.smartcontract.settlement.SettlementGenerator;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;
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
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SettlementService {
	private static final Logger logger = LoggerFactory.getLogger(SettlementService.class);

	private final MarginCalculator marginCalculator = new MarginCalculator();

	private final RefinitivConfig refinitivConfig;
	private final ValuationConfig valuationConfig;

	public SettlementService(RefinitivConfig refinitivConfig, ValuationConfig valuationConfig) {
		this.refinitivConfig = refinitivConfig;
		this.valuationConfig = valuationConfig;
	}

	public RegularSettlementResult generateRegularSettlementResult(RegularSettlementRequest regularSettlementRequest) {

		SmartDerivativeContractDescriptor sdc = parseProductData(regularSettlementRequest.getTradeData());
		MarketDataList newMarketDataList = retrieveMarketData(sdc);
		String newMarketDataString = SDCXMLParser.marshalClassToXMLString(newMarketDataList);
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
				.settlementValue(getValue(newMarketDataString, regularSettlementRequest.getTradeData()))
				.settlementValuePrevious(settlementLast.getSettlementValue())
				.settlementTimeNext(settlementTimeNext)
				.settlementValueNext(settlementValueNext.getValue())
				.build();

		return new RegularSettlementResult()
				.generatedRegularSettlement(newSettlement)
				.currency(valuationConfig.getSettlementCurrency())
				.marginValue(margin)
				.valuationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
	}

	public InitialSettlementResult generateInitialSettlementResult(InitialSettlementRequest initialSettlementRequest) {

		SmartDerivativeContractDescriptor sdc = parseProductData(initialSettlementRequest.getTradeData());
		MarketDataList newMarketDataList = retrieveMarketData(sdc);
		String newMarketDataString = SDCXMLParser.marshalClassToXMLString(newMarketDataList);

		ZonedDateTime settlementTimeNext = ZonedDateTime.now().plusDays(1);

		ValueResult settlementValueNext = getValuationValueAtTime(
				newMarketDataString, initialSettlementRequest.getTradeData(), settlementTimeNext.toLocalDateTime());

		List<BigDecimal> marginLimits = new ArrayList<>();
		sdc.getCounterparties().forEach(party -> marginLimits.add(BigDecimal.valueOf(sdc.getMarginAccount(party.getId()))));

		String newSettlement = new SettlementGenerator()
				.generateInitialSettlementXml(newMarketDataString, sdc)
				.marginLimits(marginLimits)
				.settlementValue(getValue(newMarketDataString, initialSettlementRequest.getTradeData()))
				//.settlementValuePrevious(BigDecimal.ZERO)
				.settlementTimeNext(settlementTimeNext)
				.settlementValueNext(settlementValueNext.getValue())
				.build();

		return new InitialSettlementResult()
				.generatedInitialSettlement(newSettlement)
				.currency(valuationConfig.getSettlementCurrency())
				.marginValue(BigDecimal.ZERO)
				.valuationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
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

		if (sdc.getMarketDataProvider().equals(valuationConfig.getLiveMarketDataProvider()) && valuationConfig.isLiveMarketData()) {
			marketDataList.set(MarketDataGeneratorLauncher.instantiateMarketDataGeneratorWebsocket(initConnectionProperties(), sdc));
		} else {
			//includes provider internal or no liveMarketData activated
			final io.reactivex.rxjava3.functions.Consumer<MarketDataList> marketDataWriter = marketDataList::set;
			MarketDataGeneratorScenarioList marketDataServiceScenarioList = new MarketDataGeneratorScenarioList();
			marketDataServiceScenarioList.asObservable().subscribe(marketDataWriter,                        //onNext
					throwable -> logger.error("unable to generate marketData from files ", throwable),        //onError
					() -> logger.info("on complete, simulated marketData generated from files"));            //onComplete
		}
		return marketDataList.get();
	}

	private Properties initConnectionProperties() {
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
	}

	private ValueResult getValuationValueAtTime(String marketData, String tradeData, LocalDateTime valuationDate){
		try {
			return marginCalculator.getValueAtEvaluationTime(marketData, tradeData, valuationDate);
		} catch (Exception e) {
			logger.error("unable to get valueAtTime for market data ", e);
			throw new SDCException(ExceptionId.SDC_VALUE_CALCULATION_ERROR, "error in MarginCalculator getValueAtTime");		}
	}

	private BigDecimal getValue(String marketData, String tradeData){
		try {
			return marginCalculator.getValue(marketData, tradeData).getValue();
		} catch (Exception e) {
			logger.error("unable to get value for market data ", e);
			throw new SDCException(ExceptionId.SDC_VALUE_CALCULATION_ERROR, "error in MarginCalculator getValue");
		}
	}

	private BigDecimal getMargin(String marketDataStart, String marketDataEnd, String tradeData){
		try {
			return marginCalculator.getValue(marketDataStart, marketDataEnd, tradeData).getValue();
		} catch (Exception e) {
			logger.error("unable to get margin for market data ", e);
			throw new SDCException(ExceptionId.SDC_VALUE_CALCULATION_ERROR, "error in MarginCalculator getMargin");
		}
	}
}
