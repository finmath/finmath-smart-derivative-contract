// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SDC.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";
import "./SettlementToken.sol";


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

contract SDCPledgedBalance is SDC {

    modifier onlyCounterparty() {
        require(msg.sender == party1 || msg.sender == party2, "You are not a counterparty."); _;
    }
    address public immutable party1;
    address public immutable party2;
    address private immutable receivingParty; // Determine the receiver ("valuation view"): Positive values are considered to be received by receivingParty. Negative values are received by the other counterparty.


    struct MarginRequirement {
        int256 buffer;
        int256 terminationFee;
    }

    string private tradeId;
    string private tradeData;

    int256[] private settlementAmounts;
    string[] private settlementData;
    int256 upfrontPayment;
    int256 terminationPayment;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of M and P per counterparty address
    mapping(uint256 => address) private pendingRequests;               // Stores open request hashes for several requests: initiation, update and termination

    bool private mutuallyTerminated = false;

    constructor(
        address _party1,
        address _party2,
        address _receivingParty,
        address _settlementToken,
        uint256 _initialBuffer, // m
        uint256 _initalTerminationFee // p
    ) {
        party1 = _party1;
        party2 = _party2;
        require(_receivingParty == _party1 || _receivingParty == _party2, "Receiver's address is not a counterparty address!");
        receivingParty = _receivingParty;
        settlementToken = IERC20(_settlementToken); // TODO: Check if contract at given address supports interface
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
        address upfrontPayer = upfrontPayment>0 ? otherParty(receivingParty) : receivingParty;
        uint256 marginRequirementParty1 = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee + (upfrontPayer==party1 ? abs(_upfrontPayment) : int256(0)));
        uint256 marginRequirementParty2 = uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee + (upfrontPayer==party2 ? abs(_upfrontPayment) : int256(0)));
        bool isAvailableParty1 = (settlementToken.balanceOf(party1) >= marginRequirementParty1) && (settlementToken.allowance(party1, address(this)) >= marginRequirementParty1);
        bool isAvailableParty2 = (settlementToken.balanceOf(party2) >= marginRequirementParty2) && (settlementToken.allowance(party2, address(this)) >= marginRequirementParty2);
        if (isAvailableParty1 && isAvailableParty2){       // Pre-Conditions: M + P needs to be locked (i.e. pledged)
            settlementToken.transferFrom(party1, address(this), marginRequirementParty1);        // transfer marginRequirementParty1 to sdc
            settlementToken.transferFrom(party2, address(this), marginRequirementParty2);        // transfer marginRequirementParty2 to sdc
            settlementToken.transferFrom(upfrontPayer,otherParty(upfrontPayer),uint256(abs(_upfrontPayment))); // transfer upfrontPayment
            processState = ProcessState.SettlementPhase;
            emit TradeConfirmed(msg.sender, tradeId);
        }
        else {
            tradeState == TradeState.Inactive;
            processState = ProcessState.Idle;
            emit TradeTerminated("Insufficient Balance or Allowance");
        }
    }

    /*
     * Balance Check
     */
    function afterSettlement(bool success) external override onlyWhenSettlementPhase {
        /*uint256 expectedSDCBalance = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee) + uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee);
        if (settlementToken.balanceOf(this) < expectedSDCBalance){
            emit ProcessHalted("SDC Balance does not match pledged amounts");
            return;
        }*/
        if (tradeState == TradeState.Confirmed){
            tradeState = TradeState.Active;
            processState = ProcessState.Settled;
            emit ProcessSettled();
        }
        if (tradeState == TradeState.Active){
            processState = ProcessState.Settled;
            emit ProcessSettled();
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
    function initiateSettlement() external override onlyCounterparty onlyWhenSettled {
        _emitSettlementRequest();
    }

    /*
     * Performs a settelement only when processState is ValuationAndSettlement
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     * can be called only when ProcessState = ValuationAndSettlement
     */

    function performSettlement(int256 settlementAmount, string memory _settlementData) onlyWhenValuation external override {
        settlementData.push(_settlementData);
        settlementAmounts.push(settlementAmount);

        address settlementReceiver = settlementAmount > 0 ? receivingParty : otherParty(receivingParty);
        address settlementPayer = otherParty(settlementReceiver);

        if (mutuallyTerminated){
            settlementAmount = settlementAmount+terminationPayment;
        }

        uint256 transferAmount;
        if (settlementAmount > 0)
            transferAmount = uint(min( settlementAmount, int(marginRequirements[settlementPayer].buffer)));
        else
            transferAmount = uint(max( settlementAmount, -int(marginRequirements[settlementReceiver].buffer)));

        if (settlementToken.balanceOf(settlementPayer) >= transferAmount &&
            settlementToken.allowance(settlementPayer,address(this)) >= transferAmount) { /* Good case: Balances are sufficient and token has enough approval */
            settlementToken.transferFrom(settlementPayer, settlementReceiver, transferAmount);
            emit ProcessSettlementPhase();
            processState = ProcessState.SettlementPhase;
            if ( mutuallyTerminated){
                tradeState = TradeState.Terminated;
                emit TradeTerminated("Trade Terminated");
            }
        }
        else { // Pledge Case: transferAmount is transfered from SDC balance (i.e. pledged balance).
            settlementToken.transfer( settlementReceiver, uint256(transferAmount));
            settlementToken.transfer( settlementReceiver, uint256(marginRequirements[settlementPayer].terminationFee));
            settlementToken.approve(settlementPayer,uint256(marginRequirements[settlementPayer].buffer - int(transferAmount)));
            settlementToken.approve(settlementReceiver,uint256(marginRequirements[settlementReceiver].buffer));
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
    function requestTradeTermination(string memory _tradeId, int256 _terminationPayment) external override onlyCounterparty onlyWhenSettled {
        require(keccak256(abi.encodePacked(tradeId)) == keccak256(abi.encodePacked(_tradeId)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeId, "terminate", terminationPayment)));
        pendingRequests[hash] = msg.sender;
        terminationPayment = _terminationPayment;
        emit TradeTerminationRequest(msg.sender, _tradeId);
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function confirmTradeTermination(string memory _tradeId, int256 _terminationPayment) external override onlyCounterparty onlyWhenSettled {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(_tradeId, "terminate", terminationPayment)));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        mutuallyTerminated = true;
        emit TradeTerminationConfirmed(msg.sender, _tradeId);
        _emitSettlementRequest();
    }

    function _emitSettlementRequest() internal {
        processState = ProcessState.Valuation;
        uint256 latest = settlementData.length - 1;
        emit ProcessSettlementRequest(tradeData, settlementData[latest]);
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
