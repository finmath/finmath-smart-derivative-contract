/*
 /*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 15 Oct 2018
 */

package net.finmath.smartcontract.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.api.SettlementValuationApi;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Controller for the settlement valuation REST service.
 * TODO Refactor try/catch once openapi can generate exception handling
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 * @author Dietmar Schnabel
 */
@RestController
public class SettlementValuationController implements SettlementValuationApi {
	
	private  static String curve1 = "NONE";
	private  static String curve2 = "NONE";
	private  static final HashMap<String, String> fpml = new HashMap<String, String>();
	private  static final HashMap<String, String> result = new HashMap<String, String>();
	
	private final Logger logger = LoggerFactory.getLogger(SettlementValuationController.class);
	
	/**
	 * Request mapping for the settlementvaluationForProductAsFPML
	 * 
	 * @param tradeAsFPML Trade FPML string
	 * @param tradeId Trade ID
	 * @param marketDataAsJson1 Market data Json string
	 * @param marketDataAsJson2 Market data Json string
	 * @return String Json representing the valuation.
	 */
	public ResponseEntity<String> settlementvaluationForProductAsFPMLOnChain(String marketDataAsJson1, String marketDataAsJson2, String tradeAsFPML, String tradeId)
		{
		/*LocalDate ld1 = SDCDateUtil.getDateFromJSON(marketDataAsJson1, SDCConstants.DATE_FORMAT_yyyyMMdd);
		LocalDate ld2 = SDCDateUtil.getDateFromJSON(marketDataAsJson2, SDCConstants.DATE_FORMAT_yyyyMMdd);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "SettlementValuationControllerTwoCurves");
		
		boolean b = true;
		if (SDCProperties.getProperty(SDCConstants.USE_CURVES_STRICT_BUS_DAYS).equals("TRUE")) {
			b = SDCDateUtil.isFollowingBusinessDays(ld1, ld2, new BusinessdayCalendarExcludingTARGETHolidays());
		}
		
		if(!b) {
			String message = "The dates " + ld1 + " and  " + ld2 + " are not T, T-1 following business dates!";
			logger.error(message);
			return new ResponseEntity<String>(message, responseHeaders, HttpStatus.BAD_REQUEST);
		}
		logger.info("Starting Margin Calculation with dates " + ld1 + " and  " + ld2);*/
		MarginCalculator marginCalculator = new MarginCalculator();

		ObjectMapper objectMapper = new ObjectMapper();

		ValuationResult valuationResult;
			try {
				valuationResult = marginCalculator.getValue(marketDataAsJson1, marketDataAsJson2, tradeAsFPML);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "MarginResult");
		return new ResponseEntity<String>(objectMapper.valueToTree(valuationResult).toString(), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Request mapping for test
	 * 
	 * @return String "Connect successful".
	 */
	public ResponseEntity<String> test()
		{
				
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		responseHeaders.add("Responded", "test");
		String totalResult =  "Connect successful";
				
		return new ResponseEntity<String>(totalResult, responseHeaders, HttpStatus.OK);
	}
}
