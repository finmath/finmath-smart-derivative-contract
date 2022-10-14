// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0 <0.9.0;

/*------------------------------------------- DESCRIPTION ---------------------------------------------------------------------------------------*/

/**
 * @dev Interface specification for - a Smart Derivative Contract - to either handle trade inception and post-trade processing of
 * one or M trades between two or N counterparties.
 *
 * The concept of a Smart Derivative Contract was introduced in the following paper: https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3163074
 * A Smart Derivative Contract is a deterministic settlement protocol which has economically the same behaviour as a collateralized OTC
 * Derivative. In contrast to a collateralized derivative contract based and collateral flows are netted such that the smart derivative
 * introduces a high frequent - e.g. daily - settlement flow schedule. With each settlement flow  net present value of the underlying contract is
 * exchanged and the value of the contract is reset to zero.
 * To automatically process the settlement counterparties need to provide sufficient prefunded margin amounts and termination fees at the
 * beginning of each settlement cycle. Through a settlement cycle the margin amounts are locked.
 * A SDC contract automatically terminates the derivatives contract if there is insufficient prefunding or if the settlement amount exceeds a
 * prefunded margin balance. Beyond mutual termination is also intended by the function specification.
 *
 * Events include: TradeIncepted, TradeConfirmed, TradeActive, TradeTerminated, ValuationRequest, TerminationRequest, TerminationConfirmed, MarginLocked, MarginUpdated,
 * Functionality includes: Trade-Inception, Trade-Confirmation, Settlement-Processing, and Margin-Locks

 */
interface ISDC {
    /*------------------------------------------- EVENTS ---------------------------------------------------------------------------------------*/

    /**
     * @dev Emitted when a new trade is incepted
     * cpAdress is initiator, hence Counterparty A
     * If Counterparty B has checked tradeId from TradeInceptionEvent succesfully, it's Counterparty B's turn to call confirmTrade()
     *
     * TODO XVA:
     * Sollte es nicht auch die tradeData enthalten? Werden TradeDaten off-chain über die ID allein ermittelt?
     * TradeDaten sollten vielleicht eher im Event mitgeliefert werden, damit Counterparty B sich sicher sein kann. Aber: privacy?
     *
     */
    event TradeInceptedEvent(
        address initiator,
        string tradeId,
        string tradeData
    );

    /**
     * @dev Emitted when a new trade is confirmed by the opposite Counterparty B
     *
     *
     * TODO DZ/UI:
     * Wer lauscht auf diesen event und was passiert dann? (siehe unten bei initiateSettlement...)
     *
     */
    event TradeConfirmedEvent(address confirmer, string tradeId);

    /**
     * @dev Emitted when a new trade is set to active i.e. balance check is succesfull
     */
    event TradeActivatedEvent(string tradeId);

    /**
     * @dev global flag
     */
    event TradeTerminatedEvent(string tradeId);

    /**
     * @dev
     */
    event TerminateByMaturityEvent(string tradeId);

    /**
     * @dev Termination due to unsufficient margin balance
     */
    event TerminationDueToMarginInsufficientEvent(
        address causingParty,
        uint256 balance
    );

    /*
     * dev Termination due to exceeded margin amount
     */
    event TerminationDueToMarginExceedanceEvent(
        address causingParty,
        uint256 balance
    );

    /**
     * @dev Emitted when a valuation is requested
     */
    event ValuationRequestEvent(string tradeData, address valuationViewParty);

    /**
     * @dev Emitted when a settlent is processed
     */
    event SettlementCompletedEvent();

    /**
     * @dev Emitted to unlock a margin account
     */
    event MarginAccountUnlockRequestEvent(address _address);

    /**
     * @dev Emitted to lock a margin account
     */
    event MarginAccountLockRequestEvent(address _address);

    /**
     * @dev Emitted when margin balance was updated
     * TODO: Define parameters per counterparty?
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
     * @dev Emitted when a counterparty request an early termination
     */
    event TerminationRequestEvent(address cpAddress, string tradeId);

    /**
     * @dev Emitted when early termination request is confirmet
     */
    event TerminationConfirmedEvent(address cpAddress, string tradeId);

    /*------------------------------------------- FUNCTIONALITY ---------------------------------------------------------------------------------------*/

    /**
     * @dev Handles trade inception, stores trade data and terminationFee and marginBuffer amounts
     * emits a {TradeInceptionEvent}
     */
    function inceptTrade(string memory _tradeData) external;

    /**
     * @dev Puts the state to trade confirmed which triggers balance check
     * confirmed by other counterparty
     * emits a {TradeConfirmEvent}
     */
    function confirmTrade(string memory _tradeData) external;

    function marginAccountUnlockRequest() external;

    /**
     * @dev Called from outside to trigger a margin lock
     * emits a {MarginAccountLockRequestEvent}
     * Aufgerufen, wenn adjustment-Zeitraum vorbei
     */
    function initiateMarginAccountCheck() external;

    /**
     * @dev Called from outside to update on-chain balances
     * callback for initiateMarginLock() event handler
     * emits a {MarginAccountLockedEvent}
     * emits a {TerminationDueToMarginInsufficientEvent}
     *
     */

    /**
     *
     *
     */

    function performMarginAccountCheck(
        uint256 balanceParty1,
        uint256 balanceParty2
    ) external;

    /**
     * @dev Called from outside to trigger an external valuation and according settlement process
     * TODO: delegate to contract?
     * emits a {ValuationRequestEvent}
     * check msg.sender via modifier
     *
     * TODO DZ/UI:
     * Nachdem TradeConfirmEvent incepted wurde, können jetzt Settlements zu gegebenen Zeitpunkten initiiert werden. Aufrufe beispielsweise über externen Service zu fester Uhrzeit...
     * Sobald initiateSettlement aufgerufen wurde, soll ValuationRequestEvent beim ValuationService den Settlement-Betrag anfordern.
     * ValuationService ruft anschließend mit Settlement-Betrag performSettlement auf (siehe unten...)
     *
     */
    function initiateSettlement() external;

    /**
     * @dev Called from outside to trigger according settlement on chain-balances
     * callback for initiateSettlement() event handler
     * emits a {MarginAccountUnlockRequestEvent} and ({SettlementCompletedEvent} or {TerminationDueToMarginExceedanceEvent} was im mgn buffer ist wird verwendet zur abwicklung)
     * TODO: can emit a {PaymentRequestEvent}
     * TODO: pattern naming
     *
     * TODO DZ/UI:
     * Aufzurufen z. B. durch den externen ValuationService... Triggert gemäß dem mitglieferten Settlement-Betrag die Buchhaltung on-chain.
     * Je nachdem, ob zahlende Counterparty aureichend Margin hinterlegt hatte emit von SettlementCompletedEvent oder TerminationDueToMarginExeedanceEvent (falls Marktbewegung Erwartung übertrifft?)...
     *
     * Wenn Settlement-Buchungen on-chain erledigt sind, dann kann MarginAccountUnlockRequestEvent emitted werden.
     *
     * argument generischer halten um evtl. marktdaten als json mitliefern zu können (für spätere überprüfung der valuation z. B.)
     */
    function performSettlement(int256 settlementAmount) external;

    /*
     * emits {MarginAmountUpdateRequestEvent}
     */
    function initiateMarginReqirementUpdate() external;

    /*
     * emits {MarginUpdatedEvent(bool)} fail if locked
     */
    function performMarginRequirementUpdate(address _address, uint256 amount)
        external;

    /**
     * @dev Called from a counterparty to request a mutual termination
     * Called by both counterparties, if both agree to terminate the trade prematurely with termination fees will be transferred back to each counterparty (emergency exit)
     * TODO: What process will it trigger internally
     */
    function requestTradeTermination(string memory tradeId) external;

    /**
     * @dev Called from a counterparty to confirm a mutual termination, which will be triggered after next settlement
     *
     */
    function confirmTradeTermination(string memory tradeId) external;
}
