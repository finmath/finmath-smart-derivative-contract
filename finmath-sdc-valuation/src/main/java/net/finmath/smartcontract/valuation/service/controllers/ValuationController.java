/*
 /*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 15 Oct 2018
 */

package net.finmath.smartcontract.valuation.service.controllers;

import net.finmath.smartcontract.api.ValuationApi;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the settlement valuation REST service.
 * TODO Refactor try/catch once openapi can generate exception handling
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 */
@RestController
public class ValuationController implements ValuationApi {

	private final Logger logger = LoggerFactory.getLogger(ValuationController.class);
	private static final String FAILED_CALCULATION = "Failed to calculate value.";
	private static final String RESPONDED = "Responded";

	/**
	 * Request mapping for the settlementvaluationForProductAsFPML
	 *
	 //* @param marginRequest The request
	 //* @return String Json representing the valuation.
	 */
	/*@Override
	@Deprecated
	public ResponseEntity<MarginResult> legacyMargin(LegacyMarginRequest marginRequest) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "margin");

		MarginResult margin = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			margin = marginCalculator.getValue(marginRequest.getMarketDataStart(), marginRequest.getMarketDataEnd(), marginRequest.getTradeData());
			logger.info(margin.toString());
			return ResponseEntity.ok(margin);
		} catch (Exception e) {
			logger.error("Failed to calculate margin.");
			logger.info(marginRequest.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}*/

	/**
	 * Request mapping for the settlementvaluationForProductAsFPMLOneCurve
	 *
	 //* @param valueRequest The request
	 //* @return String Json representing the valuation.
	 */
	/*@Override
	@Deprecated
	public ResponseEntity<ValueResult> legacyValue(LegacyValueRequest valueRequest) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "value");

		ValueResult value = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			value = marginCalculator.getValue(valueRequest.getMarketData(), valueRequest.getTradeData());
			logger.info(value.toString());
			return ResponseEntity.ok(value);
		} catch (Exception e) {
			logger.error(FAILEDCALCULATION);
			logger.info(value.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}*/

	@Override
	public ResponseEntity<MarginResult> margin(MarginRequest marginRequest) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(RESPONDED, "margin");

		MarginResult margin = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			margin = marginCalculator.getValue(marginRequest.getMarketDataStart(), marginRequest.getMarketDataEnd(), marginRequest.getTradeData());
			logger.info(margin.toString());
			return ResponseEntity.ok(margin);
		} catch (SAXException e){
			logger.error("invalid trade data xml");
			throw new SDCException(ExceptionId.SDC_INVALID_TRADE_DATA, e.getMessage());
		} catch (Exception e) {
			logger.error("Failed to calculate margin.", e);
			logger.debug(marginRequest.toString());
			throw new SDCException(ExceptionId.SDC_MARGIN_CALCULATION_ERROR, e.getMessage());
		}
	}

	@Override
	public ResponseEntity<ValueResult> value(ValueRequest valueRequest) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(RESPONDED, "value");
		ValueResult value = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			value = marginCalculator.getValue(valueRequest.getMarketData(), valueRequest.getTradeData());
			logger.info(value.toString());
			return ResponseEntity.ok(value);
		} catch (Exception e) {
			logger.error(FAILED_CALCULATION, e);
			throw new SDCException(ExceptionId.SDC_MARGIN_CALCULATION_ERROR, e.getMessage());
		}
	}

	@Override
	public ResponseEntity<ValueResult> valueAtTime(ValueRequest valueRequest) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(RESPONDED, "valueAtTime");
		ValueResult value = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
			LocalDateTime evaluationTime = LocalDateTime.parse(valueRequest.getValuationDate(), formatter);
			value = marginCalculator.getValueAtEvaluationTime(valueRequest.getMarketData(), valueRequest.getTradeData(),evaluationTime);
			logger.info(value.toString());
			return ResponseEntity.ok(value);
		} catch (Exception e) {
			logger.error(FAILED_CALCULATION, e);
			throw new SDCException(ExceptionId.SDC_MARGIN_CALCULATION_ERROR, e.getMessage());
		}
	}


	@Override
	public ResponseEntity<ValueResult> testProductValue(MultipartFile tradeData) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(RESPONDED, "value");

		ValueResult value = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml").readAllBytes(), StandardCharsets.UTF_8);
			value = marginCalculator.getValue(marketData, new String(tradeData.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
			logger.info(value.toString());
			return ResponseEntity.ok(value);
		} catch (Exception e) {
			logger.error(FAILED_CALCULATION, e);
			throw new SDCException(ExceptionId.SDC_MARGIN_CALCULATION_ERROR, e.getMessage());
		}
	}

	/**
	 * Request mapping for test
	 *
	 * @return String "Connect successful".
	 */
	public ResponseEntity<String> test() {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		responseHeaders.add(RESPONDED, "test");
		String totalResult = "Connect successful";

		return new ResponseEntity<>(totalResult, responseHeaders, HttpStatus.OK);
	}


}
