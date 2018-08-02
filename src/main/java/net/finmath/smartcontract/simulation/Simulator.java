package net.finmath.smartcontract.simulation;

import net.finmath.smartcontract.specifications.SmartContract;
import net.finmath.time.TimeDiscretization;

/**
 * Simulation of a smart contract over a series of times.
 *
 * @author Christian Fries
 */
public class Simulator {

	private final TimeDiscretization timeDiscretization;
	private final SmartContract smartContract;

	public Simulator(TimeDiscretization timeDiscretization, SmartContract smartContract) {
		super();
		this.timeDiscretization = timeDiscretization;
		this.smartContract = smartContract;
	}

}
