package net.finmath.smartcontract.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.client.ValuationClient;
import net.finmath.smartcontract.model.MarginRequest;
import net.finmath.smartcontract.model.ValueRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Tests ValuationController / Valuation API Endpoint.
 */


@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = Application.class)
public class ValuationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ValuationController valuationController;

	@Test
	public void getMargin() throws Exception {

		final String marketDataStart = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEnd = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset2.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/smartderivativecontract-sample-swap.xml").readAllBytes(), StandardCharsets.UTF_8);

		final MarginRequest marginRequest = new MarginRequest().marketDataStart(marketDataStart).marketDataEnd(marketDataEnd).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(marginRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/margin").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json).characterEncoding("utf-8"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void getValue() throws Exception {

		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1_new_format.json").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/smartderivativecontract-sample-swap.xml").readAllBytes(), StandardCharsets.UTF_8);

		final ValueRequest valueRequest = new ValueRequest().marketData(marketData).tradeData(product).valuationDate(LocalDateTime.now().toString());

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(valueRequest);

		mockMvc.perform(MockMvcRequestBuilders
						.post("/valuation/value").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json).characterEncoding("utf-8"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
	}

}
