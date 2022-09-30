/*
 /*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 15 Oct 2018
 */

package net.finmath.smartcontract.service;

import net.finmath.smartcontract.api.ValuationApi;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.model.ValueRequest;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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

	/**
	 * Request mapping for the settlementvaluationForProductAsFPML
	 *
	 * @param marginRequest The request
	 * @return String Json representing the valuation.
	 */
	@Override
	public ResponseEntity<ValuationResult> margin(MarginRequest marginRequest) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "margin");

		ValuationResult margin = null;
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
	}

	/**
	 * Request mapping for the settlementvaluationForProductAsFPMLOneCurve
	 *
	 * @param valueRequest The request
	 * @return String Json representing the valuation.
	 */
	@Override
	public ResponseEntity<ValuationResult> value(ValueRequest valueRequest) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "value");

		ValuationResult value = null;
		try {
			MarginCalculator marginCalculator = new MarginCalculator();
			value = marginCalculator.getValue(valueRequest.getMarketData(), valueRequest.getTradeData());
			logger.info(value.toString());
			return ResponseEntity.ok(value);
		} catch (Exception e) {
			logger.error("Failed to calculate margin.");
			logger.info(value.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
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
		responseHeaders.add("Responded", "test");
		String totalResult = "Connect successful";

		return new ResponseEntity<String>(totalResult, responseHeaders, HttpStatus.OK);
	}
}
