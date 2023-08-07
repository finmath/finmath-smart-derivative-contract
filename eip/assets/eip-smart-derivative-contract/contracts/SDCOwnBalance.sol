// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SmartDerivativeContract.sol";
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

contract SDCOwnBalance is SmartDerivativeContract {

    struct MarginRequirement {
        uint256 buffer;
        uint256 terminationFee;
    }

    int256[] private settlementAmounts;
    string[] private settlementData;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of M and P per counterparty address

    mapping(address => int256) private sdcBalances; // internal book-keeping: needed to track what part of the gross token balance is held for each party

    constructor(
        address counterparty1,
        address counterparty2,
        address _settlementToken,
        uint256 initialMarginRequirement,
        uint256 initalTerminationFee
    ) SmartDerivativeContract(counterparty1,counterparty2,_settlementToken) {
        marginRequirements[party1] = MarginRequirement(initialMarginRequirement, initalTerminationFee);
        marginRequirements[party2] = MarginRequirement(initialMarginRequirement, initalTerminationFee);
        sdcBalances[party1] = 0;
        sdcBalances[party2] = 0;
    }

    /**
     * Check sufficient balances and lock Termination Fees otherwise trade does not get activated
     */
    function processTradeAfterConfirmation(uint256 upfrontPayment) override internal{
        address upfrontPayer = upfrontPayment>0 ? otherParty(receivingParty) : receivingParty;
        bool isAvailableParty1 = (settlementToken.balanceOf(party1) >= marginRequirements[party1].terminationFee) && (settlementToken.allowance(party1,address(this)) >= marginRequirements[party1].terminationFee);
        bool isAvailableParty2 = (settlementToken.balanceOf(party2) >= marginRequirements[party2].terminationFee) && (settlementToken.allowance(party2,address(this)) >= marginRequirements[party2].terminationFee);
        if (isAvailableParty1 && isAvailableParty2){
            adjustSDCBalances(int256(marginRequirements[party1].terminationFee), int(marginRequirements[party2].terminationFee)); // Update internal balances
            settlementToken.transferFrom(party1, address(this), marginRequirements[party1].terminationFee); // transfer termination fee party1 to sdc
            settlementToken.transferFrom(party2, address(this), marginRequirements[party2].terminationFee); // transfer termination fee party2 to sdc
            settlementToken.transferFrom(upfrontPayer,otherParty(upfrontPayer),upfrontPayment);  // transfer upfrontPayment
            tradeState = TradeState.InTransfer;
            emit TradeConfirmed(msg.sender, tradeID);
        }
        else{
            tradeState == TradeState.Inactive;
            emit TradeTerminated("Termination Fee could not be locked - Trade cannot be activated");
        }
    }

    /*
     * Failsafe: Free up accounts upon termination
     */
    function _processTermination() internal {
        settlementToken.transfer(party1, uint256(sdcBalances[party1]));
        settlementToken.transfer(party2, uint256(sdcBalances[party2]));
        tradeState = TradeState.Terminated;
    }

    /*
     * Settlement Cycle
     */

    /*
     * Send an Lock Request Event only when Process State = Funding
     * Puts Process state to Margin Account Check
     * can be called only when ProcessState = AwaitingFunding
     */
    function afterSettlement(uint256 transactionHash, bool success) external override onlyWhenSettlementPhase {
        uint256 balanceParty1 = settlementToken.balanceOf(party1);
        uint256 balanceParty2 = settlementToken.balanceOf(party2);

        /* Calculate gap amount for each party, i.e. residual between buffer and termination fee and actual balance */
        // max(M+P - sdcBalance,0)
        uint gapAmountParty1 = int(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee) - sdcBalances[party1] > 0 ? uint(int(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee) - sdcBalances[party1]) : 0;
        uint gapAmountParty2 = int(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee) - sdcBalances[party2] > 0 ? uint(int(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee) - sdcBalances[party2]) : 0;

        /* Good case: Balances are sufficient and token has enough approval */
        if ( (balanceParty1 >= gapAmountParty1 && settlementToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && settlementToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            settlementToken.transferFrom(party1, address(this), gapAmountParty1);  // Transfer of GapAmount to sdc contract
            settlementToken.transferFrom(party2, address(this), gapAmountParty2);  // Transfer of GapAmount to sdc contract
            tradeState = TradeState.Settled;
            adjustSDCBalances(int(gapAmountParty1),int(gapAmountParty2));  // Update internal balances
            emit TradeSettled();
        }
        /* Party 1 - Bad case: Balances are insufficient or token has not enough approval */
        else if ( (balanceParty1 < gapAmountParty1 || settlementToken.allowance(party1,address(this)) < gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && settlementToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            tradeState = TradeState.Terminated;
            adjustSDCBalances(-int(marginRequirements[party1].terminationFee),int(marginRequirements[party1].terminationFee)); // Update internal balances

            _processTermination(); // Release all buffers
            emit TradeTerminated("Termination caused by party1 due to insufficient prefunding");
        }
        /* Party 2 - Bad case: Balances are insufficient or token has not enough approval */
        else if ( (balanceParty1 >= gapAmountParty1 && settlementToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 < gapAmountParty2 || settlementToken.allowance(party2,address(this)) < gapAmountParty2) ) {
            tradeState = TradeState.Terminated;

            adjustSDCBalances(int(marginRequirements[party2].terminationFee),-int(marginRequirements[party2].terminationFee)); // Update internal balances

            _processTermination(); // Release all buffers
            emit TradeTerminated("Termination caused by party2 due to insufficient prefunding");
        }
        /* Both parties fail: Cross Transfer of Termination Fee */
        else {
            tradeState = TradeState.Terminated;
            // if ( (balanceParty1 < gapAmountParty1 || settlementToken.allowance(party1,address(this)) < gapAmountParty1) &&  (balanceParty2 < gapAmountParty2 || settlementToken.allowance(party2,address(this)) < gapAmountParty2) ) { tradeState = TradeState.Terminated;
            adjustSDCBalances(int(marginRequirements[party2].terminationFee)-int(marginRequirements[party1].terminationFee),int(marginRequirements[party1].terminationFee)-int(marginRequirements[party2].terminationFee)); // Update internal balances: Cross Booking of termination fee

            _processTermination(); // Release all buffers
            emit TradeTerminated("Termination caused by both parties due to insufficient prefunding");
        }
    }

    /*
     * Settlement can be initiated when margin accounts are locked, a valuation request event is emitted containing tradeData and valuationViewParty
     * Changes Process State to Valuation&Settlement
     * can be called only when ProcessState = Funded and TradeState = Active
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
    function performSettlement(int256 settlementAmount, string memory _settlementData) onlyWhenValuation external override
    {
        settlementData.push(_settlementData);
        settlementAmounts.push(settlementAmount);

        address receiver  = settlementAmount > 0 ? receivingParty : otherParty(receivingParty);
        address payer     = otherParty(receiver);

        bool noTermination = uint(abs(settlementAmount)) <= marginRequirements[payer].buffer;
        int256 transferAmount = (noTermination == true) ? abs(settlementAmount) : int(marginRequirements[payer].buffer + marginRequirements[payer].terminationFee); // Override with Buffer and Termination Fee: Max Transfer

        if(receiver == party1)  // Adjust internal Balances, only debit is booked on sdc balance as receiving party obtains transfer amount directly from sdc
            adjustSDCBalances(0, -transferAmount);
        else
            adjustSDCBalances(-transferAmount, 0);

        settlementToken.transfer(receiver, uint256(transferAmount)); // SDC contract performs transfer to receiving party

        if (noTermination) {   // Regular Settlement
            emit TradeSettlementPhase();
            tradeState = TradeState.InTransfer;  // Set TradeState to 'InTransfer'
        } else {  // Termination Event, buffer not sufficient, transfer margin buffer and termination fee and process termination
            tradeState = TradeState.Terminated;
            _processTermination(); // Transfer all locked amounts
            emit TradeTerminated("Termination due to margin buffer exceedance");
        }

        if (mutuallyTerminated) {  // Both counterparties agreed on a premature termination
            _processTermination();
        }
    }


    function adjustSDCBalances(int256 adjustmentAmountParty1, int256 adjustmentAmountParty2) internal {
        if (adjustmentAmountParty1 < 0)
            require(sdcBalances[party1] >= adjustmentAmountParty1, "SDC Balance Adjustment fails for Party1");
        if (adjustmentAmountParty2 < 0)
            require(sdcBalances[party2] >= adjustmentAmountParty2, "SDC Balance Adjustment fails for Party2");
        sdcBalances[party1] = sdcBalances[party1] + adjustmentAmountParty1;
        sdcBalances[party2] = sdcBalances[party2] + adjustmentAmountParty2;
    }

    function getOwnSdcBalance() public view returns (int256) {
        return sdcBalances[msg.sender];
    }

    function processTradeAfterMutualTermination() virtual internal override{
        tradeState = TradeState.Valuation;
        emit TradeSettlementRequest(tradeData, settlementData[settlementData.length - 1]);
    }

}