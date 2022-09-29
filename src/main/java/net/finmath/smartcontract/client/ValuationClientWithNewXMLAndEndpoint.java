package net.finmath.smartcontract.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.service.Application;
import net.finmath.smartcontract.util.SDCConstants;
import net.finmath.smartcontract.util.SDCProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;


/**
 * Spring-boot application to demonstrate the REST service for the valuation oracle,
 * the market data and trade files are assumed to be in some local file system,
 * It uses URL_ENDPOINT_TWO_CURVES
 *
 * @author Dietmar Schnabel
 */
public class ValuationClientWithNewXMLAndEndpoint {

	private static String ENDPOINT_URL;

	private static final Logger logger = LoggerFactory.getLogger(ValuationClientWithNewXMLAndEndpoint.class);

	public static void main(String[] args) throws Exception {
		String connectionPropertiesFile = Application.class.getClassLoader().getResource("sdc.properties").getPath();
		URI test = new File(Application.class.getClassLoader().getResource("md_testset1.json").getPath()).toURI();
		SDCProperties.init(connectionPropertiesFile);
		String marketDataStart = Files.readString(Path.of(Application.class.getClassLoader().getResource("md_testset1.json").getPath()), StandardCharsets.UTF_8);
		String marketDataEnd = Files.readString(Path.of(Application.class.getClassLoader().getResource("md_testset1.json").getPath()), StandardCharsets.UTF_8);
		String product = Files.readString(Path.of(Application.class.getClassLoader().getResource("vanilla-swap.xml").getPath()), StandardCharsets.UTF_8);


		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		MarginRequest marginRequest = new MarginRequest();
		marginRequest.setMarketDataStart(marketDataStart);
		marginRequest.setMarketDataEnd(marketDataEnd);
		marginRequest.setTradeData(product);
		marginRequest.setValuationDate(LocalDateTime.now().toString());
		bodyMap.add("marginRequest", marginRequest);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// create auth credentials
		String authString = "user_dz:password_dz";
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add("Authorization", "Basic " + base64Creds);

		String url = "http://localhost:8080/valuation/margin";
		RequestEntity<MarginRequest> requestEntity = new RequestEntity<MarginRequest>(marginRequest, headers, HttpMethod.POST, new URI(url), MarginRequest.class);

		ResponseEntity<String> response = new RestTemplate().exchange(requestEntity, String.class);

		String body = response.getBody();

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(body).getAsJsonObject();

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

		// create auth credentials
		String authString = SDCProperties.getProperty(SDCConstants.USERNAME) + ":" + SDCProperties.getProperty(SDCConstants.PASSWORD);
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, requestEntity, String.class);

		return response;
	}
}
