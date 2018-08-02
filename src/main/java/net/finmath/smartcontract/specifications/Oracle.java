package net.finmath.smartcontract.specifications;

import java.time.ZonedDateTime;

public class Oracle {
	
	public Oracle(){
		
	}
	
	public 	Double	getValue(SmartContract contract, ZonedDateTime time){
		return 0.0;
	}
	
	public 	Double	getFixing(String index, ZonedDateTime time){
		return 0.0;
	}
	

}
