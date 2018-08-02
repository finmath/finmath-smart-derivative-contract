package net.finmath.smartcontract.specifications;

import java.time.ZonedDateTime;

/**
 * Implementation of an Oracle providing the valuation of the constrac.
 *
 * @TODO: I assume we can remove the getFixing method, since we only require getValue to calculate the effective cashflow.
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 */
public class Oracle {

	public Oracle() {
	}

	public 	Double	getValue(SmartContract contract, ZonedDateTime time){
		return 0.0;
	}

	public 	Double	getFixing(String index, ZonedDateTime time){
		return 0.0;
	}


}
