package net.finmath.smartcontract.settlement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SettlementsTest {

	Settlements settlements;

	String tradeId1, tradeId2, tradeId3, tradeId4;
	Settlement s1, s2, s3, s4;

	@BeforeEach
	void setup() {
		settlements = new Settlements();
		generateSettlementList();
	}

	@Test
	void getPrevious() {
		Optional<Settlement> optionalSettlement = settlements.getPrevious(s3);
		Settlement settlement = null;
		if (optionalSettlement.isPresent())
			settlement = optionalSettlement.get();

		assertNotNull(settlement);
		assertEquals(s2, settlement);
		assertEquals(tradeId2, settlement.getTradeId());
	}

	@Test
	void getNext() {
		Optional<Settlement> optionalSettlement = settlements.getNext(s3);
		Settlement settlement = null;
		if (optionalSettlement.isPresent()) {
			settlement = optionalSettlement.get();
		}

		assertNotNull(settlement);
		assertEquals(s4, settlement);
		assertEquals(tradeId4, settlement.getTradeId());
	}

	@Test
	void getSettlements() {
		List<Settlement> list = settlements.getSettlements();

		assertNotNull(list);
		assertEquals(tradeId1, list.get(list.indexOf(s1)).getTradeId());
	}


	private void generateSettlementList() {
		tradeId1 = "ID_1";
		tradeId2 = "ID_2";
		tradeId3 = "ID_3";
		tradeId4 = "ID_4";

		List<Settlement> list = new ArrayList<>();

		s1 = new Settlement();
		s1.setTradeId(tradeId1);
		list.add(s1);

		s2 = new Settlement();
		s2.setTradeId(tradeId2);
		list.add(s2);

		s3 = new Settlement();
		s3.setTradeId(tradeId3);
		list.add(s3);

		s4 = new Settlement();
		s4.setTradeId(tradeId4);
		list.add(s4);

		settlements.setSettlements(list);
	}

}