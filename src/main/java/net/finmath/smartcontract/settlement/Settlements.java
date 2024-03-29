package net.finmath.smartcontract.settlement;

import java.util.List;
import java.util.Optional;

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

	public Optional<Settlement> getPrevious(Settlement settlement) {
		int currentIndex = settlements.indexOf(settlement);
		if (currentIndex <= 0) return Optional.empty();
		else return Optional.of(settlements.get(currentIndex - 1));
	}

	public Optional<Settlement> getNext(Settlement settlement) {
		int currentIndex = settlements.indexOf(settlement);
		if (currentIndex > settlements.size() - 2) return Optional.empty();
		else return Optional.of(settlements.get(currentIndex + 1));
	}
}
