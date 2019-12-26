/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 25 Dec 2019
 */

package net.finmath.smartcontract.statemachine;

import java.util.EnumSet;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

/**
 * State Machine Modeling of the Smart Derivative Contract.
 *
 * This state machine models a smart derivative contract using a loop of the following core states:
 * <ul>
 * 	<li>a pre-funding test (Guard),</li>
 * 	<li>an active state (State) (representing the time until the settlement has to occurs),</li>
 * 	<li>a settlement state (State),</li>
 * 	<li>a settlement test (Guard),</li>
 * 	<li>a maturity test (Guard),</li>
 * </ul>
 * See also {@link SmartContractStateMachine.States} and {@link SmartContractStateMachine.Events}.
 *
 * <p/>
 *
 * Most of the time, a smart derivative contract is in the ACTIVE state. If a SETTLE event occurs the contract moves to the
 * SETTLEMENT state.
 *
 * A settlement can be <i>partial</i> or successful. A partial settlement will lead to termination.
 *
 * After settlement, the machine checks the success of the settlement, checks for maturity, checks for pre-funding for the next period, then returns to the active state.
 *
 * Hence, there are three different termination states.
 *
 * @author Christian Fries
 */
public class SmartContractStateMachine {

	/**
	 * The state of a smart derivative contract.
	 *
	 * @author Christian Fries
	 */
	public static enum States {
		/**
		 * The initial state of the contract
		 */
		INCEPTION,
		/**
		 * The waiting state of the contract
		 */
		ACTIVE,
		/**
		 * The contract performing a settlement
		 */
		SETTLEMENT,
		/**
		 * The contract being terminated due to insufficient pre-funding
		 */
		TERMINATED_BY_INSUFFICIENT_PREFUNDING,
		/**
		 * The contract being terminated due to insufficient margin (i.e. the settlement could only be carried out partially)
		 */
		TERMINATED_BY_INSUFFICIENT_MARGIN,
		/**
		 * The contract being terminated by maturing.
		 */
		TERMINATED_BY_MATURITY,
		/**
		 * The pseudo-state (junction) performing the pre-funding check.
		 */
		PREFUNDING_CHECK,
		/**
		 * The pseudo-state (junction) performing the pre-funding check.
		 */
		SETTLEMENT_CHECK,
		/**
		 * The pseudo-state (junction) performing the maturity check.
		 */
		MATURITY_CHECK,
	}

	/**
	 * The events of a smart derivative contract. Events trigger the transitions between states.
	 *
	 * @author Christian Fries
	 */
	public static enum Events {
		INCEPT,
		SETTLE,
		CONTINUE,
		TERMINATE_BY_INSUFFICIENT_PREFUNDING,
		TERMINATE_BY_INSUFFICIENT_MARGIN,
		MATURE
	}

	/*
	 * Example of using the <code>SmartContractStateMachine</code>.
	 */
	public static void main(String[] args) throws Exception {

		SmartContractStateMachine sdcStateMachine = new SmartContractStateMachine();

		// Create stateMachine. The state machine receives events.
		StateMachine<States, Events> stateMachine = sdcStateMachine.buildMachine();

		/*
		 * Example 1: perform some transitions, then terminate due to maturity.
		 */
		sdcStateMachine.setMatured(false).setPrefunded(true).setSettlementSuccessful(true);
		stateMachine.start();
		stateMachine.sendEvent(Events.INCEPT);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		sdcStateMachine.setMatured(true);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.stop();

		/*
		 * Example 2: perform some transitions, then terminate due to insufficient pre-funding.
		 */
		sdcStateMachine.setMatured(false).setPrefunded(true).setSettlementSuccessful(true);
		stateMachine.start();
		stateMachine.sendEvent(Events.INCEPT);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		sdcStateMachine.setPrefunded(false);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.stop();

		/*
		 * Example 3: perform some transitions, then terminate due to insufficient settlement amounts.
		 */
		sdcStateMachine.setMatured(false);
		sdcStateMachine.setPrefunded(true);

		stateMachine.start();
		stateMachine.sendEvent(Events.INCEPT);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		sdcStateMachine.setSettlementSuccessful(false);
		stateMachine.sendEvent(Events.CONTINUE);
		stateMachine.sendEvent(Events.SETTLE);
		stateMachine.stop();
	}

	private boolean isPrefunded = true;
	private boolean isMatured =false;
	private boolean isSettlementSuccessful = true;

	/**
	 * Building a smart derivative contract state machine.
	 */
	public StateMachine<States, Events> buildMachine() throws Exception {
		StateMachineBuilder.Builder<States, Events> builder = StateMachineBuilder.builder();

		builder.configureStates()
		.withStates()
		.initial(States.INCEPTION)
		.state(States.SETTLEMENT, performSettlement())
		.states(EnumSet.allOf(States.class))
		.junction(States.PREFUNDING_CHECK)
		.junction(States.SETTLEMENT_CHECK)
		.junction(States.MATURITY_CHECK);

		builder.configureTransitions()
		.withExternal()
		.source(States.INCEPTION).target(States.ACTIVE).event(Events.INCEPT)
		.and().withExternal()
		.source(States.ACTIVE).target(States.SETTLEMENT).event(Events.SETTLE)
		.and().withExternal()
		.source(States.SETTLEMENT).target(States.SETTLEMENT_CHECK).event(Events.CONTINUE)
		.and().withJunction()
		.source(States.SETTLEMENT_CHECK)
		.first(States.MATURITY_CHECK, settlementCheck())
		.last(States.TERMINATED_BY_INSUFFICIENT_MARGIN)
		.and().withJunction()
		.source(States.MATURITY_CHECK)
		.first(States.PREFUNDING_CHECK, notMaturedCheck())
		.last(States.TERMINATED_BY_MATURITY)
		.and().withJunction()
		.source(States.PREFUNDING_CHECK)
		.first(States.ACTIVE, prefundingCheck())
		.last(States.TERMINATED_BY_INSUFFICIENT_PREFUNDING);

		builder.configureConfiguration().withConfiguration().listener(new StateMachineListener());

		return builder.build();
	}

	/**
	 * @return the isPrefunded
	 */
	public boolean isPrefunded() {
		return isPrefunded;
	}

	/**
	 * @param isPrefunded the isPrefunded to set
	 */
	public SmartContractStateMachine setPrefunded(boolean isPrefunded) {
		this.isPrefunded = isPrefunded;
		return this;
	}

	/**
	 * @return the isMatured
	 */
	public boolean isMatured() {
		return isMatured;
	}

	/**
	 * @param isMatured the isMatured to set
	 */
	public SmartContractStateMachine setMatured(boolean isMatured) {
		this.isMatured = isMatured;
		return this;
	}

	/**
	 * @return the isSettlementSuccessful
	 */
	public boolean isSettlementSuccessful() {
		return isSettlementSuccessful;
	}

	/**
	 * @param isSettlementSuccessful the isSettlementSuccessful to set
	 */
	public SmartContractStateMachine setSettlementSuccessful(boolean isSettlementSuccessful) {
		this.isSettlementSuccessful = isSettlementSuccessful;
		return this;
	}

	/**
	 * Test to be executed to check for successful settlement.
	 *
	 * @return The test to be executed to check for successful settlement.
	 */
	public Guard<States, Events> settlementCheck() {
		return ctx -> this.isSettlementSuccessful;
	}

	/**
	 * Test to be executed to check for existing pre-funding.
	 *
	 * @return The test to be executed to check for existing pre-funding.
	 */
	public Guard<States, Events> prefundingCheck() {
		return ctx -> this.isPrefunded;
	}

	/**
	 * Test to be executed to check for maturity.
	 *
	 * @return The test to be executed to check for maturity.
	 */
	public Guard<States, Events> notMaturedCheck() {
		return ctx -> !this.isMatured;
	}

	/**
	 * Action performend if the settlement state is entered.
	 *
	 * @return The action executed if settlement state is entered
	 */
	public Action<States, Events> performSettlement() {
		return new Action<States, Events>() {

			@Override
			public void execute(StateContext<States, Events> context) {
				/*
				 * This is place where the settlement has to be performed:
				 *	- Call valuation oracle.
				 *  - Update accounts
				 *  - Update status
				 */
				System.out.println("Performing settlement.");
			}
		};
	}

	public class StateMachineListener extends StateMachineListenerAdapter {

		@Override
		public void stateChanged(State from, State to) {
			System.out.printf("Transitioned from %s to %s%n", from == null ?
					"none" : from.getId(), to.getId());
		}
	}
}

