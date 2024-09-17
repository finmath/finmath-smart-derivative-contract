package net.finmath.smartcontract.valuation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.model.ValueRequest;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.client.ValuationClient;
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
import java.time.OffsetDateTime;

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
class ValuationControllerTest {

	final String productXMLFile = "net.finmath.smartcontract.product.xml/smartderivativecontract.xml";

	@Test
	@WithUserDetails("user1")    // testing now uses more of the server environment, including security. Tests would fail if requests are not authenticated.
	void getMargin(@Autowired MockMvc mockMvc) throws Exception {

		final String marketDataStartXml = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml").readAllBytes(), StandardCharsets.UTF_8);
//		final MarketDataList marketDataStart = SDCXMLParser.unmarshalXml(marketDataStartXml, MarketDataList.class);
		final String marketDataEndXml = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset2.xml").readAllBytes(), StandardCharsets.UTF_8);
//		final MarketDataList marketDataEnd = SDCXMLParser.unmarshalXml(marketDataEndXml, MarketDataList.class);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream(productXMLFile).readAllBytes(), StandardCharsets.UTF_8);

		final MarginRequest marginRequest = new MarginRequest().marketDataStart(marketDataStartXml).marketDataEnd(marketDataEndXml).tradeData(product).valuationDate(OffsetDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
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
	void getValue(@Autowired MockMvc mockMvc) throws Exception {

		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream(productXMLFile).readAllBytes(), StandardCharsets.UTF_8);

		final ValueRequest valueRequest = new ValueRequest().marketData(marketData).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(valueRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/value").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json).characterEncoding("utf-8"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
	}

}
