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

    bool private mutuallyTerminated = false;

    constructor(
        address _party1,
        address _party2,
        address _settlementToken)
        SDC(_party1,_party2,_settlementToken) {
     }

    function processTradeAfterConfirmation(uint256 upfrontPayment) override internal{
        //@Todo: Process Upfront
    }
    /*
     * Balance Check
     */
    function afterTransfer(uint256 transactionHash, bool success) external override onlyWhenSettlementPhase {
        /*uint256 expectedSDCBalance = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee) + uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee);
        if (settlementToken.balanceOf(this) < expectedSDCBalance){
            emit ProcessHalted("SDC Balance does not match pledged amounts");
            return;
        }*/
        if (tradeState == TradeState.Confirmed){
            tradeState = TradeState.Settled;
            emit TradeSettled();
        }
        if (tradeState == TradeState.InTransfer){
            tradeState = TradeState.Settled;
            emit TradeSettled();
        }
        if (tradeState == TradeState.Terminated){
            tradeState = TradeState.Inactive;
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
                emit TradeSettlementPhase();
                tradeState = TradeState.InTransfer;
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
        tradeState = TradeState.Valuation;
        uint256 latest = settlementData.length - 1;
        emit TradeSettlementRequest(tradeData, settlementData[latest]);
    }




    function getBulkSettlementHistory() public view onlyCounterparty returns (int256[] memory, string[] memory) {
        return (settlementAmounts, settlementData);
    }

    function getPendingRequests(uint256 requestHash) public view returns (address) {
        return pendingRequests[requestHash];
    }

}
