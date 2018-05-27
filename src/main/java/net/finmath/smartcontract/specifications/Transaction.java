package net.finmath.smartcontract.specifications;



public class Transaction {
	
	public static enum TransactionTypes{
		Default,
		CouponCashflow,
		VariationMargin
	}
	
	final String sourceWalletAdress;
	final String targetWalletAdress;
	final Double Amount;
	public Transaction(String sourceWalletAdress, String targetWalletAdress, Double Amount) throws Exception{
		this.sourceWalletAdress=sourceWalletAdress;
		this.targetWalletAdress=targetWalletAdress;
		this.Amount=Amount;
	}
	public String getSourceWalletAdress() {
		return sourceWalletAdress;
	}
	public String getTargetWalletAdress() {
		return targetWalletAdress;
	}
	public Double getAmount() {
		return Amount;
	}
	

}
