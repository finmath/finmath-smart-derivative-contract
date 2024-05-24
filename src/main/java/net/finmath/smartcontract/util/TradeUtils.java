package net.finmath.smartcontract.util;

import java.util.UUID;

public class TradeUtils {

	private TradeUtils(){}

	/**
	 * create a unique trade id as a substring of a java.util.UUID.randomUUID
	 * @return unique trade id
	 */
	public static String getUniqueTradeId(){
		String uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 17);
		return "ID_" + uniqueID;
	}
}
