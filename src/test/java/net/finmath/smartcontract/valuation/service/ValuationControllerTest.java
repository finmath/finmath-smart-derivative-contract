package net.finmath.smartcontract.valuation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.model.LegacyMarginRequest;
import net.finmath.smartcontract.model.LegacyValueRequest;
import net.finmath.smartcontract.valuation.service.config.BasicAuthWebSecurityConfiguration;
import net.finmath.smartcontract.valuation.service.config.MockUserAuthConfig;
import net.finmath.smartcontract.valuation.service.controllers.ValuationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Tests ValuationController / Valuation API Endpoint.
 */
//@ExtendWith(SpringExtension.class)	// these new annotations are needed to avoid conflict between WebFlux and SpringMVC configs, as now spring.webmvc needs to be explicitly on the classpath (because CORS)
@SpringBootTest(classes = {ValuationController.class, Application.class},
		webEnvironment = SpringBootTest.WebEnvironment.MOCK, // explicitly enable Mockito
		useMainMethod = SpringBootTest.UseMainMethod.ALWAYS) // use the same ApplicationContext as the regular (non test) server
@ContextConfiguration(classes = {BasicAuthWebSecurityConfiguration.class, Application.class, MockUserAuthConfig.class})
@ComponentScan("net.finmath.smartcontract.marketdata.database")
@AutoConfigureMockMvc
@WebAppConfiguration
@ActiveProfiles("test")
public class ValuationControllerTest {

	final String productXMLFile = "net.finmath.smartcontract.product.xml/smartderivativecontract.xml";

	@Test
	@WithUserDetails("user1")    // testing now uses more of the server environment, including security. Tests would fail if requests are not authenticated.
	public void getMargin(@Autowired MockMvc mockMvc) throws Exception {

		final String marketDataStart = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEnd = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset2.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream(productXMLFile).readAllBytes(), StandardCharsets.UTF_8);

		final LegacyMarginRequest marginRequest = new LegacyMarginRequest().marketDataStart(marketDataStart).marketDataEnd(marketDataEnd).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(marginRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/legacy/margin")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(json)
						.characterEncoding("utf-8")
				)
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
	}

	@Test
	@WithUserDetails("user1")
	public void getValue(@Autowired MockMvc mockMvc) throws Exception {

		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream(productXMLFile).readAllBytes(), StandardCharsets.UTF_8);

		final LegacyValueRequest valueRequest = new LegacyValueRequest().marketData(marketData).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(valueRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/legacy/value").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json).characterEncoding("utf-8"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
	}

}
