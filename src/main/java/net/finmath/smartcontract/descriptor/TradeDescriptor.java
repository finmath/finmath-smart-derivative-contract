package net.finmath.smartcontract.descriptor;

import java.time.LocalDate;
import java.util.HashMap;


/**
 * Trade descriptor for all product types.
 *
 * 
 * @author Dietmar Schnabel
 * @version 1.0
 */
public class TradeDescriptor  {

	private  HashMap<String,String>   				  legalEntitiesExternalReferences;
	private  HashMap<String,HashMap<String,String>>   legalEntitiesNames;
	private  LocalDate 		   		  				  tradeDate;
	private  String 		   		  				  legReceiver;

	/**
	 * Return the set of legal entity, external references of this trade.
	 *
	 * @return The legalEntitiesExternalReferences The Hash Set.
	 */
	public HashMap<String, String> getLegalEntitiesExternalReferences() {
		return legalEntitiesExternalReferences;
	}

	/**
	 * Set the set of legal external references of this trade.
	 *
	 * @param legalEntitiesExternalReferences The set of legal entity, external references of this trade.
	 */
	public void setLegalEntitiesExternalReferences(HashMap<String, String> legalEntitiesExternalReferences) {
		this.legalEntitiesExternalReferences = legalEntitiesExternalReferences;
	}
	/**
	 * Return the set of legal entity, names of this trade.
	 *
	 * @return The Hash Set.
	 */
	public HashMap<String, HashMap<String,String>> getLegalEntitiesNames() {
		return legalEntitiesNames;
	}

	
	/**
	 * Set the set of legal entity, names of this trade.
	 *
	 * @param legalEntitiesNames The set of legal entity, names of this trade.
	 */
	public void setLegalEntitiesNames(HashMap<String, HashMap<String,String>> legalEntitiesNames) {
		this.legalEntitiesNames = legalEntitiesNames;
	}
	
	
	/**
	 * Return the trade date.
	 *
	 * @return The trade date.
	 */
	public LocalDate getTradeDate() {
		return tradeDate;
	}

	
	/**
	 * Set the trade date.
	 *
	 * @param tradeDate The trade date.
	 */
	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate=tradeDate;
	}
	
	/**
	 * Set the legReceiver.
	 *
	 * @param legReceiver The legReceiver.
	 */
	public void setLegReceiver(String legReceiver) {
		this.legReceiver=legReceiver;
	}
	
	/**
	 * Get the legReceiver.
	 *
	 * @return The legReceiver.
	 */
	public String getLegReceiver() {
		return legReceiver;
	}
}
