// SPDX-License-Identifier: MIT
pragma solidity >=0.7.0 <0.9.0;

/*------------------------------------------- DESCRIPTION ---------------------------------------------------------------------------------------*/

/**
 * @dev Interface specification for a Smart Derivative Contract, which specifies the post-trade live cycle of an OTC financial derivative in a completely deterministic way.
 * Counterparty Risk is removed by construction.
 *
 * A Smart Derivative Contract is a deterministic settlement protocol which has economically the same behaviour as a collateralized OTC financial derivative.
 * It aims is to remove many inefficiencies in collateralized OTC transactions and remove counterparty credit risk by construction.
 *
 * In contrast to a collateralized derivative contract based and collateral flows are netted. As result, the smart derivative contract generates a stream of
 * reflecting the settlement of a referenced underlying. The settlement cash flows may be daily (which is the standard frequency in traditional markets)
 * or at higher frequencies.
 * With each settlement flow the change is the (discounting adjusted) net present value of the underlying contract is exchanged and the value of the contract is reset to zero.
 *
 * To automatically process settlement, counterparties need to provide sufficient prefunded margin amounts and termination fees at the
 * beginning of each settlement cycle. Through a settlement cycle the margin amounts are locked. Simplified, the contract reverts the classical scheme of
 * 1) underlying valuation, then 2) funding of a margin call to
 * 1) pre-funding of a margin buffer (a token), then 2) settlement.
 *
 * A SDC automatically terminates the derivatives contract if there is insufficient pre-funding or if the settlement amount exceeds a
 * prefunded margin balance. Beyond mutual termination is also intended by the function specification.
 *
 * Events and Functionality specify the entire live cycle: TradeInception, TradeConfirmation, TradeTermination, Margin-Account-Mechanics, Valuation and Settlement.
 *
 * The process can be described by time points and time-intervalls which are associated with well definied states:
 * <ol>
 *  <li>t < T* (befrore incept).
 *  </li>
 *  <li>
 *      The process runs in cycles. Let i = 0,1,2,... denote the index of the cycle. Within each cycle there are times
 *      T_{i,0}, T_{i,1}, T_{i,2}, T_{i,3} with T_{i,1} = pre-funding of the Smart Contract, T_{i,2} = request valuation from oracle, T_{i,3} = perform settlement on given valuation, T_{i+1,0} = T_{i,3}.
 *  </li>
 *  <li>
 *      Given this time discretization the states are assigned to time points and time intervalls:
 *      <dl>
 *          <dt>Idle</dt>
 *          <dd>Before incept or after terminate</dd>
 *
 *          <dt>Initiation</dt>
 *          <dd>T* < t < T_{0}, where T* is time of incept and T_{0} = T_{0,0}</dd>
 *
 *          <dt>AwaitingFunding</dt>
 *          <dd>T_{i,0} < t < T_{i,1}</dd>
 *
 *          <dt>Funding</dt>
 *          <dd>t = T_{i,1}</dd>
 *
 *          <dt>AwaitingSettlement</dt>
 *          <dd>T_{i,1} < t < T_{i,2}</dd>
 *
 *          <dt>ValuationAndSettlement</dt>
 *          <dd>T_{i,2} < t < T_{i,3}</dd>
 *
 *          <dt>Settled</dt>
 *          <dd>t = T_{i,3}</dd>
 *      </dl>
 *  </li>
 * </ol>
 */

interface ISDC {
    /*------------------------------------------- EVENTS ---------------------------------------------------------------------------------------*/
    /**
     * @dev Event Emitted when a new trade is incepted from a counterparty
     * If initiating counterparty has checked tradeId from TradeInceptionEvent succesfully, it is other counterparty who needs to call confirmTrade
     */
    event TradeInceptedEvent(address initiator, string tradeId, string tradeData);

    /**
     * @dev Emitted when an incepted trade is confirmed by the opposite counterparty
     */
    event TradeConfirmedEvent(address confirmer, string tradeId);

    /**
     * @dev Emitted when a confirmed trade is set to active - e.g. when sufficient prefunding is provided by both counterparties
     */
    event TradeActivatedEvent(string tradeId);

    /**
     * @dev Emitted when a valuation is requested
     */
    event ValuationRequestEvent(string tradeData);

    /**
     * @dev Emitted when a settlent was processed succesfully
     */
    event SettlementCompletedEvent();

    /**
     * @dev Emitted to unlock a margin account
     */
    event MarginAccountUnlockedEvent();

    /**
     * @dev Emitted when margin balance was updated
     */
    event MarginAccountLockedEvent();

    /**
     * @dev Emitted when update margin requirements was requested
     */
    event MarginRequirementUpdateRequestEvent();

    /**
     * @dev Emitted when margin requirement was updated
     */
    event MarginRequirementUpdatedEvent();

    /**
     * @dev Emitted when a counterparty proactively requests an early termination
     */
    event TerminationRequestEvent(address cpAddress, string tradeId);

    /**
     * @dev Emitted when early termination request is confirmet
     */
    event TerminationConfirmedEvent(address cpAddress, string tradeId);


    /**
     * @dev Emitted when an active trade is terminated
     */
    event TerminationEvent(string cause);

    /*------------------------------------------- FUNCTIONALITY ---------------------------------------------------------------------------------------*/

    /**
     * @dev Handles trade inception, stores trade data
     * @notice emits a {TradeInceptionEvent}
     * @param _tradeData a description of the trade in sdc.xml
     */
    function inceptTrade(string memory _tradeData) external;

    /**
     * @dev Performes a matching of provided trade data, puts the state to trade confirmed if trade data match
     * @notice emits a {TradeConfirmEvent}
     * @param _tradeData a description of the trade in sdc.xml
     */
    function confirmTrade(string memory _tradeData) external;

    /**
     * @dev Called from outside to secure pre-funding. Terminate the trade if prefunding fails.
     * emits a {MarginAccountLockedEvent} followed by a {TradeActivatedEvent} or
     * emits a {TerminationEvent}
     */
    function ensurePrefunding() external;

//    function performMarginAccountCheck(uint256 balanceParty1, uint256 balanceParty2) external;

    /**
     * @dev Called from outside to trigger an external valuation and according settlement process
     * emits a {ValuationRequestEvent}
     */
    function initiateSettlement() external;

    /**
     * @dev Called from outside to trigger according settlement on chain-balances
     * callback for initiateSettlement() event handler
     * emits a {MarginAccountUnlockRequestEvent} and ({SettlementCompletedEvent} or {Termination Event}
     */
    function performSettlement(int256 settlementAmount, string memory marketData) external;

    /*
     * emits {MarginAmountUpdateRequestEvent}
     */
    function initiateMarginReqirementUpdate() external;

    /*
     * emits {MarginUpdatedEvent(bool)} fail if locked
     */
    function performMarginRequirementUpdate(address _address, uint256 amount) external;

    /**
     * @dev Called from a counterparty to request a mutual termination
    */
    function requestTradeTermination(string memory tradeId) external;

    /**
     * @dev Called from a counterparty to confirm a mutual termination, which will triggers a final settlement before trade gets inactive
     *
     */
    function confirmTradeTermination(string memory tradeId) external;
}

