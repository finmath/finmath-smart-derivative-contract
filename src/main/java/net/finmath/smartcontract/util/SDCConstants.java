/*
 * Contains common string constants and property file content.
 *
 * @author Dietmar Schnabel
 */
package net.finmath.smartcontract.util;

import java.io.File;


/**
 * Static strings and property names.
 */
public class SDCConstants {

	public static String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
	public static String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
	public static String DATE_FORMAT_yyyy_MM_ddThh_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
	public static String SDC_HOME = "SDC_HOME";
	private static final String EVENT_HANDLER_PATH_CONST = "net.finmath.handler";
	public static String MARKET_DATA_AS_JSON_1 = "marketDataAsJson1";
	public static String MARKET_DATA_AS_JSON_2 = "marketDataAsJson2";
	public static String TRADE_AS_FPML = "tradeAsFPML";

	public static String XPATH_FPML_SWAP_LEG1 = "/dataDocument/trade/swap/swapStream[ns:payerPartyReference[contains(@href,'party1')]]";
	public static String XPATH_FPML_SWAP_LEG2 = "/dataDocument/trade/swap/swapStream[ns:payerPartyReference[contains(@href,'party2')]]";
	public static String XPATH_FPML_SWAP_MATURITY_DATE = "/calculationPeriodDates/terminationDate/unadjustedDate";
	public static String XPATH_FPML_SWAP_START_DATE = "/calculationPeriodDates/effectiveDate/unadjustedDate";
	public static String XPATH_FPML_PARTY1 = "/dataDocument/party[@id='party1']/partyId";
	public static String XPATH_FPML_PARTY2 = "/dataDocument/party[@id='party2']/partyId";
	public static String XPATH_FPML_PARTY1_MARGINM = "/dataDocument/party[@id='party1']/marginM";
	public static String XPATH_FPML_PARTY1_MARGINP = "/dataDocument/party[@id='party1']/marginP";
	public static String XPATH_FPML_PARTY2_MARGINM = "/dataDocument/party[@id='party2']/marginM";
	public static String XPATH_FPML_PARTY2_MARGINP = "/dataDocument/party[@id='party2']/marginP";
	public static String XPATH_FPML_PARTY1_KEY = "/dataDocument/party[@id='party1']/privateKey";
	public static String XPATH_FPML_PARTY2_KEY = "/dataDocument/party[@id='party2']/privateKey";
	public static String XPATH_FPML_PARTY1_ADDRESS = "/dataDocument/party[@id='party1']/address";
	public static String XPATH_FPML_PARTY2_ADDRESS = "/dataDocument/party[@id='party2']/address";
	public static String XPATH_FPML_TRADE_DATE = "/dataDocument/trade/tradeHeader/tradeDate";

	public static String FPML_DEFAULT_NAMESPACE = "http://www.fpml.org/FpML-5/master";

	// SDC property file content.


	public static String JSON_FILE_1 = "JSON_FILE_1";
	public static String JSON_FILE_2 = "JSON_FILE_2";
	public static String FPML_FILE_1 = "FPML_FILE_1";
	public static String URL_ENDPOINT_TWO_CURVES = "URL_ENDPOINT_TWO_CURVES";
	public static String DATA_PATH = "DATA_PATH";
	public static String DATA_PATH_DEFAULT = System.getenv(SDCConstants.SDC_HOME) + File.separator + "data";
	public static String EVENT_HANDLER_PATH = "EVENT_HANDLER_PATH";
	public static String EVENT_HANDLER_PATH_DEFAULT = EVENT_HANDLER_PATH_CONST;
	public static String MARKET_DATA_FILE_HEADER = "MARKET_DATA_FILE_HEADER";
	public static String MARKET_DATA_FILE_HEADER_DEFAULT = "Curves_";

	public static String USE_CURVES_STRICT_BUS_DAYS = "USE_CURVES_STRICT_BUS_DAYS";
	public static String URL_TIMEOUT = "URL_TIMEOUT";
	public static String URL_TIMEOUT_DEFAULT = "2000";
	public static String FPML_NAMESPACE = "FPML_NAMESPACE";

	public static String USERNAME = "USERNAME";
	public static String PASSWORD = "PASSWORD";

}
