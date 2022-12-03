// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0 <0.9.0;

import "./ISDC.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract SDC is ISDC {
    /*
     * Trade States
     */
    enum TradeState {
        Inactive,
        Incepted,
        Confirmed,
        Active,
        Terminated
    }

    /*
     * Process States
     */
    enum ProcessState {
        Idle,
        Initiation,
        Funding,
        MarginAccountCheck,
        MarginAccountLocked,
        ValuationAndSettlement,
        Settled
    }

    struct MarginRequirement {
        int256 buffer;
        int256 terminationFee;
    }


    modifier onlyCounterparty() {
        require(msg.sender == party1 || msg.sender == party2, "You are not a counterparty.");
        _;
    }

    TradeState private tradeState;
    ProcessState private processState;

    address public party1;
    address public party2;

    IERC20 private liquidityToken;

    string private tradeID;
    string private tradeData;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of margin-buffer and terminationfee per counterparty address
    mapping(uint256 => address) private pendingRequests; // Stores open request hashes for several requests: initiation, update and termination

    mapping(address => int256) private sdcBalances; // internal book-keeping: needed to track what part of the gross token balance is held for each party

    address private immutable valuationViewPartyAddress; // immutable variable how to derive payment request from settlement amount, set to party 1


    string[] private settlementTimestamps;
    int256[] private settlementAmounts;
    string[] private marketData;

    constructor(
        address _party1,
        address _party2,
        address tokenAddress,
        uint256 initialMarginRequirement,
        uint256 initalTerminationFee
    ) {
        party1 = _party1;
        party2 = _party2;
        valuationViewPartyAddress = party1;
        liquidityToken = IERC20(tokenAddress);  // token is based on ERC20 standard
        tradeState = TradeState.Inactive;
        processState = ProcessState.Idle;
        marginRequirements[party1] = MarginRequirement(int256(initialMarginRequirement), int256(initalTerminationFee) );
        marginRequirements[party2] = MarginRequirement(int256(initialMarginRequirement), int256(initalTerminationFee) );
        sdcBalances[party1] = 0;    // initial internal balances are set to zero
        sdcBalances[party2] = 0;
    }

    /**
     * @dev Handles trade inception, stores trade data
     * emits a {TradeInceptionEvent}
     */
    function inceptTrade(string memory _tradeData) external override onlyCounterparty
    {
        processState = ProcessState.Initiation;
        uint256 hash = uint256(keccak256(abi.encode(_tradeData)));  // generate a hash as trade id from trade data
        tradeState = TradeState.Incepted; // Set TradeState to Incepted
        pendingRequests[hash] = msg.sender;
        tradeData = _tradeData;     // Set Trade Data to enable querying already in inception state
        emit TradeInceptedEvent(msg.sender, hash, _tradeData);
    }

    /**
     * @dev Performes a matching of provided trade data, puts the state to trade confirmed if trade data match
     * emits a {TradeConfirmEvent}
     */
    function confirmTrade(string memory _tradeData) external override onlyCounterparty
    {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 tradeIDConf = uint256(keccak256(abi.encode(_tradeData)));
        require(pendingRequests[tradeIDConf] == pendingRequestParty, "Confirmation fails due to inconsistent trade data or wrong party address");
        delete pendingRequests[tradeIDConf]; // Delete Pending Request
        tradeState = TradeState.Confirmed;
        emit TradeConfirmedEvent(msg.sender, tradeID); // subscribe event und handler, h löst tx aus gegen token
    }

    /*
     * Unlocks a Margin Amount only when Trade is Initiated or if Trade is Active and Process State is Settled
     * Puts Process State to Funding
     */
    function marginAccountUnlockRequest() external override onlyWhenConfirmedOrSettled
    {
        _processMarginUnlock(); // call of internal function
        emit MarginAccountUnlockedEvent(msg.sender);
    }

    function _processMarginUnlock() internal {
        if (tradeState == TradeState.Confirmed) {
            processState = ProcessState.Funding;
            return;
        }

        if (tradeState == TradeState.Active && processState == ProcessState.Settled) {
            liquidityToken.approve(party1, uint256(sdcBalances[party1] - marginRequirements[party1].terminationFee) ); // unlock all except termination fee
            liquidityToken.approve(party2, uint256(sdcBalances[party2] - marginRequirements[party2].terminationFee) ); // unlock all except termination fee
            processState = ProcessState.Funding; // Funding Period is started
        }

        if (tradeState == TradeState.Terminated) {    // Process Termination - Release all locked tokens
            liquidityToken.approve(party1, uint256(sdcBalances[party1]));
            liquidityToken.approve(party2, uint256(sdcBalances[party2]));
            processState = ProcessState.Idle;
            tradeState = TradeState.Inactive;
        }
    }

    /*
     * Send an Lock Request Event only when Process State = Funding
     * Puts Process state to Margin Account Check
     */
    function initiateMarginAccountCheck() external override onlyWhenFunding {
        processState = ProcessState.MarginAccountCheck;
        emit MarginAccountLockRequestEvent(msg.sender);
        uint256 balance1 = liquidityToken.balanceOf(party1);
        uint256 balance2 = liquidityToken.balanceOf(party2);
        _processMarginLock(balance1, balance2);
    }

    /*
     * Only when State = MarginAccountCheck
     * Checks balances for each party and sends PaymentRequest on Termination
     * If successfull checked TradeState is put to Active, ProcessState is put to MarginAccountLocked
     */
    function performMarginAccountCheck(uint256 balanceParty1, uint256 balanceParty2) external override onlyWhenMarginAccountCheck {
        _processMarginLock(balanceParty1, balanceParty2);
    }

    function _processMarginLock(uint balanceParty1, uint balanceParty2) internal {
        if (tradeState == TradeState.Confirmed){    // In case of confirmation state - transfer termination Fees in case contract is in confirmTrade-State
            try liquidityToken.transferFrom(party1,address(this),uint(marginRequirements[party1].terminationFee)) {
            } catch Error(string memory reason){
                tradeState = TradeState.Inactive;
                processState = ProcessState.Idle;
                return;
            }
            try liquidityToken.transferFrom(party2,address(this),uint(marginRequirements[party2].terminationFee)) {
            }  catch Error(string memory reason){
                tradeState == TradeState.Inactive;
                processState = ProcessState.Idle;
                return;
            }
            adjustSDCBalances(marginRequirements[party1].terminationFee, marginRequirements[party2].terminationFee); // Update internal balances

            /* adjust balances: substract difference from balanceParty1 and balanceParty2 after transfer of termination fee
              since balanceParty1 and balanceParty2 *keep mirroring the external amounts* off which the contract gets funded */
            balanceParty1 = balanceParty1 - uint(marginRequirements[party1].terminationFee);
            balanceParty2 = balanceParty2 - uint(marginRequirements[party2].terminationFee);
        }

        /* Calculate gap amount for each party, i.e. residual between buffer and termination fee and actual balance*/
        uint gapAmountParty1 = marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1] > 0 ? uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1]) : 0;
        uint gapAmountParty2 = marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2] > 0 ? uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2]) : 0;

        /* Good case: Balances are sufficient and token has enough approval */
        if ( (balanceParty1 >= gapAmountParty1 && liquidityToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && liquidityToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            liquidityToken.approve(party1,0);    // Remove outstanding Approval for party1
            liquidityToken.approve(party2,0);   // Remove outstanding Approval for party2
            liquidityToken.transferFrom(party1,address(this),gapAmountParty1);  // Transfer of GapAmount to sdc contract
            liquidityToken.transferFrom(party2,address(this),gapAmountParty2);  // Transfer of GapAmount to sdc contract
            adjustSDCBalances(int(gapAmountParty1),int(gapAmountParty2));  // Update internal balances
            tradeState = TradeState.Active;
            processState = ProcessState.MarginAccountLocked;
            emit MarginAccountLockedEvent();
            emit TradeActivatedEvent(tradeID);
        }
        else{  /* Bad case: Balances are insufficient or token has not enough approval */
            address terminatingParty = (balanceParty1 < gapAmountParty1 || liquidityToken.allowance(party1,address(this)) < gapAmountParty1) ? party1 : party2;
            tradeState = TradeState.Terminated;
            liquidityToken.transfer(party2,uint(marginRequirements[party1].terminationFee));   // Transfer termination fee to party2
            adjustSDCBalances(-marginRequirements[party1].terminationFee,marginRequirements[party1].terminationFee); // Update internal balances
            _processMarginUnlock(); // Release all buffers
            emit TradeTerminatedEvent();
        }
    }

    /*
     * Settlement can be initiated when margin accounts are locked, a valuation request event is emitted containing tradeData and valuationViewParty
     * Changes Process State to Valuation&Settlement
     */
    function initiateSettlement() external override onlyWhenMarginAccountLocked onlyWhenActive
    {
        processState = ProcessState.ValuationAndSettlement;
        emit ValuationRequestEvent(tradeData);
    }

    /*
     * Performs a settelement only when processState is ValuationAndSettlement
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     */
    function performSettlement(int256 settlementAmount) external override onlyWhenValuationAndSettlement onlyWhenActive
    {
        settlementTimestamps.push(_timestamp);
        settlementAmounts.push(_amount);
        marketData.push(_marketData);

        int256 transferAmount = settlementAmount < 0 ? -settlementAmount : settlementAmount; // casting
        address payingParty = settlementAmount < 0 ? valuationViewPartyAddress : (valuationViewPartyAddress == party1 ? party2 : party1);
        address receivingParty = payingParty == party1 ? party2 : party1;

        if (transferAmount > marginRequirements[payingParty].buffer) {
            // Termination Event, buffer not sufficient
            tradeState = TradeState.Terminated;
            transferAmount = marginRequirements[payingParty].buffer + marginRequirements[payingParty].terminationFee; // Override with Buffer and Termination Fee: Max Transfer
            liquidityToken.transfer(receivingParty, uint256(transferAmount)); // SDC performs transfer to receiving party
            emit TerminationEvent("Margin Buffer Exceedance");
        } else {
            // Regular Settlement
            liquidityToken.transfer(receivingParty, uint256(transferAmount)); // SDC Contract performs transfer to receiving party
            emit SettlementCompletedEvent();
        }
        payingParty == party1 ? adjustSDCBalances(-transferAmount,0) : adjustSDCBalances(0,-transferAmount); // Update Internal Balances
        processState = ProcessState.Settled;  // Set Process State to Settled
    }

    /*
     * Can be called by a party for mutual termination
     * Hash is generated an entry is put into pendingRequests
     * TerminationRequest is emitted
     */
    function requestTradeTermination(string memory _tradeID) external override onlyCounterparty onlyWhenActive onlyWhenMarginAccountLocked
    {
        require(keccak256(abi.encodePacked(tradeID)) == keccak256(abi.encodePacked(_tradeID)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeID, "terminate")));
        pendingRequests[hash] = msg.sender;
        emit TerminationRequestEvent(msg.sender, _tradeID);
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     */
    function confirmTradeTermination(string memory tradeId) external override onlyCounterparty onlyWhenActive onlyWhenMarginAccountLocked
    {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(tradeId, "terminate")));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        tradeState = TradeState.Terminated;
        emit TerminationConfirmedEvent(msg.sender, tradeID);
    }

    function initiateMarginReqirementUpdate() external override {
        emit MarginRequirementUpdateRequestEvent();
    }

    function performMarginRequirementUpdate(address _address, uint256 amount)
    external
    override
    notIsSettlementOrMarginCheck
    {
        marginRequirements[_address].buffer = int256(amount);
        emit MarginRequirementUpdatedEvent();
    }

    function getTradeID() public view returns (string memory) {
        return tradeID;
    }

    function getTradeData() public view returns (string memory) {
        return tradeData;
    }




    function adjustSDCBalances(
        int256 adjustmentAmountParty1,
        int256 adjustmentAmountParty2
    ) internal {
        if (adjustmentAmountParty1 < 0)
            require(
                sdcBalances[party1] >= adjustmentAmountParty1,
                "SDC Balance Adjustment fails for Party1"
            );
        if (adjustmentAmountParty2 < 0)
            require(
                sdcBalances[party2] >= adjustmentAmountParty2,
                "SDC Balance Adjustment fails for Party2"
            );
        sdcBalances[party1] = sdcBalances[party1] + adjustmentAmountParty1;
        sdcBalances[party2] = sdcBalances[party2] + adjustmentAmountParty2;
    }

    function getTokenAddress() public view returns(address) {
        return address(liquidityToken);
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



}
