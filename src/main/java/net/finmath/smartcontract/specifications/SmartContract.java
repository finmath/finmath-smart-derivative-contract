package net.finmath.smartcontract.specifications;

import java.util.List;

import org.joda.time.DateTime;

public class SmartContract {
	
	DateTime maturityTime;
	DateTime initialisationTime;
	List<DateTime> fixingDates;
	List<DateTime> fixPaymentDates;
	List<DateTime> floatPaymentDates;
	List<Double> fixCoupons;

	
	public SmartContract(){
		
	}
	
	Transaction		getTransaction(DateTime time) {
		return null;
	}
	
	Transaction		getMarginTransaction(DateTime time, DateTime timeLast, Oracle oracle) {
		return null;
	}
	
	Transaction		getCouponTransaction(DateTime time, Oracle oracle) {
		return null;
	}
	
}
