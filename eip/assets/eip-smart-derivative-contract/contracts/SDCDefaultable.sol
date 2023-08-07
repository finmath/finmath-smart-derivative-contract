// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SmartDerivativeContract.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract SDCDefaultable is SmartDerivativeContract {

    event FailureToPayEvent();

    int256[] private settlementAmounts;
    string[] private settlementData;


    constructor(
        address _party1,
        address _party2,
        address _settlementToken)
        SmartDerivativeContract(_party1,_party2,_settlementToken) {
     }

    function processTradeAfterConfirmation(uint256 upfrontPayment) override internal{
        //@Todo: Process Upfront
    }
    /*
     * Balance Check
     */
    function afterSettlement(uint256 transactionHash, bool success) external override onlyWhenSettlementPhase {
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
        tradeState = TradeState.Valuation;
        emit TradeSettlementRequest(tradeData, settlementData[settlementData.length - 1]);
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


    function processTradeAfterMutualTermination() virtual internal override{
        tradeState = TradeState.Valuation;
        emit TradeSettlementRequest(tradeData, "none");
    }
}
