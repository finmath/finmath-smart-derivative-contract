package net.finmath.smartcontract.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.service.config.BasicAuthWebSecurityConfiguration;
import net.finmath.smartcontract.service.config.MockUserAuthConfig;
import net.finmath.smartcontract.service.controllers.PlainSwapEditorController;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Tests EditorController / Editor API Endpoint.
 */
@ExtendWith(SpringExtension.class)
// these new annotations are needed to avoid conflict between WebFlux and SpringMVC configs, as now spring.webmvc needs to be explicitly on the classpath (because CORS)
@SpringBootTest(classes = {PlainSwapEditorController.class, Application.class}, // also, the environment was defined for the JUnit4 test runner, but the Spring Boot 3.x.y line uses JUnit5
        webEnvironment = SpringBootTest.WebEnvironment.MOCK, // <--- explicitly enable Mockito
        useMainMethod = SpringBootTest.UseMainMethod.ALWAYS) // <--- use the same ApplicationContext as the regular (non-test) server
@ContextConfiguration(classes = {BasicAuthWebSecurityConfiguration.class, Application.class, MockUserAuthConfig.class})
@AutoConfigureMockMvc
@AutoConfigureJsonTesters // Junit cannot mind his own business and will disable Spring Jackson autoconfig
@AutoConfigureJson
@Disabled("Class disabled because the test would depend on an absolute path.")
public class PlainSwapEditorControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(PlainSwapEditorControllerTest.class);

    @Test
    @WithUserDetails("user1")
    @Disabled("Test disabled because the test would depend on an absolute path.")
    void evaluateFromEditorTest_whenMismatchWithReferenceFails(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {

        final String marketData = new ClassPathResource("net.finmath.smartcontract.client" + File.separator + "md_testset2.json").getContentAsString(StandardCharsets.UTF_8);
        final String product = new ClassPathResource("references" + File.separator + "template2.xml").getContentAsString(StandardCharsets.UTF_8);
        final String fullSymbolListFromTemplate = new ClassPathResource("references" + File.separator + "template2_symbolslist.json").getContentAsString(StandardCharsets.UTF_8);
        final Counterparty firstCounterparty = new Counterparty().baseUrl("aaa").bicCode("ABCDXXXX").fullName("PartyDoubleTest");
        final PaymentFrequency floatingPaymentFrequency = new PaymentFrequency().period("M").periodMultiplier(6).fullName("Semiannual");
        final PaymentFrequency fixedPaymentFrequency = new PaymentFrequency().period("Y").periodMultiplier(1).fullName("Annual");
        final Counterparty secondCounterparty = new Counterparty().baseUrl("bbb").bicCode("EFDGXXXX").fullName("PartyTest");
        final PlainSwapOperationRequest plainSwapOperationRequest = new PlainSwapOperationRequest().firstCounterparty(firstCounterparty).secondCounterparty(secondCounterparty).marginBufferAmount(30000.0).terminationFeeAmount(10000.0).currency("EUR").tradeDate(OffsetDateTime.of(LocalDateTime.of(2022, Month.SEPTEMBER, 5, 12, 0), ZoneOffset.UTC)).effectiveDate(OffsetDateTime.of(LocalDateTime.of(2022, Month.SEPTEMBER, 7, 12, 0), ZoneOffset.UTC)).terminationDate(OffsetDateTime.of(LocalDateTime.of(2032, Month.SEPTEMBER, 7, 12, 0), ZoneOffset.UTC)).fixedPayingParty(secondCounterparty).floatingPayingParty(firstCounterparty).fixedRate(3.95).fixedDayCountFraction("30E/360").fixedPaymentFrequency(fixedPaymentFrequency).floatingRateIndex("EUR-LIBOR-BBA").floatingDayCountFraction("ACT/360").floatingFixingDayOffset(-2).floatingPaymentFrequency(floatingPaymentFrequency).notionalAmount(1000000.00).valuationSymbols(objectMapper.readerForListOf(JsonMarketDataItem.class).readValue(fullSymbolListFromTemplate)).currentGenerator("generators/eur_euribor_y_s.xml");
        String jsonPlainSwapOperationRequest = objectMapper.writeValueAsString(plainSwapOperationRequest);

        MvcResult serverResponse = mockMvc.perform(MockMvcRequestBuilders.post("/plain-swap-editor/evaluate-from-editor").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(jsonPlainSwapOperationRequest).characterEncoding("utf-8")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        ValueResult valueResultFromRequest = objectMapper.readValue(serverResponse.getResponse().getContentAsString(), ValueResult.class);
        ValueResult valueResultFromLocalStorage = (new MarginCalculator()).getValue(marketData, product);
        logger.info("Known value is 95313.13");
        logger.info("Value result from generated request is: " + valueResultFromRequest.getValue().doubleValue());
        logger.info("Value result from locally stored data is: " + valueResultFromLocalStorage.getValue().doubleValue());
        Assertions.assertEquals(Math.abs(valueResultFromLocalStorage.getValue().doubleValue() - 95313.13) + Math.abs(Math.abs(valueResultFromRequest.getValue().doubleValue()) - 95313.13) + Math.abs(Math.abs(valueResultFromLocalStorage.getValue().doubleValue()) - Math.abs(valueResultFromRequest.getValue().doubleValue())), 0.0, 0.005, "Valuation mismatch!");

    }
}
