package net.finmath.smartcontract.valuation.service.utils;

import net.finmath.smartcontract.model.*;
import net.finmath.smartcontract.valuation.service.config.ValuationConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

	@InjectMocks
	private SettlementService serviceUnderTest;

	@Mock
	private ValuationConfig valuationConfig;

	@Test
	void generateInitialSettlement_Exception_missingRefinitvConfig() throws IOException {
		InputStream inputStream = SettlementServiceTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_with_rics.xml");
		String productXml = new String(inputStream.readAllBytes());

		InitialSettlementRequest initialSettlementRequest = new InitialSettlementRequest().tradeData(productXml);

		when(valuationConfig.getLiveMarketDataProvider()).thenReturn("refinitiv");
		when(valuationConfig.isLiveMarketData()).thenReturn(true);

		assertThrows(SDCException.class, () -> serviceUnderTest.generateInitialSettlementResult(initialSettlementRequest));
	}

	@Test
	void generateInitialSettlement() throws IOException {
		InputStream inputStream = SettlementServiceTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_simulated_historical_marketdata.xml");
		String productXml = new String(inputStream.readAllBytes());

		InitialSettlementRequest initialSettlementRequest = new InitialSettlementRequest().tradeData(productXml);

		when(valuationConfig.getLiveMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.getInternalMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.isLiveMarketData()).thenReturn(false);

		InitialSettlementResult initialSettlementResult = serviceUnderTest.generateInitialSettlementResult(initialSettlementRequest);
		System.out.println(initialSettlementResult.getGeneratedInitialSettlement());
	}

	@Test
	void generateRegularSettlement() throws IOException {
		String settlementLast = new String(SettlementServiceTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/settlement_testset_initial.xml").readAllBytes(), StandardCharsets.UTF_8);

		InputStream inputStream = SettlementServiceTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_simulated_historical_marketdata.xml");
		String productXml = new String(inputStream.readAllBytes());

		RegularSettlementRequest regularSettlementRequest = new RegularSettlementRequest()
				.settlementLast(settlementLast)
				.tradeData(productXml);

		when(valuationConfig.getLiveMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.getInternalMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.isLiveMarketData()).thenReturn(false);

		RegularSettlementResult regularSettlementResult = serviceUnderTest.generateRegularSettlementResult(regularSettlementRequest);
		String settlementString = regularSettlementResult.getGeneratedRegularSettlement();
		System.out.println(settlementString);

		assertTrue(settlementString.contains("ESTRSWP3Y"));
		assertTrue(settlementString.contains("ESTRSWP1W"));
		assertTrue(settlementString.contains("REGULAR"));
		assertFalse(settlementString.contains("INITIAL"));
		assertTrue(settlementString.contains("<marginValue>12329.11</marginValue>"));
		assertTrue(settlementString.contains("<marketData>"));
		assertTrue(settlementString.contains("<requestTimeStamp>"));
		assertTrue(settlementString.contains("<item>"));
		assertTrue(settlementString.contains("<value>"));
		assertTrue(settlementString.contains("<settlementTimeNext>"));
		assertTrue(settlementString.contains("<settlementNPVNext>"));
		assertTrue(settlementString.contains("<settlementNPVPrevious>"));
		assertTrue(settlementString.contains("<settlementNPV>"));
		assertTrue(settlementString.contains("<marginLimits>"));
	}

	@Test
	void generateRegularSettlement_includes_FixingOfLastSettlement() throws IOException {
		String settlementLast = new String(SettlementServiceTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/settlement_testset_initial_historical.xml").readAllBytes(), StandardCharsets.UTF_8);

		InputStream inputStream = SettlementServiceTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_simulated_historical_marketdata.xml");
		String productXml = new String(inputStream.readAllBytes());

		RegularSettlementRequest regularSettlementRequest = new RegularSettlementRequest()
				.settlementLast(settlementLast)
				.tradeData(productXml);

		when(valuationConfig.getLiveMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.getInternalMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.isLiveMarketData()).thenReturn(false);
		when(valuationConfig.getProductFixingType()).thenReturn("Fixing");

		RegularSettlementResult regularSettlementResult = serviceUnderTest.generateRegularSettlementResult(regularSettlementRequest);
		String settlementString = regularSettlementResult.getGeneratedRegularSettlement();
		System.out.println(settlementString);

		assertTrue(settlementString.contains("<id>EUB6FIX6M</id><value>0.0484</value><timeStamp>20080502-170000</timeStamp>"));
		assertTrue(settlementString.contains("<id>EUB6FIX6M</id><value>0.0521</value><timeStamp>20080917-170000</timeStamp>"));
		assertTrue(settlementString.contains("<settlementNPV>-752.94</settlementNPV>"));
		assertTrue(settlementString.contains("<settlementType>REGULAR</settlementType>"));
	}

	@Test
	void generateRegularSettlement_includes_FixingOfLastSettlement_twice() throws IOException {
		String settlementLast = new String(SettlementServiceTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/settlement_testset_initial_historical.xml").readAllBytes(), StandardCharsets.UTF_8);

		InputStream inputStream = SettlementServiceTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_simulated_historical_marketdata.xml");
		String productXml = new String(inputStream.readAllBytes());

		when(valuationConfig.getLiveMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.getInternalMarketDataProvider()).thenReturn("internal");
		when(valuationConfig.isLiveMarketData()).thenReturn(false);
		when(valuationConfig.getProductFixingType()).thenReturn("Fixing");


		//first regular settlement request
		RegularSettlementRequest regularSettlementRequest = new RegularSettlementRequest()
				.settlementLast(settlementLast)
				.tradeData(productXml);

		RegularSettlementResult regularSettlementResult = serviceUnderTest.generateRegularSettlementResult(regularSettlementRequest);
		String settlementString = regularSettlementResult.getGeneratedRegularSettlement();
		System.out.println("first regular settlementString");
		System.out.println(settlementString);

		//second regular settlement request
		RegularSettlementRequest secondSettlementRequest = new RegularSettlementRequest()
				.settlementLast(regularSettlementResult.getGeneratedRegularSettlement())
				.tradeData(productXml);
		RegularSettlementResult secondSettlementResult = serviceUnderTest.generateRegularSettlementResult(secondSettlementRequest);

		settlementString = secondSettlementResult.getGeneratedRegularSettlement();
		System.out.println("second regular settlementString");
		System.out.println(settlementString);

		//fixing from initial settlement
		assertTrue(settlementString.contains("<id>EUB6FIX6M</id><value>0.0521</value><timeStamp>20080917-170000</timeStamp>"));
		//fixing first settlement request
		assertTrue(settlementString.contains("<id>EUB6FIX6M</id><value>0.0484</value><timeStamp>20080502-170000</timeStamp>"));
		//fixing second settlement request
		assertTrue(settlementString.contains("<id>EUB6FIX6M</id><value>0.0484</value><timeStamp>20080505-170000</timeStamp>"));
		assertTrue(settlementString.contains("<settlementNPV>72349.58</settlementNPV>"));
		assertTrue(settlementString.contains("<settlementType>REGULAR</settlementType>"));
	}

}