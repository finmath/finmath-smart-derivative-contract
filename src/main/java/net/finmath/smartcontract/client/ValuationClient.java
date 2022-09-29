package net.finmath.smartcontract.client;

import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.model.ValueRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Spring-boot application to demonstrate the ReST service for the valuation oracle,
 * the market data and trade files are taken from the resource folder.
 *
 * @author: Christian Fries
 * @author: Peter Kohl-Landgraf
 * @author Dietmar Schnabel
 */
public class ValuationClient {

	private static final String ENDPOINT_URL = "http://localhost:8080/valuation/margin";

	private static final Logger logger = LoggerFactory.getLogger(ValuationClient.class);

	public static void main(String[] args) throws Exception {
		final String marketDataStart = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEnd = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset2.json").readAllBytes(), StandardCharsets.UTF_8);
//		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/smartderivativecontract-sample-swap.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/vanilla-swap.xml").readAllBytes(), StandardCharsets.UTF_8);

		final MarginRequest marginRequest = new MarginRequest().marketDataStart(marketDataStart).marketDataEnd(marketDataEnd).tradeData(product).valuationDate(LocalDateTime.now().toString());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// create auth credentials
		String authString = "user_dz:password_dz";
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add("Authorization", "Basic " + base64Creds);

		RequestEntity<MarginRequest> requestEntity = new RequestEntity<MarginRequest>(marginRequest, headers, HttpMethod.POST, new URI(ENDPOINT_URL), MarginRequest.class);

		ResponseEntity<ValuationResult> response = new RestTemplate().exchange(requestEntity, ValuationResult.class);
		ValuationResult result = response.getBody();

		System.out.println("Received the valuation result:\n" + result);
	}
}
