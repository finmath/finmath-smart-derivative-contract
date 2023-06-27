// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./ISDC.sol";
//import "@finmath.net/sdc/contracts/ISDC.sol";
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
 *------------------------------------*
     * Setup with SDC holding tokens
     *
     *  Settlement:
     *  _bookSettlement
     *      Update internal balances
     *      Message
     *  _transferSettlement
     *      Book SDC -> Party1:   X
     *      Book SDC -> Party2:   0
     *  Rebalance (was: Perform Funding)
     *      Book Party2 -> SDC:   X
     *      Rebalance Check
     *          Failed
     *              Terminate
     *
     * Setup with Pledge Account
     *
     *  Settlement:
     *  _bookSettlement
     *      Update internal balances
     *      Message
     *  Rebalance:
     *      Book Party2 -> Party1:   X
     *      Rebalance Check
     *          Failed
     *              Book SDC -> Party1:   X
     *              Terminate

*/

contract SDCWithPledge is ISDC {
    /*
     * Trade States
     */
    enum TradeState {

        /*
         * State before the trade is incepted.
         */
        Inactive,

        /*
         * Incepted: Trade data submitted by one party. Market data for initial valuation is set.
         */
        Incepted,

        /*
         * Confirmed: Trade data accepted by other party.
         */
        Confirmed,

        /*
         * Active (Confirmend + Prefunded Termination Fees). Will cycle through process states.
         */
        Active,

        /*
         * Terminated.
         */
        Terminated
    }

    /*
     * Process States. t < T* (vor incept). The process runs in cycles. Let i = 0,1,2,... denote the index of the cycle. Within each cycle there are times
     * T_{i,0}, T_{i,1}, T_{i,2}, T_{i,3} with T_{i,1} = pre-funding of the Smart Contract, T_{i,2} = request valuation from oracle, T_{i,3} = perform settlement on given valuation, T_{i+1,0} = T_{i,3}.
     * Given this time discretization the states are assigned to time points and time intervalls:
     * Idle: Before incept or after terminate
     * Initiation: T* < t < T_{0}, where T* is time of incept and T_{0} = T_{0,0}
     * AwaitingFunding: T_{i,0} < t < T_{i,1}
     * Funding: t = T_{i,1}
     * AwaitingSettlement: T_{i,1} < t < T_{i,2}
     * ValuationAndSettlement: T_{i,2} < t < T_{i,3}
     * Settled: t = T_{i,3}
     */
    enum ProcessState {
        /**
         * @dev The process has not yet started or is terminated
         */
        Idle,
        /*
         * @dev The process is initiated (incepted, but not yet completed confimation). Next: Initiation
         */
        Initiation,
        /*
         * @dev Awaiting preparation for Rebalancing the smart contract. Next: Rebalanced
         */
        AwaitingRebalancing,
        /*
         * @dev Prefunding the smart contract. Next: AwaitingSettlement
         */
        Rebalanced,
        /*
         * @dev The valuation is initiated. Next: Awaiting Rebalancing
         */
        ValuationAndSettlement
    }



    struct MarginRequirement {
        int256 buffer;
        int256 terminationFee;
    }

    /*
     * Modifiers serve as guards whether at a specific process state a specific function can be called
     */

    modifier onlyCounterparty() {
        require(msg.sender == party1 || msg.sender == party2, "You are not a counterparty."); _;
    }
    modifier onlyWhenTradeInactive() {
        require(tradeState == TradeState.Inactive, "Trade state is not 'Inactive'."); _;
    }
    modifier onlyWhenTradeIncepted() {
        require(tradeState == TradeState.Incepted, "Trade state is not 'Incepted'."); _;
    }
    modifier onlyWhenRebalanced() {
        require(processState == ProcessState.Rebalanced, "Process state is not 'Rebalanced'."); _;
    }
    modifier onlyWhenValuationAndSettlement() {
        require(processState == ProcessState.ValuationAndSettlement, "Process state is not 'ValuationSettlement'."); _;
    }
    modifier onlyWhenAwaitingRebalancing() {
        require(processState == ProcessState.AwaitingRebalancing, "Process state is not 'AwaitingRebalancing'."); _;
    }


    TradeState private tradeState;
    ProcessState private processState;

    address public immutable party1;
    address public immutable party2;
    address private immutable receivingParty; // Determine the receiver ("valuation view"): Positive values are considered to be received by receivingParty. Negative values are received by the other counterparty.
    // TODO: Might reduce complexity, enhance performance
    // address private immutable payingPartyAddress; //--> backlog: instead of party1 party2

    /*
     * liquidityToken holds:
     * - funding account of party1
     * - funding account of party2
     * - account for SDC (sum - this is split among parties by sdcBalances)
     */
    IERC20 private immutable liquidityToken;

    string private tradeId;
    string private tradeData;

    int256[] private settlementAmounts;
    string[] private settlementData;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of M and P per counterparty address
    mapping(uint256 => address) private pendingRequests; // Stores open request hashes for several requests: initiation, update and termination

    // TODO maybe better a uinit for sdcBalances
    mapping(address => int256) private sdcBalances; // internal book-keeping: needed to track what part of the gross token balance is held by each party

    bool private mutuallyTerminated = false;

    constructor(
        address _party1,
        address _party2,
        address _receivingParty,
        address _liquidityToken,
        uint256 _initialBuffer, // m
        uint256 _initalTerminationFee // p
    ) {
        party1 = _party1;
        party2 = _party2;

        // Make sure, receiver is a cp:
        require(_receivingParty == _party1 || _receivingParty == _party2, "Receiver's address is not a counterparty address!");
        receivingParty = _receivingParty;
        // TODO
        // payingPartyAddress = otherParty(receivingParty);

        liquidityToken = IERC20(_liquidityToken); // TODO: Check if contract at given address supports interface

        tradeState = TradeState.Inactive;
        processState = ProcessState.Idle;
        marginRequirements[party1] = MarginRequirement(int256(_initialBuffer), int256(_initalTerminationFee));
        marginRequirements[party2] = MarginRequirement(int256(_initialBuffer), int256(_initalTerminationFee));
        sdcBalances[party1] = 0;
        sdcBalances[party2] = 0;
    }

    /*
     * generates a hash from tradeData and generates a map entry in openRequests
     * emits a TradeIncepted
     * can be called only when TradeState = Incepted
     */
    function inceptTrade(string memory _tradeData, string memory _initialSettlementData, int256 _upfrontPayment) external override onlyCounterparty onlyWhenTradeInactive {
        processState = ProcessState.Initiation;
        tradeState = TradeState.Incepted; // Set TradeState to Incepted

        uint256 _hash = uint256(keccak256(abi.encode(_tradeData, _initialSettlementData, _upfrontPayment)));
        pendingRequests[_hash] = msg.sender;
        tradeId = Strings.toString(_hash); // TODO: TradeId must be generated on-chain in order to be unique (manage via registry)
        tradeData = _tradeData; // Set Trade Data to enable querying already in inception state
        settlementData.push(_initialSettlementData); // Store settlement data to make them available for confirming party

        emit TradeIncepted(msg.sender, tradeId, _tradeData);
    }

    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmed
     * can be called only when TradeState = Incepted
     */
    function confirmTrade(string memory _tradeData, string memory _initialSettlementData, int256 _upfrontPayment) external override onlyCounterparty onlyWhenTradeIncepted {
        address pendingRequestParty = otherParty(msg.sender);
        uint256 _hash = uint256(keccak256(abi.encode(_tradeData, _initialSettlementData, _upfrontPayment)));

        require(pendingRequests[_hash] == pendingRequestParty, "Confirmation fails due to inconsistent trade data or wrong party address");
        delete pendingRequests[_hash]; // Delete Pending Request

        tradeState = TradeState.Confirmed;
        emit TradeConfirmed(msg.sender, tradeId);

        // Pre-Conditions: M + P needs to be locked (i.e. pledged)
        if(_lockMarginRequirements(_upfrontPayment)) {
            tradeState = TradeState.Active;
            processState = ProcessState.Rebalanced;
            emit TradeActivated(tradeId);
            emit ProcessRebalanced();
        }
    }

    /**
     * Check sufficient balances and lock Termination Fees otherwise trade does not get activated
     */
    function _lockMarginRequirements(int256 _upfrontPayment) internal returns(bool) {
        uint256 marginRequirementParty1 = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee);
        uint256 marginRequirementParty2 = uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee);

        bool isAvailableParty1 = (liquidityToken.balanceOf(party1) >= marginRequirementParty1) && (liquidityToken.allowance(party1, address(this)) >= marginRequirementParty1);
        bool isAvailableParty2 = (liquidityToken.balanceOf(party2) >= marginRequirementParty2) && (liquidityToken.allowance(party2, address(this)) >= marginRequirementParty2);
        if (isAvailableParty1 && isAvailableParty2){
            liquidityToken.transferFrom(party1, address(this), marginRequirementParty1);     // transfer marginRequirementParty1 to sdc
            liquidityToken.transferFrom(party2, address(this), marginRequirementParty2);     // transfer marginRequirementParty2 to sdc

            // Pricess _upfrontPayment
            int256 upfrontPaymentSign = (party1 == receivingParty) ?  int(1) : -1;
            adjustSDCBalances(int(marginRequirementParty1) + upfrontPaymentSign * _upfrontPayment, int(marginRequirementParty2) - upfrontPaymentSign * _upfrontPayment);             // Update internal balances
            return true;
        } else {
            tradeState == TradeState.Inactive;
            processState = ProcessState.Idle;
            emit TradeTerminated("Termination Fee could not be locked.");
            return false;
        }
    }

    /*
     * Settlement Cycle
     */

    /**
     *
     * Locks possibility of prefunding for given settlement period
     * can be called only when ProcessState = AwaitingFunding
     */
    function rebalance() external override onlyWhenAwaitingRebalancing {
        uint256 balanceParty1 = liquidityToken.balanceOf(party1);
        uint256 balanceParty2 = liquidityToken.balanceOf(party2);

        /* Calculate gap amount for each party, i.e. residual between buffer and termination fee and actual balance */
        // max(M+P - sdcBalance,0)
        // if m+p-b < 0 then balance larger than requirements and hence no gap amount
        uint256 gapAmountParty1 = uint(max(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1], 0));   // max(+X,0)
        uint256 gapAmountParty2 = uint(max(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2], 0));   // max(-X,0)

        bool party1Good = (balanceParty1 >= gapAmountParty1 && liquidityToken.allowance(party1, address(this)) >= gapAmountParty1);
        bool party2Good = (balanceParty2 >= gapAmountParty2 && liquidityToken.allowance(party2, address(this)) >= gapAmountParty2);
        /* Good case: Balances are sufficient and token has enough approval */
        if (party1Good && party2Good) {
            if(gapAmountParty1 > 0) liquidityToken.transferFrom(party1, party2, gapAmountParty1);
            if(gapAmountParty2 > 0) liquidityToken.transferFrom(party2, party1, gapAmountParty2);
            adjustSDCBalances(int(gapAmountParty1), int(gapAmountParty2));  // Update internal balances
            processState = ProcessState.Rebalanced;
            emit ProcessRebalanced();
        }
        else {
            // Bad case: sdcBalances contain information who failed: sdcBalances[party] < marginRequirements[party].buffer + marginRequirements[party].terminationFee => party failed.
            if(!party1Good){
                sdcBalances[party2] += marginRequirements[party1].terminationFee;
                sdcBalances[party1] -= marginRequirements[party1].terminationFee;
            }
            if(!party2Good){
                sdcBalances[party1] += marginRequirements[party2].terminationFee;
                sdcBalances[party2] -= marginRequirements[party2].terminationFee;
            }

            liquidityToken.transfer(party1, uint(sdcBalances[party1])); // SDC contract performs transfer to party1
            liquidityToken.transfer(party2, uint(sdcBalances[party2])); // SDC contract performs transfer to party2
            sdcBalances[party1] = 0;
            sdcBalances[party2] = 0;

            processState = ProcessState.Idle;
            tradeState = TradeState.Inactive;
            emit TradeTerminated("Trade Terminated");
        }
    }

    /*
     * Settlement can be initiated when margin accounts are locked, a valuation request event is emitted containing tradeData and valuationViewParty
     * Changes Process State to Valuation&Settlement
     * can be called only when ProcessState = Rebalanced and TradeState = Active
     */
    function initiateSettlement() external override onlyCounterparty onlyWhenRebalanced {
        processState = ProcessState.ValuationAndSettlement;
        uint256 latest = settlementData.length - 1;
        emit ProcessSettlementRequest(tradeData, settlementData[latest]);
    }

    /*
     * Performs a settelement only when processState is ValuationAndSettlement
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     * can be called only when ProcessState = ValuationAndSettlement
     */

    // TODO: WHY do we need an split at that point, book settlement and rebalance could be in one function
    // Problem: First Implementation does not fit in terms of interface functionality with the the approach here.
    function settlement(int256 settlementAmount, string memory _settlementData) onlyWhenValuationAndSettlement external override {
        settlementData.push(_settlementData);
        settlementAmounts.push(settlementAmount);

        address settlementReceiver = settlementAmount > 0 ? receivingParty : otherParty(receivingParty);
        address settlementPayer = otherParty(settlementReceiver);

        int transferAmount = 0;
        if (settlementAmount > 0)
            transferAmount = min( settlementAmount, int(marginRequirements[settlementPayer].buffer));
        else
            transferAmount = max( settlementAmount, -int(marginRequirements[settlementReceiver].buffer));

        int256 paymentSign = (party1 == receivingParty) ? int(1) : -1;
        adjustSDCBalances(paymentSign * transferAmount, -paymentSign * transferAmount);
        emit ProcessAwaitingRebalancing();
        processState = ProcessState.AwaitingRebalancing;
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
    function requestTradeTermination(string memory _tradeId) external override onlyCounterparty onlyWhenRebalanced {
        require(keccak256(abi.encodePacked(tradeId)) == keccak256(abi.encodePacked(_tradeId)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeId, "terminate")));
        pendingRequests[hash] = msg.sender;
        emit TradeTerminationRequest(msg.sender, _tradeId);
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function confirmTradeTermination(string memory _tradeId) external override onlyCounterparty onlyWhenRebalanced {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(_tradeId, "terminate")));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        mutuallyTerminated = true;
        emit TradeTerminationConfirmed(msg.sender, _tradeId);
    }

    /**
     * adds amounts to internal book-keeping
     */
    function adjustSDCBalances(int256 adjustmentAmountParty1, int256 adjustmentAmountParty2) internal {
        if (adjustmentAmountParty1 < 0)
            require(sdcBalances[party1] >= adjustmentAmountParty1, "SDC Balance Adjustment fails for Party1");
        if (adjustmentAmountParty2 < 0)
            require(sdcBalances[party2] >= adjustmentAmountParty2, "SDC Balance Adjustment fails for Party2");
        sdcBalances[party1] = sdcBalances[party1] + adjustmentAmountParty1;
        sdcBalances[party2] = sdcBalances[party2] + adjustmentAmountParty2;
    }

    /*
     * Utilities
     */

    /**
     * Absolute value of an integer
     */
    function abs(int x) private pure returns (int256) {
        return x >= 0 ? x : -x;
    }

    /**
     * Maximum value of two integers
     */
    function max(int a, int b) private pure returns (int256) {
        return a > b ? a : b;
    }

    /**
    * Minimum value of two integers
    */
    function min(int a, int b) private pure returns (int256) {
        return a < b ? a : b;
    }

    /**
     * Other party
     */
    function otherParty(address party) private view returns (address) {
        return (party == party1 ? party2 : party1);
    }

    /*
     * Setters/Getters
     * TODO: Check modifiers!
     */

    function getTokenAddress() public view returns(address) {
        return address(liquidityToken);
    }

    function getReceivingParty() public view onlyCounterparty returns (address) {
        return receivingParty;
    }

    function getTradeId() public view returns (string memory) {
        return tradeId;
    }

    function setTradeId(string memory _tradeId) public {
        tradeId = _tradeId;
    }

    function getTradeData() public view returns (string memory) {
        return tradeData;
    }

    function getTradeState() public view returns (TradeState) {
        return tradeState;
    }

    function getProcessState() public view returns (ProcessState) {
        return processState;
    }

    function getOwnSdcBalance() public view returns (int256) {
        return sdcBalances[msg.sender];
    }

    // TODO: add modifier onlyCpOrToken
    function getSdcBalancesByAddress(address _address)
        public
        view
        /* onlyCounterparty */
        returns (int256)
    {
        return sdcBalances[_address];
    }

    function getBufferAmount(address cpAddress)
        public
        view
        returns (uint256)
    {
        require(
            cpAddress == party1 || cpAddress == party2,
            "Counterparty address not known"
        );
        return uint256(marginRequirements[cpAddress].buffer);
    }

    function getTerminationFeeAmount(address cpAddress)
        public
        view
        returns (uint256)
    {
        require(
            cpAddress == party1 || cpAddress == party2,
            "Counterparty address not known"
        );
        return uint256(marginRequirements[cpAddress].terminationFee);
    }

    function getBulkSettlementHistory() public view onlyCounterparty returns (int256[] memory, string[] memory) {
        return (settlementAmounts, settlementData);
    }

    function getPendingRequests(uint256 requestHash) public view returns (address) {
        return pendingRequests[requestHash];
    }

}
