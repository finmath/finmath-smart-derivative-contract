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
import net.finmath.smartcontract.util.SDCConstants;
import net.finmath.smartcontract.util.SDCDateUtil;
import net.finmath.smartcontract.util.SDCProperties;
import net.finmath.smartcontract.valuation.MarginCalculator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

/**
 * Controller for the settlement valuation REST service.
 * TODO Refactor try/catch once openapi can generate exception handling
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 * @author Dietmar Schnabel
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
	 * @param marketData    Market data Json file1
	 * @param tradeData     Trade FPML file
	 * @param valuationDate The date to be used in valuation.
	 * @return String Json representing the valuation.
	 */
	public ResponseEntity<String> value(String marketData, String tradeData, String valuationDate) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "SettlementValuationControllerOneCurve");

		LocalDate marketDataDate = SDCDateUtil.getDateFromJSON(marketData, SDCConstants.DATE_FORMAT_yyyyMMdd);
		String currentDateString = SDCDateUtil.getStringFromDate(marketDataDate, SDCConstants.DATE_FORMAT_yyyyMMdd);

		LocalDate previousDate = SDCDateUtil.getPreviousBusinessDay(marketDataDate, new BusinessdayCalendarExcludingTARGETHolidays());
		logger.info("T-1 = " + previousDate);

		String previousDateString = SDCDateUtil.getStringFromDate(previousDate, SDCConstants.DATE_FORMAT_yyyyMMdd);
		String FileHeader = SDCProperties.getProperty(SDCConstants.DATA_PATH) + File.separator + SDCProperties.getProperty(SDCConstants.MARKET_DATA_FILE_HEADER);
		String previousJson = FileHeader + previousDateString + ".json";
		String currentJson = FileHeader + currentDateString + ".json";

		File previousFile = new File(previousJson);
		File currentFile = new File(currentJson);

		if (!Files.exists(previousFile.toPath())) {
			String message = "The file " + previousJson + " does not exist!";
			logger.error(message);
			return new ResponseEntity<String>(message, responseHeaders, HttpStatus.BAD_REQUEST);
		}
		logger.info("Previous File = " + previousFile);
		String json2String = null;
		try {
			json2String = new String(Files.readAllBytes(previousFile.toPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LocalDate ld2 = SDCDateUtil.getDateFromJSON(json2String, SDCConstants.DATE_FORMAT_yyyyMMdd);


		logger.info("Starting Margin Calculation with dates " + marketDataDate + " and  " + ld2);
		MarginCalculator marginCalculator = new MarginCalculator();

		if (logger.isDebugEnabled()) {
			logger.debug("json1bytes: " + marketData);
			logger.debug("json2bytes: " + json2String);
			logger.debug("fpmlbytes: " + tradeData);
		}
		try {
			marginCalculator.getValue(marketData, json2String, tradeData);
		} catch (Exception e) {
			logger.error("Failed to calculate margin.");
			e.printStackTrace();
		}

		String resultJSON = marginCalculator.getContractValuationAsJSON();
		logger.info(resultJSON);
		try {
			Files.write(currentFile.toPath(), marketData.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<String>(resultJSON, responseHeaders, HttpStatus.OK);
	}

	/**
	 * Request mapping for the settlementvaluationForProductAsFPML
	 *
	 * @param tradeAsFPML       Trade FPML file
	 * @param marketDataAsJson1 Market data Json file1
	 * @param marketDataAsJson2 Market data Json file2
	 * @return String Json representing the valuation.
	 */
	public ResponseEntity<String> settlementvaluationForProductAsFPML(MultipartFile marketDataAsJson1, MultipartFile marketDataAsJson2, MultipartFile tradeAsFPML) {
		String json1String = null;
		try {
			json1String = new String(marketDataAsJson1.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String json2String = null;
		try {
			json2String = new String(marketDataAsJson2.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String fpmlString = null;
		try {
			fpmlString = new String(tradeAsFPML.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		LocalDate ld1 = SDCDateUtil.getDateFromJSON(json1String, SDCConstants.DATE_FORMAT_yyyyMMdd);
		LocalDate ld2 = SDCDateUtil.getDateFromJSON(json2String, SDCConstants.DATE_FORMAT_yyyyMMdd);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "SettlementValuationControllerTwoCurves");
		boolean b = true;
		if (SDCProperties.getProperty(SDCConstants.MARKET_DATA_FILE_HEADER).equals("TRUE")) {
			b = SDCDateUtil.isFollowingBusinessDays(ld1, ld2, new BusinessdayCalendarExcludingTARGETHolidays());
		}

		if (!b) {
			String message = "The dates " + ld1 + " and  " + ld2 + " are not T, T-1 following business dates!";
			logger.error(message);
			return new ResponseEntity<String>(message, responseHeaders, HttpStatus.BAD_REQUEST);
		}
		logger.info("Starting Margin Calculation with dates " + ld1 + " and  " + ld2);
		MarginCalculator marginCalculator = new MarginCalculator();


		try {
			marginCalculator.getValue(json1String, json2String, fpmlString);
		} catch (Exception e) {
			logger.error("Failed to calculate margin.");
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("json1bytes: " + json1String);
			logger.debug("json2bytes: " + json2String);
			logger.debug("fpmlbytes: " + fpmlString);
		}
		String resultJSON = marginCalculator.getContractValuationAsJSON();
		logger.info(resultJSON);
		return new ResponseEntity<String>(resultJSON, responseHeaders, HttpStatus.OK);
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
