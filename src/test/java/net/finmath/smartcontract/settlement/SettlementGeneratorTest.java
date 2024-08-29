package net.finmath.smartcontract.settlement;

import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SettlementGeneratorTest {

	@Test
	void generateInitialSettlement() throws IOException, ParserConfigurationException, SAXException {
		InputStream inputStream = SettlementGeneratorTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/historicalMarketData/marketdata_2008-05-02.xml");
		String marketDataString = new String(inputStream.readAllBytes());

		inputStream = SettlementGeneratorTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_with_rics.xml");
		String productString = new String(inputStream.readAllBytes());
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(productString);

		String settlementString = new SettlementGenerator().generateInitialSettlementXml(marketDataString, sdc)
				.marginLimits(List.of(BigDecimal.ONE, BigDecimal.ZERO))
				.settlementValue(BigDecimal.ZERO)
				//.settlementValuePrevious(BigDecimal.ZERO)
				.settlementTimeNext(ZonedDateTime.now())
				.settlementValueNext(BigDecimal.ZERO)
				.build();

		System.out.println(settlementString);

		assertTrue(settlementString.contains("ESTRSWP3Y"));
		assertTrue(settlementString.contains("ESTRSWP1W"));
		assertTrue(settlementString.contains("INITIAL"));
		assertFalse(settlementString.contains("REGULAR"));
		assertTrue(settlementString.contains("<marginValue>0</marginValue>"));
		assertTrue(settlementString.contains("<marketData>"));
		assertTrue(settlementString.contains("<requestTimeStamp>"));
		assertTrue(settlementString.contains("<item>"));
		assertTrue(settlementString.contains("<value>"));
		assertTrue(settlementString.contains("<settlementTimeNext>"));
		assertTrue(settlementString.contains("<settlementValueNext>"));
		assertTrue(settlementString.contains("<settlementValuePrevious>"));
		assertTrue(settlementString.contains("<settlementValue>"));
		assertTrue(settlementString.contains("<marginLimits>"));
	}

	@Test
	void generateRegularSettlement() throws IOException, ParserConfigurationException, SAXException {
		InputStream inputStream = SettlementGeneratorTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/historicalMarketData/marketdata_2008-05-02.xml");
		String marketDataString = new String(inputStream.readAllBytes());

		inputStream = SettlementGeneratorTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_with_rics.xml");
		String productString = new String(inputStream.readAllBytes());
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(productString);

		String settlementString = new SettlementGenerator().generateRegularSettlementXml(marketDataString, sdc, BigDecimal.ONE)
				.marginLimits(List.of(BigDecimal.ONE, BigDecimal.ZERO))
				.settlementValue(BigDecimal.ZERO)
				.settlementValuePrevious(BigDecimal.ZERO)
				.settlementTimeNext(ZonedDateTime.now())
				.settlementValueNext(BigDecimal.ZERO)
				.build();

		System.out.println(settlementString);

		assertTrue(settlementString.contains("ESTRSWP3Y"));
		assertTrue(settlementString.contains("ESTRSWP1W"));
		assertTrue(settlementString.contains("REGULAR"));
		assertFalse(settlementString.contains("INITIAL"));
		assertTrue(settlementString.contains("<marginValue>1</marginValue>"));
		assertTrue(settlementString.contains("<marketData>"));
		assertTrue(settlementString.contains("<requestTimeStamp>"));
		assertTrue(settlementString.contains("<item>"));
		assertTrue(settlementString.contains("<value>"));
		assertTrue(settlementString.contains("<settlementTimeNext>"));
		assertTrue(settlementString.contains("<settlementValueNext>"));
		assertTrue(settlementString.contains("<settlementValuePrevious>"));
		assertTrue(settlementString.contains("<settlementValue>"));
		assertTrue(settlementString.contains("<marginLimits>"));
	}

	@Test
	void generateIncompleteSettlement_Exception() throws IOException, ParserConfigurationException, SAXException {
		InputStream inputStream = SettlementGeneratorTest.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/historicalMarketData/marketdata_2008-05-02.xml");
		String marketDataString = new String(inputStream.readAllBytes());

		inputStream = SettlementGeneratorTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract_with_rics.xml");
		String productString = new String(inputStream.readAllBytes());
		SmartDerivativeContractDescriptor sdc = SDCXMLParser.parse(productString);

		SettlementGenerator generator = new SettlementGenerator().generateRegularSettlementXml(marketDataString, sdc, BigDecimal.ONE)
				.marginLimits(List.of(BigDecimal.ONE, BigDecimal.ZERO))
				.settlementValue(BigDecimal.ZERO)
				.settlementValuePrevious(BigDecimal.ZERO)
				.settlementTimeNext(ZonedDateTime.now());
				//.settlementValueNext(BigDecimal.ZERO);

		assertThrows(SDCException.class, generator::build);
	}
}