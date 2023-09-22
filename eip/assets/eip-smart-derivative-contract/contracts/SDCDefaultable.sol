// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SmartDerivativeContract.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract SDCDefaultable is SmartDerivativeContract {

    event FailureToPayEvent();


    constructor(
        address _party1,
        address _party2,
        address _settlementToken)
        SmartDerivativeContract(_party1,_party2,_settlementToken) {
     }

    function processTradeAfterConfirmation(address upfrontPayer, uint256 upfrontPayment) override internal{
        settlementToken.transferFrom(upfrontPayer,otherParty(upfrontPayer),upfrontPayment);  // transfer upfrontPayment
    }
    /*
     * Balance Check
     */
    function afterTransfer(uint256 transactionHash, bool success) external override onlyWhenSettlementPhase {
        if(success){
            tradeState = TradeState.Settled;
            emit TradeSettled();
        }
        else{
            if(settlementAmounts.length>0){  // Settlement & Pledge Case: transferAmount is transfered from SDC balance (i.e. pledged balance).
                /**int256 settlementAmount = settlementAmounts[settlementAmounts.length-1];
                uint256 transferAmount;
                address settlementPayer;
                (settlementPayer, transferAmount)  = getPayerAddressAndTransferAmount(settlementAmounts[settlementAmounts.length-1]);
                address settlementReceiver = otherParty(settlementPayer);
                settlementToken.transfer(settlementReceiver, uint256(transferAmount));
                settlementToken.transfer(settlementReceiver, uint256(marginRequirements[settlementPayer].terminationFee));
                settlementToken.approve(settlementPayer,uint256(marginRequirements[settlementPayer].buffer - transferAmount)); // Release Buffers
                settlementToken.approve(settlementReceiver,uint256(marginRequirements[settlementReceiver].buffer)); // Release Buffers
                tradeState = TradeState.Terminated;
                emit TradeTerminated("Trade Terminated");**/
            }
            else{
                tradeState = TradeState.Inactive;
                emit TradeTerminated("Initial Transfer fails - Trade cannot be activated");
            }
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


}
