package net.finmath.smartcontract.settlement;

import java.util.List;

/**
 * Collection of settlements.
 */
public class Settlements {

	private List<Settlement> settlements;

	public List<Settlement> getSettlements() {
		return settlements;
	}

	public void setSettlements(List<Settlement> settlements) {
		this.settlements = settlements;
	}
}
