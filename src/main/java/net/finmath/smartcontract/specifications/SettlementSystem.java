package net.finmath.smartcontract.specifications;

import java.util.*;


public class SettlementSystem {
	
	final Set<Wallet> walletSet;
	final Set<SmartContract> contractSet;
	final Set<Transaction> transactionSet;
	final Oracle oracleInstance;
	
	public SettlementSystem(Oracle oracleInstance){
		this.walletSet = new HashSet<>();
		this.contractSet = new HashSet<>();
		this.transactionSet = new HashSet<>();
		this.oracleInstance = oracleInstance;
	}
	

}
