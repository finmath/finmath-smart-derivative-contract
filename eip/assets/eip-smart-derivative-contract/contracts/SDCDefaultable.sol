// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SDC.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract SDCDefaultable is SDC {

    event FailureToPayEvent();

    int256[] private settlementAmounts;
    string[] private settlementData;
    int256 upfrontPayment;
    int256 terminationPayment;

    mapping(uint256 => address) private pendingRequests;               // Stores open request hashes for several requests: initiation, update and termination

    bool private mutuallyTerminated = false;

    constructor(
        address _party1,
        address _party2,
        address _receivingParty,
        address _settlementToken
    ) {
        party1 = _party1;
        party2 = _party2;
        require(_receivingParty == _party1 || _receivingParty == _party2, "Receiver's address is not a counterparty address!");
        receivingParty = _receivingParty;
        settlementToken = IERC20(_settlementToken); // TODO: Check if contract at given address supports interface
        tradeState = TradeState.Inactive;
        processState = ProcessState.Idle;
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
        tradeID = Strings.toString(_hash); // TODO: TradeId must be generated on-chain in order to be unique (manage via registry)
        tradeData = _tradeData; // Set Trade Data to enable querying already in inception state
        settlementData.push(_initialSettlementData); // Store settlement data to make them available for confirming party
        emit TradeIncepted(msg.sender, tradeID, _tradeData);
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
        if (settlementToken.balanceOf(upfrontPayer)>uint(abs(_upfrontPayment))){
            settlementToken.transferFrom(upfrontPayer,otherParty(upfrontPayer),uint256(abs(_upfrontPayment))); // transfer upfrontPayment
            processState = ProcessState.SettlementPhase;
            emit TradeConfirmed(msg.sender, tradeID);
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
        if (mutuallyTerminated){
            settlementAmount = settlementAmount+terminationPayment;
        }
        if (settlementAmount!=0){
            settlementData.push(_settlementData);
            settlementAmounts.push(settlementAmount);
            address settlementReceiver = settlementAmount > 0 ? receivingParty : otherParty(receivingParty);
            address settlementPayer = otherParty(settlementReceiver);

            uint256 transferAmount = uint(settlementAmount);
/*        if (settlementAmount > 0)
            transferAmount = uint(min( settlementAmount, int(marginRequirements[settlementPayer].buffer)));
        else
            transferAmount = uint(max( settlementAmount, -int(marginRequirements[settlementReceiver].buffer)));*/

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
        }
        else {
            emit FailureToPayEvent();
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
        require(keccak256(abi.encodePacked(tradeID)) == keccak256(abi.encodePacked(_tradeId)), "Trade ID mismatch");
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




    function getBulkSettlementHistory() public view onlyCounterparty returns (int256[] memory, string[] memory) {
        return (settlementAmounts, settlementData);
    }

    function getPendingRequests(uint256 requestHash) public view returns (address) {
        return pendingRequests[requestHash];
    }

}
