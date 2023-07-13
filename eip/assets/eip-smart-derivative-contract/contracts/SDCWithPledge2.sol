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

contract SDCWithPledge2 is ISDC {
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


    event ProcessHalted();


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
        address UF_PayingParty = _upfrontPayment>0 ? receivingParty : otherParty(receivingParty);
        liquidityToken.transferFrom(UF_PayingParty,this,_upfrontPayment);
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
        // Pre-Conditions: M + P needs to be locked (i.e. pledged)
        if(_lockMarginRequirements()) {
            processState = ProcessState.AwaitingRebalancing;
            emit TradeConfirmed(msg.sender, tradeId);
        }
        else {
            tradeState == TradeState.Inactive;
            processState = ProcessState.Idle;
            emit TradeTerminated("Termination Fee could not be locked.");
        }
    }

    /**
     * Check sufficient balances and lock Termination Fees otherwise trade does not get activated
     */
    function _lockMarginRequirements() internal returns(bool) {
        uint256 marginRequirementParty1 = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee);
        uint256 marginRequirementParty2 = uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee);
        bool isAvailableParty1 = (liquidityToken.balanceOf(party1) >= marginRequirementParty1) && (liquidityToken.allowance(party1, address(this)) >= marginRequirementParty1);
        bool isAvailableParty2 = (liquidityToken.balanceOf(party2) >= marginRequirementParty2) && (liquidityToken.allowance(party2, address(this)) >= marginRequirementParty2);
        if (isAvailableParty1 && isAvailableParty2){
            liquidityToken.transferFrom(party1, address(this), marginRequirementParty1);     // transfer marginRequirementParty1 to sdc
            liquidityToken.transferFrom(party2, address(this), marginRequirementParty2);     // transfer marginRequirementParty2 to sdc
            return true;
        } else {
            return false;
        }
    }

    /*
     * Balance Check
     */

    function rebalance() external override onlyWhenAwaitingRebalancing {
        uint256 expectedSDCBalance = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee) + uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee);
        if (liquidityToken.balanceOf(this) < expectedSDCBalance){
            emit ProcessHalted("SDC Balance does not match pledged amounts");
            return;
        }
        if (tradeState == Trade.Confirmed){
            tradeState = TradeState.Active;
            processState = ProcessState.Rebalanced;
            emit ProcessRebalanced();
        }
        if (tradeState == TradeState.Active){
            processState = ProcessState.Rebalanced;
            emit ProcessRebalanced();
        }
        if (tradeState == TradeState.Terminated){
            tradeState = TradeState.Inactive;
            processState = ProcessState.Idle;
            emit ProcessHalted("Trade Terminated");
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

        if (liquidityToken.balanceOf(payerAddress) >= transferAmount &&
            liquidityToken.allowance(payerAddress,this) >= transferAmount) { /* Good case: Balances are sufficient and token has enough approval */
            liquidityToken.transferFrom(payerAddress, receiverAddress, transferAmount);
            emit ProcessAwaitingRebalancing();
            processState = ProcessState.AwaitingRebalancing;
        }
        else { // Pledge Case: transferAmount is transfered from SDC balance (i.e. pledged balance).
            liquidityToken.transfer(payerAddress, receiverAddress, transferAmount);
            liquidityToken.transfer(payerAddress, receiverAddress, terminationFee);
            liquidityToken.approve(this,payerAddress,marginRequirements[party1].buffer - transferAmount);
            liquidityToken.approve(this,party2,marginRequirements[party2].buffer + marginRequirements[party2].terminationFee);
            tradeState = TradeState.Terminated;
            emit TradeTerminated("Trade Terminated");
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
