package net.finmath.smartcontract.product.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.finmath.smartcontract.client.ValuationClient;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.product.xml.PlainSwapEditorHandler;
import net.finmath.smartcontract.valuation.MarginCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@AutoConfigureJsonTesters // manually re-enable Spring Jackson auto-config
@AutoConfigureJson
public class PlainSwapEditorHandlerTests {

	@Test
	void handlerTest() throws java.lang.Exception {

		final String generatorFile = "generators/eur_euribor_y_s_with_fixings.xml";
		final String schemaPath = "schemas/sdc-schemas/sdcml-contract.xsd";

		PlainSwapOperationRequest request = generateRequest(generatorFile);

		PlainSwapEditorHandler handler =new PlainSwapEditorHandler(request,request.getCurrentGenerator(),schemaPath);

		String product = handler.getContractAsXmlString();

		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset_with_fixings.json").readAllBytes(), StandardCharsets.UTF_8);

		MarginCalculator marginCalculator = new MarginCalculator();
		ValueResult valuationResult = marginCalculator.getValue(marketData, product);

		double value = valuationResult.getValue().doubleValue();

		Assertions.assertEquals(-549726.34, value, 0.005, "Valuation");
		System.out.println(valuationResult);



	}

	private PlainSwapOperationRequest generateRequest(String currentGeneratorFile) throws java.lang.Exception {


		ObjectMapper objectMapper = JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();

		final String fullSymbolListFromTemplate = new ClassPathResource(
				"references" + File.separator + "template2_symbolslist.json").getContentAsString(
				StandardCharsets.UTF_8);

		final Counterparty firstCounterparty = new Counterparty().baseUrl("aaa").bicCode("ABCDXXXX")
				.fullName("PartyDoubleTest");
		final PaymentFrequency floatingPaymentFrequency = new PaymentFrequency().period("M").periodMultiplier(6)
				.fullName("Semiannual");
		final PaymentFrequency fixedPaymentFrequency = new PaymentFrequency().period("Y").periodMultiplier(1)
				.fullName("Annual");
		final Counterparty secondCounterparty = new Counterparty().baseUrl("bbb").bicCode("EFDGXXXX")
				.fullName("PartyTest");
		final PlainSwapOperationRequest plainSwapOperationRequest = new PlainSwapOperationRequest().firstCounterparty(
						firstCounterparty).secondCounterparty(secondCounterparty).marginBufferAmount(30000.0)
				.terminationFeeAmount(
						10000.0)
				.currency("EUR")
				.tradeDate(		//2022-09-07
						OffsetDateTime.of(
								LocalDateTime.of(
										2022,
										Month.SEPTEMBER,
										05,
										14,
										35),
								ZoneOffset.UTC))
				.effectiveDate(
						OffsetDateTime.of(
								LocalDateTime.of(
										2022,
										Month.SEPTEMBER,
										7,
										14,
										35),
								ZoneOffset.UTC))
				.terminationDate(
						OffsetDateTime.of(
								LocalDateTime.of(
										2032,
										Month.FEBRUARY,
										7,
										14,
										35),
								ZoneOffset.UTC))
				.fixedPayingParty(
						secondCounterparty)
				.floatingPayingParty(
						firstCounterparty)
				.fixedRate(3.95)
				.fixedDayCountFraction(
						"30E/360")
				.fixedPaymentFrequency(
						fixedPaymentFrequency)
				.floatingRateIndex(
						"EURIBOR 6M")
				.floatingDayCountFraction(
						"ACT/360")
				.floatingFixingDayOffset(
						-2)
				.floatingPaymentFrequency(
						floatingPaymentFrequency)
				.notionalAmount(
						10000000.00)
				.valuationSymbols(
						objectMapper.readerForListOf(
										FrontendItemSpec.class)
								.readValue(
										fullSymbolListFromTemplate))
				.currentGenerator(currentGeneratorFile);

		return plainSwapOperationRequest;
	}
}
