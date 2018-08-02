package net.finmath.smartcontract.specifications;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.finmath.smartcontract.specifications.Wallet.BufferTypes;


public class SettlementSystem {

	final Map<String,Wallet> walletMap;
	final Set<SmartContract> contractSet;
	final Set<Transaction> transactionSet;
	final Oracle oracleInstance;

	public SettlementSystem(Oracle oracleInstance){
		this.walletMap = new HashMap<>();
		this.contractSet = new HashSet<>();
		this.transactionSet = new HashSet<>();
		this.oracleInstance = oracleInstance;
	}

	SettlementSystem	updateAtTime(ZonedDateTime time) {
		Set<Transaction> transactionsToSettle =  contractSet.stream().filter(contract->contract.hasTransaction(time)).map(contract->contract.getTransaction(time)).collect(Collectors.toSet());
		transactionsToSettle.stream().forEach(transaction->this.settleTransaction(transaction));
		return this; /*Should be a clone with updated wallets */
	}

	SettlementSystem	settleTransaction(Transaction transaction) {
		Wallet sourceWallet = walletMap.get(transaction.getSourceWalletAdress());
		Wallet targetWallet = walletMap.get(transaction.getTargetWalletAdress());

		sourceWallet = sourceWallet.addAmount(BufferTypes.Default, transaction.getAmount());
		targetWallet = targetWallet.subtractAmount(BufferTypes.Default, transaction.getAmount());
		return this;

	}


}
