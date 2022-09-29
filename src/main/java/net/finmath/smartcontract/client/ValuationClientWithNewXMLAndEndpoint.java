package net.finmath.smartcontract.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.service.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
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
 * @author: Christian Fries
 * @author: Peter Kohl-Landgraf
 *
 */
public class ValuationClientWithNewXMLAndEndpoint {

	private static final String ENDPOINT_URL = "http://localhost:8080/valuation/margin";

	private static final Logger logger = LoggerFactory.getLogger(ValuationClientWithNewXMLAndEndpoint.class);

	public static void main(String[] args) throws Exception {
		final String marketDataStart = Files.readString(Path.of(new File(Application.class.getClassLoader().getResource("md_testset1.json").getPath()).toURI()), StandardCharsets.UTF_8);
		final String marketDataEnd = Files.readString(Path.of(new File(Application.class.getClassLoader().getResource("md_testset2.json").getPath()).toURI()), StandardCharsets.UTF_8);
		final String product = Files.readString(Path.of(new File(Application.class.getClassLoader().getResource("vanilla-swap.xml").getPath()).toURI()), StandardCharsets.UTF_8);

		final MarginRequest marginRequest = new MarginRequest();
		marginRequest.setMarketDataStart(marketDataStart);
		marginRequest.setMarketDataEnd(marketDataEnd);
		marginRequest.setTradeData(product);
		marginRequest.setValuationDate(LocalDateTime.now().toString());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// create auth credentials
		String authString = "user_dz:password_dz";
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add("Authorization", "Basic " + base64Creds);

		RequestEntity<MarginRequest> requestEntity = new RequestEntity<MarginRequest>(marginRequest, headers, HttpMethod.POST, new URI(ENDPOINT_URL), MarginRequest.class);

		ResponseEntity<String> response = new RestTemplate().exchange(requestEntity, String.class);

		/* Parse Response to ValuationResult-Object and write to json */
		final String bodyasStr = response.getBody();
		final ValuationResult resultObject = new ObjectMapper()
				.readerFor(ValuationResult.class)
				.readValue(bodyasStr);

		final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		final String resultAsJson = ow.writeValueAsString(resultObject);

		logger.info(resultAsJson);
	}
}
