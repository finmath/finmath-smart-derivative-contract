package net.finmath.smartcontract.valuation.service.utils;

import net.finmath.smartcontract.model.InitialSettlementRequest;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.valuation.service.config.ValuationConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

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

		Mockito.when(valuationConfig.getLiveMarketDataProvider()).thenReturn("refinitiv");
		Mockito.when(valuationConfig.isLiveMarketData()).thenReturn(true);

		assertThrows(SDCException.class, () -> serviceUnderTest.generateInitialSettlementResult(initialSettlementRequest));
	}

}