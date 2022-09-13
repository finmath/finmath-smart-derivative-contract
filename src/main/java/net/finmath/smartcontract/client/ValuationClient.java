package net.finmath.smartcontract.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.finmath.smartcontract.util.SDCConstants;
import net.finmath.smartcontract.util.SDCProperties;
import net.finmath.smartcontract.util.SDCStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;


/**
 * Spring-boot application to demonstrate the REST service for the valuation oracle,
 * the market data and trade files are assumed to be in some local file system,
 * It uses URL_ENDPOINT_TWO_CURVES
 *
 * @author Dietmar Schnabel
 */
public class ValuationClient {
	
	private static String ENDPOINT_URL;

	private static final Logger logger = LoggerFactory.getLogger(ValuationClient.class);
	
	public static void main(String[] args) {
		SDCStarter.init(args);
		
		logger.info("Starting Valuation Client");
		ValuationClient valuationClient = new ValuationClient();

		String mdFileStart = SDCProperties.getProperty(SDCConstants.DATA_PATH) + File.separator + SDCProperties.getProperty(SDCConstants.MARKET_DATA_FILE_HEADER);
		String jsonFile1 = mdFileStart + SDCProperties.getProperty(SDCConstants.JSON_FILE_1);
		String jsonFile2 = mdFileStart + SDCProperties.getProperty(SDCConstants.JSON_FILE_2);
		String fpmlFile1 = SDCProperties.getProperty(SDCConstants.DATA_PATH) + File.separator + SDCProperties.getProperty(SDCConstants.FPML_FILE_1);
		ENDPOINT_URL = SDCProperties.getProperty(SDCConstants.URL_ENDPOINT_TWO_CURVES);
		
		logger.info("Using REST endpoint: " + ENDPOINT_URL );
		
		if(!Files.exists(new File(jsonFile1).toPath())) {
			logger.error(jsonFile1 + " does not exist!!");
			System.exit(1);
		} else {
			logger.info("Found: " + jsonFile1);
		}
		if(!Files.exists(new File(jsonFile2).toPath())) {
			logger.error(jsonFile2 + " does not exist!!");
			System.exit(1);
		} else {
			logger.info("Found: " + jsonFile2);
		}
		if(!Files.exists(new File(fpmlFile1).toPath())) {
			logger.error(fpmlFile1 + " does not exist!!");
			System.exit(1);
		} else {
			logger.info("Found: " + fpmlFile1);
		}
		
		FileSystemResource marketDataAsJson1 = new FileSystemResource(new File(jsonFile1));
		FileSystemResource marketDataAsJson2 = new FileSystemResource(new File(jsonFile2));
		FileSystemResource tradeAsFPML  = new FileSystemResource(new File(fpmlFile1));

		ResponseEntity<String> response = valuationClient.getValuation(marketDataAsJson1, marketDataAsJson2, tradeAsFPML);
		
				
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(response.getBody()).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		logger.info(gson.toJson(json));
	}
	
	private ResponseEntity<String> getValuation(FileSystemResource marketDataAsJson1, FileSystemResource marketDataAsJson2, FileSystemResource tradeAsFPML) {

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		
		bodyMap.add(SDCConstants.MARKET_DATA_AS_JSON_1, marketDataAsJson1);
		bodyMap.add(SDCConstants.MARKET_DATA_AS_JSON_2, marketDataAsJson2);
		bodyMap.add(SDCConstants.TRADE_AS_FPML, tradeAsFPML);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(ENDPOINT_URL,HttpMethod.POST, requestEntity, String.class);
		
		return response;
    }
}
