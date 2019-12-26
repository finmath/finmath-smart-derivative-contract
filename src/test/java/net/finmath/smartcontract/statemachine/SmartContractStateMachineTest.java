/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 26 Dec 2019
 */

package net.finmath.smartcontract.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;

import net.finmath.smartcontract.statemachine.SmartContractStateMachine.Events;
import net.finmath.smartcontract.statemachine.SmartContractStateMachine.States;

/**
 * @author Christian Fries
 *
 */
class SmartContractStateMachineTest {

	@Test
	void testTerminationByMaturity() throws Exception {
		SmartContractStateMachine sdcStateMachine = new SmartContractStateMachine();

		// Create stateMachine. The state machine receives events.
		StateMachine<States, Events> stateMachine = sdcStateMachine.buildMachine();

		/*
		 * perform some transitions, then terminate due to maturity.
		 */
		stateMachine.start();
		stateMachine.sendEvent(Events.INCEPT);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		sdcStateMachine.setMatured(true);
		stateMachine.sendEvent(Events.CONTINUE);

		assertEquals(States.TERMINATED_BY_MATURITY, stateMachine.getState().getId(), "Terminated state");
		stateMachine.stop();
	}

	@Test
	void testTerminationByInsufficientPreFunding() throws Exception {
		SmartContractStateMachine sdcStateMachine = new SmartContractStateMachine();

		// Create stateMachine. The state machine receives events.
		StateMachine<States, Events> stateMachine = sdcStateMachine.buildMachine();

		/*
		 * perform some transitions, then terminate due to insufficient pre-funding.
		 */
		stateMachine.start();
		stateMachine.sendEvent(Events.INCEPT);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		sdcStateMachine.setPrefunded(false);
		stateMachine.sendEvent(Events.CONTINUE);

		assertEquals(States.TERMINATED_BY_INSUFFICIENT_PREFUNDING, stateMachine.getState().getId(), "Terminated state");
		stateMachine.stop();
	}

	@Test
	void testTerminationByInsufficientMargin() throws Exception {
		SmartContractStateMachine sdcStateMachine = new SmartContractStateMachine();

		// Create stateMachine. The state machine receives events.
		StateMachine<States, Events> stateMachine = sdcStateMachine.buildMachine();

		/*
		 * perform some transitions, then terminate due to insufficient settlement amounts.
		 */
		stateMachine.start();
		stateMachine.sendEvent(Events.INCEPT);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		sdcStateMachine.setSettlementSuccessful(false);
		stateMachine.sendEvent(Events.CONTINUE);

		assertEquals(States.TERMINATED_BY_INSUFFICIENT_MARGIN, stateMachine.getState().getId(), "Terminated state");
		stateMachine.stop();
	}
}
