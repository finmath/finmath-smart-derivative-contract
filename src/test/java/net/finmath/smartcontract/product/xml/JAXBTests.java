package net.finmath.smartcontract.product.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.implementation.MarginCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.core.io.ClassPathResource;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureJsonTesters // manually re-enable Spring Jackson auto-config
@AutoConfigureJson
class JAXBTests {

	public static final String PARTY1_DLT_ADDRESS = "0x12345678901";
	public static final String PARTY2_DLT_ADDRESS = "0x23456789012";

	@Test
	void checkChangedTradeParams() throws java.lang.Exception {

		final URL url = JAXBTests.class.getClassLoader().getResource("net.finmath.smartcontract.product.xml/smartderivativecontract.xml");
		final JAXBContext jaxbContext = JAXBContext.newInstance(Smartderivativecontract.class);

		assert url != null;
		Smartderivativecontract sdc = getUnmarshalledObjectFromXML(jaxbContext, url);
		sdc.setReceiverPartyID("party2");// Change Receiver PartyID
		Swap swap = (Swap) sdc.getUnderlyings().getUnderlying().getDataDocument().getTrade().get(0).getProduct().getValue();
		BigDecimal acutalNotional = swap.getSwapStream().get(0).getCalculationPeriodAmount().getCalculation().getNotionalSchedule().getNotionalStepSchedule().getInitialValue();
		swap.getSwapStream().get(0).getCalculationPeriodAmount().getCalculation().getNotionalSchedule().getNotionalStepSchedule().setInitialValue(acutalNotional.multiply(BigDecimal.valueOf(10)));
		swap.getSwapStream().get(1).getCalculationPeriodAmount().getCalculation().getNotionalSchedule().getNotionalStepSchedule().setInitialValue(acutalNotional.multiply(BigDecimal.valueOf(10)));

		String marshalledXML = getMarshalledXMLfromObject(jaxbContext, sdc);

		MarginCalculator marginCalculator = new MarginCalculator();
		final String marketData = new String(JAXBTests.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset_with_fixings.xml").readAllBytes(), StandardCharsets.UTF_8);

		ValueResult valuationResultOrig = marginCalculator.getValue(marketData, new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8));
		ValueResult valuationResultMarshalled = marginCalculator.getValue(marketData, marshalledXML);
		double origValue = valuationResultOrig.getValue().doubleValue();
		double changedValue = valuationResultMarshalled.getValue().doubleValue();
		double modificationMultiplier = -1 * 10;
		assertEquals(origValue * modificationMultiplier, changedValue, 0.1);

	}


	@Test
	void checkValuation() throws java.lang.Exception {

		final URL url = JAXBTests.class.getClassLoader().getResource("net.finmath.smartcontract.product.xml/smartderivativecontract.xml");
		final JAXBContext jaxbContext = JAXBContext.newInstance(Smartderivativecontract.class);

		Smartderivativecontract sdc = getUnmarshalledObjectFromXML(jaxbContext, url);

		String marshalledXML = getMarshalledXMLfromObject(jaxbContext, sdc);

		MarginCalculator marginCalculator = new MarginCalculator();
		final String marketData = new String(JAXBTests.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset_with_fixings.xml").readAllBytes(), StandardCharsets.UTF_8);

		ValueResult valuationResultOrig = marginCalculator.getValue(marketData, new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8));
		ValueResult valuationResultMarshalled = marginCalculator.getValue(marketData, marshalledXML);
		assertEquals(valuationResultOrig.getValue(), valuationResultMarshalled.getValue());

	}

	private Smartderivativecontract getUnmarshalledObjectFromXML(final JAXBContext jaxbContext, final URL url) throws java.lang.Exception {

		File file = new File(url.getPath());
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Smartderivativecontract sdc = (Smartderivativecontract) jaxbUnmarshaller.unmarshal(file);
		return sdc;
	}

	private String getMarshalledXMLfromObject(final JAXBContext jaxbContext, final Smartderivativecontract sdc) throws java.lang.Exception {

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		marshaller.marshal(sdc, outputStream);
		String marshalledXML = outputStream.toString().replaceAll("<fpml:dataDocument fpmlVersion=\"5-9\">", "<dataDocument fpmlVersion=\"5-9\" xmlns=\"http://www.fpml.org/FpML-5/confirmation\">").replaceAll("fpml:", "");
		return marshalledXML;
	}


	@Test
	void jaxBTestWithValidation() {
		try {
			String xsdFile = JAXBTests.class.getClassLoader().getResource("net.finmath.smartcontract.product.xml/smartderivativecontract.xsd").getPath();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema sdcSchema = sf.newSchema(new File(xsdFile));

			String path = JAXBTests.class.getClassLoader().getResource("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").getPath();
			File file = new File(path);
			JAXBContext jaxbContext = JAXBContext.newInstance(Smartderivativecontract.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(sdcSchema);

			Smartderivativecontract sdc = (Smartderivativecontract) jaxbUnmarshaller.unmarshal(file);

			Assertions.assertNotNull(sdc);

			Marshaller marshaller = jaxbContext.createMarshaller();

			// If the patch is not applied and the marshaller has the scheme set, it will throw an exception
			marshaller.setSchema(sdcSchema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			marshaller.marshal(sdc, outputStream);


		} catch (java.lang.Exception e) {
			Assertions.fail(e);
		}
	}

	@Test
	void jaxBPlainTest() throws java.lang.Exception {
		String path = JAXBTests.class.getClassLoader().getResource("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").getPath();
		File file = new File(path);
		JAXBContext jaxbContext = JAXBContext.newInstance(Smartderivativecontract.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		Smartderivativecontract sdc = (Smartderivativecontract) jaxbUnmarshaller.unmarshal(file);

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		marshaller.marshal(sdc, outputStream);
		String xmlString = outputStream.toString().replaceAll("<fpml:dataDocument fpmlVersion=\"5-9\">", "<dataDocument fpmlVersion=\"5-9\" xmlns=\"http://www.fpml.org/FpML-5/confirmation\">").replaceAll("fpml:", "");


		Assertions.assertNotNull(xmlString);
	}


	@Test
	void handlerTest() throws java.lang.Exception {
		final String generatorFile = "net.finmath.smartcontract.product.xml/smartderivativecontract.xml";
		final String marketDataProvider = "refinitiv";
		final String schemaPath = "net.finmath.smartcontract.product.xml/smartderivativecontract.xsd";
		final String projectVersion = "x.y.z";

		PlainSwapOperationRequest request = generateRequest(marketDataProvider);

		PlainSwapEditorHandler handler = new PlainSwapEditorHandler(request, generatorFile, schemaPath, projectVersion);
		String fpml = handler.getContractAsXmlString();

		//final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/legacy/md_testset_refinitiv.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset_rics.xml").readAllBytes(), StandardCharsets.UTF_8);

		MarginCalculator marginCalculator = new MarginCalculator();
		ValueResult valuationResult = marginCalculator.getValue(marketData, fpml);

		double value = valuationResult.getValue().doubleValue();

		assertEquals(-881079.11, value, 0.005, "Valuation");
		assertTrue(fpml.contains("<address>"+PARTY1_DLT_ADDRESS+"</address>"));
		assertTrue(fpml.contains("<address>"+PARTY2_DLT_ADDRESS+"</address>"));
		System.out.println(valuationResult);
	}

	private PlainSwapOperationRequest generateRequest(String marketDataProvider) throws java.lang.Exception {

		ObjectMapper objectMapper = JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();

		final String fullSymbolListFromTemplate = new ClassPathResource(
				"references" + File.separator + "template2_symbolslist.json").getContentAsString(
				StandardCharsets.UTF_8);

		final Counterparty firstCounterparty = new Counterparty().baseUrl("aaa").bicCode("ABCDXXXX")
				.fullName("PartyDoubleTest").dltAddress(PARTY1_DLT_ADDRESS);
		final PaymentFrequency floatingPaymentFrequency = new PaymentFrequency().period("M").periodMultiplier(6)
				.fullName("Semiannual");
		final PaymentFrequency fixedPaymentFrequency = new PaymentFrequency().period("Y").periodMultiplier(1)
				.fullName("Annual");
		final Counterparty secondCounterparty = new Counterparty().baseUrl("bbb").bicCode("EFDGXXXX")
				.fullName("PartyTest").dltAddress(PARTY2_DLT_ADDRESS);
		final PlainSwapOperationRequest plainSwapOperationRequest = new PlainSwapOperationRequest().firstCounterparty(
						firstCounterparty).secondCounterparty(secondCounterparty).marginBufferAmount(30000.0)
				.terminationFeeAmount(
						10000.0)
				.currency("EUR")
				.tradeType("SDCPledgedBalance")
				.uniqueTradeIdentifier("UTI123789")
				.tradeDate(        //2022-09-07
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
				.dailySettlementTime("12:00")
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
				.marketDataProvider(marketDataProvider)
				.receiverPartyID("party2");

		return plainSwapOperationRequest;
	}
}
