package net.finmath.smartcontract.util;

import java.math.RoundingMode;

/**
 * The Class SDCRounding.
 * <p>
 * Concrete implementation of the SDCAbstractRounding class.
 * Will be currency dependent.
 */
public class SDCRounding extends SDCAbstractRounding {

	private SDCRounding() {
	}

	public SDCRounding(int sc, RoundingMode rc) {

		scale = sc;
		roundingMode = rc;

	}

}
