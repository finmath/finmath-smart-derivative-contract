package net.finmath.smartcontract.settlement;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.finmath.smartcontract.model.MarketDataList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

class SettlementTests {

	@Test
	void testGenerateSettlementXML() throws Exception{

		String tradeID = "SDCTestTrade1";
		Settlement.SettlementType  type = Settlement.SettlementType.REGULAR;
		BigDecimal marginValue = BigDecimal.valueOf(-5000);
		BigDecimal settlementValue = BigDecimal.valueOf(20000);
		BigDecimal setttlementValuePrevious = BigDecimal.valueOf(25000);
		BigDecimal settlementValueNext = BigDecimal.valueOf(20001);
		List<BigDecimal> marginLimits = List.of(BigDecimal.valueOf(-50000),BigDecimal.valueOf(50000));
		ZoneId zone = ZoneId.of("Europe/Berlin");
		ZonedDateTime settlementTime = ZonedDateTime.of(LocalDate.now(), LocalTime.of(17,0,0), zone);
		ZonedDateTime settlementTimeNext = settlementTime.plusDays(1);

		final String marketDataXMLStr = new String(SettlementTests.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml").readAllBytes(), StandardCharsets.UTF_8);

		StringReader reader = new StringReader(marketDataXMLStr);
		JAXBContext jaxbContext = JAXBContext.newInstance(MarketDataList.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MarketDataList marketData = (MarketDataList) jaxbUnmarshaller.unmarshal(reader);

		Settlement settlement = new Settlement();
		settlement.setTradeId(tradeID);
		settlement.setSettlementType(type);
		settlement.setCurrency("EUR");
		settlement.setMarginValue(marginValue);
		settlement.setMarginLimits(marginLimits);
		settlement.setSettlementTime(settlementTime);
		settlement.setMarketData(marketData);
		settlement.setSettlementNPV(settlementValue);
		settlement.setSettlementNPVPrevious(setttlementValuePrevious);
		settlement.setSettlementTimeNext(settlementTimeNext);
		settlement.setSettlementNPVNext(settlementValueNext);

		JAXBContext jaxbContextSettlement = JAXBContext.newInstance(Settlement.class);
		Marshaller jaxbMarshaller = jaxbContextSettlement.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(settlement, System.out);
		jaxbMarshaller.marshal(settlement, writer);
		String xmlStr = writer.toString();
		Assertions.assertFalse(xmlStr.isEmpty());
	}
}
