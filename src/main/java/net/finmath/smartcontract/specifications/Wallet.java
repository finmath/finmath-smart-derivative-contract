package net.finmath.smartcontract.specifications;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Wallet {

	final private String address;

	public static enum BufferTypes{
		Default,
		CouponCashflowBuffer,
		VariationMarginBuffer,
		PenaltyMarginBuffer
	}

	final private Map<BufferTypes,Double>	bufferMap;

	public Wallet(String address){
		this.address=address;
		this.bufferMap = new HashMap();
		Stream.of(BufferTypes.values()).forEach(bufferType->this.bufferMap.put(bufferType, 0.0));
	}

	private Wallet(Wallet wallet){
		this.address=new String(wallet.address);
		this.bufferMap = new HashMap();
		Stream.of(BufferTypes.values()).forEach(bufferType->this.bufferMap.put(bufferType, wallet.getCurrentBalance(bufferType)));
	}

	public 	Double getTotalBalance(){
		return Stream.of(BufferTypes.values()).mapToDouble(bufferType -> this.bufferMap.get(bufferType)).sum();
	}

	public 	Double getCurrentBalance(BufferTypes bufferType){
		return this.bufferMap.get(bufferType);
	}


	public Wallet	addAmount(BufferTypes bufferType, Double Amount){
		Wallet walletClone = new Wallet(this);
		Double newAmount = this.getCurrentBalance(bufferType)+Amount;
		walletClone.bufferMap.put(bufferType, newAmount);
		return walletClone;

	}

	public Wallet	subtractAmount(BufferTypes bufferType, Double Amount){
		Wallet walletClone = new Wallet(this);
		Double newAmount = this.getCurrentBalance(bufferType)-Amount;
		walletClone.bufferMap.put(bufferType, newAmount);
		return walletClone;

	}





}
