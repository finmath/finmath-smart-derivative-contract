package net.finmath.smartcontract.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TradeUtilsTest {

	@Test
	void getUniqueTradeId() {
		List<String> ids = new ArrayList<>();
		for (int i = 1; i <= 200 ; i++){
			String id = TradeUtils.getUniqueTradeId();
			System.out.println(i + ". " +id);
			assertTrue(id.contains("ID_"));

			//max length 20
			assertEquals(20, id.length());

			//id only contains letters and numbers (and the leading underscore)
			assertTrue(Pattern.matches("ID_[a-zA-Z0-9]+", id));

			//check on uniqueness
			assertFalse(ids.contains(id));
			ids.add(id);
		}
	}
}