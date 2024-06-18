package net.finmath.smartcontract.valuation.client;

import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.MarginResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Spring-boot application to demonstrate the ReST service for the valuation oracle,
 * the market data and trade files are taken from the resource folder.
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 * @author Dietmar Schnabel
 */
public class ValuationClient {
	private static final Logger logger = LoggerFactory.getLogger(ValuationClient.class);
	private static final String BASIC = "Basic ";

	public static void main(String[] args) throws Exception {
		String url = "http://localhost:8080";
		String authString = "user1:password1";

		if (args.length != 2) {
			logger.info("Usage: ValuationClient <url> <user>:<password>");
		}

		if (args.length == 2) {
			authString = args[1];
		} else {
			logger.info("Using default credentials {}", authString);
		}

		if (args.length >= 1) {
			url = args[0];
		} else {
			logger.info("Using default endpoint {}", url);
		}

		final String marketDataStartXml = new String(Objects.requireNonNull(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml")).readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEndXml = new String(Objects.requireNonNull(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset2.xml")).readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(Objects.requireNonNull(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/product/xml/smartderivativecontract.xml")).readAllBytes(), StandardCharsets.UTF_8);

		final MarginRequest marginRequest = new MarginRequest().marketDataStart(marketDataStartXml).marketDataEnd(marketDataEndXml).tradeData(product).valuationDate("");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// create auth credentials
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add(HttpHeaders.AUTHORIZATION, BASIC + base64Creds);

		RequestEntity<MarginRequest> requestEntity = new RequestEntity<>(marginRequest, headers, HttpMethod.POST, new URI(url + "/valuation/margin"), MarginRequest.class);

		ResponseEntity<MarginResult> response = new RestTemplate().exchange(requestEntity, MarginResult.class);
		MarginResult result = response.getBody();

		logger.info("Received the valuation result:\n{}", result);

		printInfoGit(url, authString);
		printInfoFinmath(url, authString);
	}

	private static void printInfoGit(String url, String authString) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// create auth credentials
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add(HttpHeaders.AUTHORIZATION, BASIC + base64Creds);

		RequestEntity<String> requestEntity = new RequestEntity<>(null, headers, HttpMethod.GET, new URI(url + "/info/git"), String.class);

		ResponseEntity<String> response = new RestTemplate().exchange(requestEntity, String.class);

		logger.info("git status");
		logger.info(response.getBody());
	}

	private static void printInfoFinmath(String url, String authString) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// create auth credentials
		String base64Creds = Base64.getEncoder().encodeToString(authString.getBytes());
		headers.add(HttpHeaders.AUTHORIZATION, BASIC + base64Creds);

		RequestEntity<String> requestEntity = new RequestEntity<>(null, headers, HttpMethod.GET, new URI(url + "/info/finmath"), String.class);

		ResponseEntity<String> response = new RestTemplate().exchange(requestEntity, String.class);

		logger.info("finmath-lib version");
		logger.info(response.getBody());
	}
}
