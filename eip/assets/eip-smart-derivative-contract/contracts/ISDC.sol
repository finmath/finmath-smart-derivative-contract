// SPDX-License-Identifier: MIT
pragma solidity >=0.7.0 <0.9.0;

/*------------------------------------------- DESCRIPTION ---------------------------------------------------------------------------------------*/

/**
 * @dev Interface specification for a Smart Derivative Contract which  specifies post-trade live cycle of an OTC derivative in a complete deterministic way.
 * Counterparty Risk is removed by construction.
 *
 * A Smart Derivative Contract is a deterministic settlement protocol which has economically the same behaviour as a collateralized OTC
 * Derivative. Its aim is to remove many of the inefficiencies in collateralized OTC transactions and removes counterparty credit risk by construction.

 * In contrast to a collateralized derivative contract based and collateral flows are netted such that the smart derivative
 * introduces a high frequent - e.g. daily - settlement flow schedule. With each settlement flow the net present value of the underlying contract is
 * exchanged and the value of the contract is reset to zero.
 *
 * To automatically process settlement counterparties need to provide sufficient prefunded margin amounts and termination fees at the
 * beginning of each settlement cycle. Through a settlement cycle the margin amounts are locked.
 * A SDC contract automatically terminates the derivatives contract if there is insufficient prefunding or if the settlement amount exceeds a
 * prefunded margin balance. Beyond mutual termination is also intended by the function specification.
 *
 * Events and Functionality specify the entire live cycle: TradeInception, TradeConfirmation, TradeTermination, Margin-Account-Mechanics, Valuation and Settlement.
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
     * emits a {TradeInceptionEvent}
     */
    function inceptTrade(string memory _tradeData) external;

    /**
     * @dev Performes a matching of provided trade data, puts the state to trade confirmed if trade data match
     * emits a {TradeConfirmEvent}
     */
    function confirmTrade(string memory _tradeData) external;

    /**
     * @dev Prefunding Period is initialised, parties will be able to increase approvals on liquidity token
     * emits a {MarginAccountUnlockedEvent}
     */
    function marginAccountUnlockRequest() external;

    /**
     * @dev Called from outside to trigger a margin lock
     * emits a {MarginAccountLockRequestEvent}
     */
    function initiateMarginAccountCheck() external;

    /**
     * @dev Called from outside to update on-chain balances
     * may serve as callback for initiateMarginAccountCheck()
     * emits a {MarginAccountLockedEvent} or
     * emits a {TerminationEvent}
     */
    function performMarginAccountCheck(uint256 balanceParty1, uint256 balanceParty2) external;

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

