// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SmartDerivativeContract.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";
import "./SDCSettlementToken.sol";


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

contract SDCPledgedBalance is SmartDerivativeContract {

    struct MarginRequirement {
        uint256 buffer;
        uint256 terminationFee;
    }

    int256[] private settlementAmounts;
    string[] private settlementData;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of M and P per counterparty address

    constructor(
        address _party1,
        address _party2,
        address _settlementToken,
        uint256 _initialBuffer, // m
        uint256 _initalTerminationFee // p
    ) SmartDerivativeContract(_party1,_party2,_settlementToken) {
        marginRequirements[party1] = MarginRequirement(_initialBuffer, _initalTerminationFee);
        marginRequirements[party2] = MarginRequirement(_initialBuffer, _initalTerminationFee);
    }


    function processTradeAfterConfirmation(uint256 upfrontPayment) override internal{
        address upfrontPayer = upfrontPayment>0 ? otherParty(receivingParty) : receivingParty;
        uint256 marginRequirementParty1 = uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee + (upfrontPayer==party1 ? upfrontPayment : uint256(0)));
        uint256 marginRequirementParty2 = uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee + (upfrontPayer==party2 ? upfrontPayment : uint256(0)));
        bool isAvailableParty1 = (settlementToken.balanceOf(party1) >= marginRequirementParty1) && (settlementToken.allowance(party1, address(this)) >= marginRequirementParty1);
        bool isAvailableParty2 = (settlementToken.balanceOf(party2) >= marginRequirementParty2) && (settlementToken.allowance(party2, address(this)) >= marginRequirementParty2);
        if (isAvailableParty1 && isAvailableParty2){       // Pre-Conditions: M + P needs to be locked (i.e. pledged)
            settlementToken.transferFrom(party1, address(this), marginRequirementParty1);        // transfer marginRequirementParty1 to sdc
            settlementToken.transferFrom(party2, address(this), marginRequirementParty2);        // transfer marginRequirementParty2 to sdc
            settlementToken.transferFrom(upfrontPayer,otherParty(upfrontPayer),upfrontPayment);  // transfer upfrontPayment
            tradeState = TradeState.InTransfer;
            emit TradeConfirmed(msg.sender, tradeID);
        }
        else {
            tradeState = TradeState.Inactive;
            emit TradeTerminated("Insufficient Balance or Allowance");
        }
    }

    /*
     * Balance Check
     */
    function afterSettlement(uint256 transactionHash, bool success) external override onlyWhenSettlementPhase {
        _processAfterTransfer(success);
    }

    function _processAfterTransfer(bool success) internal{
        if(success){
            tradeState = TradeState.Settled;
            emit TradeSettled();
        }
        else{
            if(settlementAmounts.length>0){  // Settlement & Pledge Case: transferAmount is transfered from SDC balance (i.e. pledged balance).
                int256 settlementAmount = settlementAmounts[settlementAmounts.length-1];
                uint256 transferAmount;
                address settlementPayer;
                (settlementPayer, transferAmount)  = getPayerAddressAndTransferAmount(settlementAmounts[settlementAmounts.length-1]);
                address settlementReceiver = otherParty(settlementPayer);
                settlementToken.transfer(settlementReceiver, uint256(transferAmount));
                settlementToken.transfer(settlementReceiver, uint256(marginRequirements[settlementPayer].terminationFee));
                settlementToken.approve(settlementPayer,uint256(marginRequirements[settlementPayer].buffer - transferAmount)); // Release Buffers
                settlementToken.approve(settlementReceiver,uint256(marginRequirements[settlementReceiver].buffer)); // Release Buffers
                tradeState = TradeState.Terminated;
                emit TradeTerminated("Trade Terminated");
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
            settlementAmount = settlementAmount + terminationPayment;
        }

        settlementData.push(_settlementData);
        settlementAmounts.push(settlementAmount);

        uint256 transferAmount;
        address settlementPayer;
        (settlementPayer, transferAmount) = getPayerAddressAndTransferAmount(settlementAmount);

        if (settlementToken.balanceOf(settlementPayer) >= transferAmount &&
            settlementToken.allowance(settlementPayer,address(this)) >= transferAmount) { /* Good case: Balances are sufficient and token has enough approval */
            settlementToken.transferFrom(settlementPayer, otherParty(settlementPayer), transferAmount);
            emit TradeSettlementPhase();
            tradeState = TradeState.InTransfer;
            if ( mutuallyTerminated){
                tradeState = TradeState.Terminated;
                emit TradeTerminated("Trade Terminated");
            }
        }
        else {
            _processAfterTransfer(false);
        }
    }

    function getPayerAddressAndTransferAmount(int256 settlementAmount) internal returns(address, uint256)  {
        address settlementReceiver = settlementAmount > 0 ? receivingParty : otherParty(receivingParty);
        address settlementPayer = otherParty(settlementReceiver);

        uint256 transferAmount;
        if (settlementAmount > 0)
            transferAmount = uint(min( settlementAmount, int(marginRequirements[settlementPayer].buffer)));
        else
            transferAmount = uint(max( settlementAmount, -int(marginRequirements[settlementReceiver].buffer)));

        return (settlementPayer,transferAmount);
    }

    function processTradeAfterMutualTermination() virtual internal override{
        tradeState = TradeState.Valuation;
        emit TradeSettlementRequest(tradeData, settlementData[settlementData.length - 1]);
    }
}
