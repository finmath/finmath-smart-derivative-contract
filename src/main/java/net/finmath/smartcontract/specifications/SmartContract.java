package net.finmath.smartcontract.specifications;

import java.time.ZonedDateTime;
import java.util.List;

public class SmartContract {

	ZonedDateTime maturityTime;
	ZonedDateTime initialisationTime;
	List<ZonedDateTime> fixingDates;
	List<ZonedDateTime> fixPaymentDates;
	List<ZonedDateTime> floatPaymentDates;
	List<Double> fixCoupons;


	public SmartContract(){

	}

	Transaction		getTransaction(ZonedDateTime time) {
		return null;
	}

	Transaction		getMarginTransaction(ZonedDateTime time,ZonedDateTime timeLast, Oracle oracle) {
		return null;
	}

	Transaction		getCouponTransaction(ZonedDateTime time, Oracle oracle) {
		return null;
	}


	public boolean hasTransaction(ZonedDateTime atTime) {
		return false;
	}


}
