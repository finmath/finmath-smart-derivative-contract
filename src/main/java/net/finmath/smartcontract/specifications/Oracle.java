package net.finmath.smartcontract.specifications;

import org.joda.time.DateTime;

public class Oracle {
	
	public Oracle(){
		
	}
	
	public 	Double	getValue(SmartContract contract, DateTime time){
		return 0.0;
	}
	
	public 	Double	getFixing(String index, DateTime time){
		return 0.0;
	}
	

}
