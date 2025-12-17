package net.finmath.smartcontract.product.xml;

public class PlainSwapValidityCheck {

	public static boolean isRollConventionEnumValid(String rollConvention) {
		if (rollConvention == null) return false;
		return switch (rollConvention) {
			case "EOM", "FRN", "IMM", "IMMCAD", "IMMAUD", "IMMNZD", "SFE", "NONE", "TBILL",
				 "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				 "11", "12", "13", "14", "15", "16", "17", "18", "19",
				 "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
				 "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" -> true;
			default -> false;
		};
	}
}
