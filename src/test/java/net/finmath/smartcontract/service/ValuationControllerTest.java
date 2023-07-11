package net.finmath.smartcontract.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.client.ValuationClient;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.ValueRequest;
import net.finmath.smartcontract.service.config.BasicAuthWebSecurityConfiguration;
import net.finmath.smartcontract.service.config.MockUserAuthConfig;
import net.finmath.smartcontract.service.controllers.ValuationController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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


	@Test
	@WithUserDetails("user1")	// testing now uses more of the server environment, including security. Tests would fail if requests are not authenticated.
	public void getMargin(@Autowired MockMvc mockMvc) throws Exception {

		final String marketDataStart = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEnd = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset2.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/smartderivativecontract-sample-swap.xml").readAllBytes(), StandardCharsets.UTF_8);

		final MarginRequest marginRequest = new MarginRequest().marketDataStart(marketDataStart).marketDataEnd(marketDataEnd).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(marginRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/margin")
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

		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/smartderivativecontract-sample-swap.xml").readAllBytes(), StandardCharsets.UTF_8);

		final ValueRequest valueRequest = new ValueRequest().marketData(marketData).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(valueRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/value").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json).characterEncoding("utf-8"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
	}

}
