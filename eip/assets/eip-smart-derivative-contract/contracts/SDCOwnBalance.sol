// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SDC.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";


/**
 * @title Reference Implementation of ERC6123 - Smart Derivative Contract
 * @notice This reference implementation is based on a finite state machine with predefined trade and process states (see enums below)
 * Some comments on the implementation:
 * - trade and process states are used in modifiers to check which function is able to be called at which state
 * - trade data are stored in the contract
 * - trade data matching is done in incept and confirm routine (comparing the hash of the provided data)
 * - ERC-20 token is used for three participants: counterparty1 and counterparty2 and sdc
 * - when prefunding is done sdc contract will hold agreed amounts and perform settlement on those
 * - sdc also keeps track on internal balances for each counterparty
 * - during prefunding sdc will transfer required amounts to its own balance - therefore sufficient approval is needed
 * - upon termination all remaining 'locked' amounts will be transferred back to the counterparties
*/

contract SDCOwnBalance is SDC {

    struct MarginRequirement {
        int256 buffer;
        int256 terminationFee;
    }

    modifier onlyCounterparty() {
        require(msg.sender == party1 || msg.sender == party2, "You are not a counterparty."); _;
    }

    address public party1;
    address public party2;
    address private immutable receivingPartyAddress; // Determine the receiver: Positive values are consider to be received by receivingPartyAddress. Negative values are received by the other counterparty.

    string private tradeID;
    string private tradeData;
    string private lastSettlementData;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of M and P per counterparty address
    mapping(uint256 => address) private pendingRequests; // Stores open request hashes for several requests: initiation, update and termination

    mapping(address => int256) private sdcBalances; // internal book-keeping: needed to track what part of the gross token balance is held for each party


    bool private mutuallyTerminated = false;

    constructor(
        address counterparty1,
        address counterparty2,
        address receivingParty,
        address tokenAddress,
        uint256 initialMarginRequirement,
        uint256 initalTerminationFee
    ) {
        party1 = counterparty1;
        party2 = counterparty2;
        receivingPartyAddress = receivingParty;
        settlementToken = IERC20(tokenAddress);
        tradeState = TradeState.Inactive;
        processState = ProcessState.Idle;
        marginRequirements[party1] = MarginRequirement(int256(initialMarginRequirement), int256(initalTerminationFee));
        marginRequirements[party2] = MarginRequirement(int256(initialMarginRequirement), int256(initalTerminationFee));
        sdcBalances[party1] = 0;
        sdcBalances[party2] = 0;
    }

    /*
     * generates a hash from tradeData and generates a map entry in openRequests
     * emits a TradeIncepted
     * can be called only when TradeState = Incepted
     */
    function inceptTrade(string memory _tradeData, string memory _initialSettlementData, int256 _upfrontPayment) external override onlyCounterparty onlyWhenTradeInactive
    {
        processState = ProcessState.Initiation;
        tradeState = TradeState.Incepted; // Set TradeState to Incepted


        uint256 hash = uint256(keccak256(abi.encode(_tradeData, _initialSettlementData, _upfrontPayment)));
        pendingRequests[hash] = msg.sender;
        tradeID = Strings.toString(hash);
        tradeData = _tradeData; // Set trade data to enable querying already in inception state
        lastSettlementData = _initialSettlementData; // Store settlement data to make them available for confirming party

        emit TradeIncepted(msg.sender, tradeID, _tradeData);
    }

    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmed
     * can be called only when TradeState = Incepted
     */
    function confirmTrade(string memory _tradeData, string memory _initialSettlementData, int256 _upfrontPayment) external override onlyCounterparty onlyWhenTradeIncepted
    {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 tradeIDConf = uint256(keccak256(abi.encode(_tradeData, _initialSettlementData, _upfrontPayment)));
        require(pendingRequests[tradeIDConf] == pendingRequestParty, "Confirmation fails due to inconsistent trade data or wrong party address");
        delete pendingRequests[tradeIDConf]; // Delete Pending Request

        tradeState = TradeState.Confirmed;
        emit TradeConfirmed(msg.sender, tradeID);

        // Pre-Conditions
        if(_lockTerminationFees()) {
            tradeState = TradeState.Active;
            emit TradeActivated(tradeID);

            processState = ProcessState.SettlementPhase;
            emit ProcessSettlementPhase();
        }
    }

    /**
     * Check sufficient balances and lock Termination Fees otherwise trade does not get activated
     */
    function _lockTerminationFees() internal returns(bool) {
        bool isAvailableParty1 = (settlementToken.balanceOf(party1) >= uint(marginRequirements[party1].terminationFee)) && (settlementToken.allowance(party1,address(this)) >= uint(marginRequirements[party1].terminationFee));
        bool isAvailableParty2 = (settlementToken.balanceOf(party2) >= uint(marginRequirements[party2].terminationFee)) && (settlementToken.allowance(party2,address(this)) >= uint(marginRequirements[party2].terminationFee));
        if (isAvailableParty1 && isAvailableParty2){
            settlementToken.transferFrom(party1, address(this), uint(marginRequirements[party1].terminationFee)); // transfer termination fee party1 to sdc
            settlementToken.transferFrom(party2, address(this), uint(marginRequirements[party2].terminationFee)); // transfer termination fee party2 to sdc
            adjustSDCBalances(marginRequirements[party1].terminationFee, marginRequirements[party2].terminationFee); // Update internal balances
            return true;
        }
        else{
            tradeState == TradeState.Inactive;
            processState = ProcessState.Idle;
            emit TradeTerminated("Termination Fee could not be locked.");
            return false;
        }
    }

    /*
     * Failsafe: Free up accounts upon termination
     */
    function _processTermination() internal {
        settlementToken.transfer(party1, uint256(sdcBalances[party1]));
        settlementToken.transfer(party2, uint256(sdcBalances[party2]));

        processState = ProcessState.Idle;
        tradeState = TradeState.Inactive;
    }

    /*
     * Settlement Cycle
     */

    /*
     * Send an Lock Request Event only when Process State = Funding
     * Puts Process state to Margin Account Check
     * can be called only when ProcessState = AwaitingFunding
     */
    function afterSettlement(bool success) external override onlyWhenSettlementPhase {

        uint256 balanceParty1 = settlementToken.balanceOf(party1);
        uint256 balanceParty2 = settlementToken.balanceOf(party2);

        /* Calculate gap amount for each party, i.e. residual between buffer and termination fee and actual balance */
        // max(M+P - sdcBalance,0)
        uint gapAmountParty1 = marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1] > 0 ? uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1]) : 0;
        uint gapAmountParty2 = marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2] > 0 ? uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2]) : 0;

        /* Good case: Balances are sufficient and token has enough approval */
        if ( (balanceParty1 >= gapAmountParty1 && settlementToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && settlementToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            settlementToken.transferFrom(party1, address(this), gapAmountParty1);  // Transfer of GapAmount to sdc contract
            settlementToken.transferFrom(party2, address(this), gapAmountParty2);  // Transfer of GapAmount to sdc contract
            processState = ProcessState.Settled;
            adjustSDCBalances(int(gapAmountParty1),int(gapAmountParty2));  // Update internal balances
            emit ProcessSettled();
        }
        /* Party 1 - Bad case: Balances are insufficient or token has not enough approval */
        else if ( (balanceParty1 < gapAmountParty1 || settlementToken.allowance(party1,address(this)) < gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && settlementToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            tradeState = TradeState.Terminated;
            adjustSDCBalances(-marginRequirements[party1].terminationFee,marginRequirements[party1].terminationFee); // Update internal balances

            _processTermination(); // Release all buffers
            emit TradeTerminated("Termination caused by party1 due to insufficient prefunding");
        }
        /* Party 2 - Bad case: Balances are insufficient or token has not enough approval */
        else if ( (balanceParty1 >= gapAmountParty1 && settlementToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 < gapAmountParty2 || settlementToken.allowance(party2,address(this)) < gapAmountParty2) ) {
            tradeState = TradeState.Terminated;

            adjustSDCBalances(marginRequirements[party2].terminationFee,-marginRequirements[party2].terminationFee); // Update internal balances

            _processTermination(); // Release all buffers
            emit TradeTerminated("Termination caused by party2 due to insufficient prefunding");
        }
        /* Both parties fail: Cross Transfer of Termination Fee */
        else {
            tradeState = TradeState.Terminated;
            // if ( (balanceParty1 < gapAmountParty1 || settlementToken.allowance(party1,address(this)) < gapAmountParty1) &&  (balanceParty2 < gapAmountParty2 || settlementToken.allowance(party2,address(this)) < gapAmountParty2) ) { tradeState = TradeState.Terminated;
            adjustSDCBalances(marginRequirements[party2].terminationFee-marginRequirements[party1].terminationFee,marginRequirements[party1].terminationFee-marginRequirements[party2].terminationFee); // Update internal balances: Cross Booking of termination fee

            _processTermination(); // Release all buffers
            emit TradeTerminated("Termination caused by both parties due to insufficient prefunding");
        }
    }

    /*
     * Settlement can be initiated when margin accounts are locked, a valuation request event is emitted containing tradeData and valuationViewParty
     * Changes Process State to Valuation&Settlement
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function initiateSettlement() external override onlyCounterparty onlyWhenSettled
    {
        processState = ProcessState.Valuation;
        emit ProcessSettlementRequest(tradeData, lastSettlementData);
    }

    /*
     * Performs a settelement only when processState is ValuationAndSettlement
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     * can be called only when ProcessState = ValuationAndSettlement
     */
    function performSettlement(int256 settlementAmount, string memory settlementData) onlyWhenValuation external override
    {
        lastSettlementData = settlementData;
        address receivingParty  = settlementAmount > 0 ? receivingPartyAddress : other(receivingPartyAddress);
        address payingParty     = other(receivingParty);

        bool noTermination = abs(settlementAmount) <= marginRequirements[payingParty].buffer;
        int256 transferAmount = (noTermination == true) ? abs(settlementAmount) : marginRequirements[payingParty].buffer + marginRequirements[payingParty].terminationFee; // Override with Buffer and Termination Fee: Max Transfer

        if(receivingParty == party1)  // Adjust internal Balances, only debit is booked on sdc balance as receiving party obtains transfer amount directly from sdc
            adjustSDCBalances(0, -transferAmount);
        else
            adjustSDCBalances(-transferAmount, 0);

        settlementToken.transfer(receivingParty, uint256(transferAmount)); // SDC contract performs transfer to receiving party

        if (noTermination) {   // Regular Settlement
            emit ProcessSettlementPhase();
            processState = ProcessState.SettlementPhase;  // Set ProcessState to 'AwaitingFunding'
        } else {  // Termination Event, buffer not sufficient, transfer margin buffer and termination fee and process termination
            tradeState = TradeState.Terminated;
            _processTermination(); // Transfer all locked amounts
            emit TradeTerminated("Termination due to margin buffer exceedance");
        }

        if (mutuallyTerminated) {  // Both counterparties agreed on a premature termination
            _processTermination();
        }
    }

    /*
     * End of Cycle
     */

    /*
     * Can be called by a party for mutual termination
     * Hash is generated an entry is put into pendingRequests
     * TerminationRequest is emitted
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function requestTradeTermination(string memory _tradeID, int256 _terminationPayment) external override onlyCounterparty onlyWhenSettled
    {
        require(keccak256(abi.encodePacked(tradeID)) == keccak256(abi.encodePacked(_tradeID)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeID, "terminate")));
        pendingRequests[hash] = msg.sender;
        emit TradeTerminationRequest(msg.sender, _tradeID);
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function confirmTradeTermination(string memory tradeId, int256 _terminationPayment) external override onlyCounterparty onlyWhenSettled
    {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(tradeId, "terminate")));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        mutuallyTerminated = true;
        emit TradeTerminationConfirmed(msg.sender, tradeID);
    }

    function adjustSDCBalances(int256 adjustmentAmountParty1, int256 adjustmentAmountParty2) internal {
        if (adjustmentAmountParty1 < 0)
            require(sdcBalances[party1] >= adjustmentAmountParty1, "SDC Balance Adjustment fails for Party1");
        if (adjustmentAmountParty2 < 0)
            require(sdcBalances[party2] >= adjustmentAmountParty2, "SDC Balance Adjustment fails for Party2");
        sdcBalances[party1] = sdcBalances[party1] + adjustmentAmountParty1;
        sdcBalances[party2] = sdcBalances[party2] + adjustmentAmountParty2;
    }


    /**
     * Other party
     */
    function other(address party) private view returns (address) {
        return (party == party1 ? party2 : party1);
    }

    function getOwnSdcBalance() public view returns (int256) {
        return sdcBalances[msg.sender];
    }

}