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

    /*
     * Modifiers serve as guards whether at a specific process state a specific function can be called
     */
    modifier onlyWhenInactive() {
        require(
            tradeState == TradeState.Inactive,
            "Trade state is not 'Inactive'."
        );
        _;
    }
    modifier onlyWhenIdle() {
        require(
            processState == ProcessState.Idle,
            "Process state is not 'Idle'."
        );
        _;
    }

    modifier onlyWhenIncepted() {
        require(
            tradeState == TradeState.Incepted,
            "Trade state is not 'Incepted'."
        );
        _;
    }

    modifier onlyWhenConfirmedOrSettled() {
        require(
            (tradeState == TradeState.Confirmed) ||
                (tradeState == TradeState.Active &&
                    processState == ProcessState.Settled),
            "Trade state is not 'Initiated' or 'Settled'."
        );
        _;
    }

    // #155
    modifier onlyWhenInceptedOrConfirmedOrSettled() {
        require(
            (tradeState == TradeState.Incepted) || (tradeState == TradeState.Confirmed) ||
                (tradeState == TradeState.Active &&
                    processState == ProcessState.Settled),
            "Trade state is not 'Initiated' or 'Settled'."
        );
        _;
    }

    modifier onlyWhenFunding() {
        require(
            processState == ProcessState.Funding,
            "Process State is not funding"
        );
        _;
    }

    modifier onlyWhenMarginAccountCheck() {
        require(
            processState == ProcessState.MarginAccountCheck,
            "Process State is not MarginAccountCheck"
        );
        _;
    }

    modifier onlyWhenActive() {
        require(tradeState == TradeState.Active, "Trade State is not Active");
        _;
    }

    modifier onlyWhenMarginAccountLocked() {
        require(
            processState == ProcessState.MarginAccountLocked,
            "Process State is not MarginAccountLocked"
        );
        _;
    }

    modifier onlyWhenValuationAndSettlement() {
        require(
            processState == ProcessState.ValuationAndSettlement,
            "Process State is not ValuationAndSettlement"
        );
        _;
    }

    modifier onlyCounterparty() {
        require(
            msg.sender == party1 || msg.sender == party2,
            "You are not a counterparty."
        );
        _;
    }

    modifier notIsSettlementOrMarginCheck() {
        require(
            !(processState == ProcessState.ValuationAndSettlement &&
                processState == ProcessState.MarginAccountCheck),
            "Process State is  Settlement or Margin Account Check"
        );
        _;
    }

    event TerminationDueToInconsistentMarketDataHistoryEvent(string tradeID);

    TradeState private tradeState;
    ProcessState private processState;

    address public party1;
    address public party2;

    IERC20 private liquidityToken;

    string private tradeID;
    string private tradeData;

    mapping(address => MarginRequirement) private marginRequirements; // Storage of M and P per counterparty address
    mapping(uint256 => address) private pendingRequests; // Stores open request hashes for several requests: initiation, update and termination

    address private immutable valuationViewPartyAddress; // immutable variable how to derive payment request from settlement amount, set to party 1

    mapping(address => int256) private sdcBalances; // internal book-keeping: needed to track what part of the gross token balance is held for each party

    // #107
    string[] private settlementTimestamps;
    int256[] private settlementAmounts;

    // #139
    string[] private marketData;
    uint256 private numberOfSettlements;
    bool private marketDataUpdateEnabled = false;

    constructor(
        address _party1,
        address _party2,
        address tokenAddress,
        uint256 initialMarginRequirement,
        uint256 initalTerminationFee
    ) {
        party1 = _party1;
        party2 = _party2;
        liquidityToken = IERC20(tokenAddress);
        valuationViewPartyAddress = party1; // hard coded, @Todo: Check with FPML Parser, valuationView should serve as an argument to the oracle
        tradeState = TradeState.Inactive;
        processState = ProcessState.Idle;
        marginRequirements[party1] = MarginRequirement(
            int256(initialMarginRequirement),
            int256(initalTerminationFee)
        );
        marginRequirements[party2] = MarginRequirement(
            int256(initialMarginRequirement),
            int256(initalTerminationFee)
        );
        sdcBalances[party1] = 0;
        sdcBalances[party2] = 0;
    }

    /*
     * generates a hash from tradeData and generates a map entry in openRequests
     * emits a TradeInceptedEvent
     */

    function inceptTrade(string memory _tradeData)
        external
        override
        onlyCounterparty
        onlyWhenInactive
        onlyWhenIdle
    {
        processState = ProcessState.Initiation;
        uint256 hash = uint256(keccak256(abi.encode(_tradeData)));
        tradeState = TradeState.Incepted; // Set TradeState to Incepted
        pendingRequests[hash] = msg.sender;
        //tradeID = hash;                         // Set Trade ID to enable querying already in inception state (changed due to problem issue related with call from registry)
        tradeData = _tradeData; // Set Trade Data to enable querying already in inception state
        emit TradeInceptedEvent(msg.sender, tradeID, _tradeData);
        // #139 #155
        marketDataUpdateEnabled = true;
    }

    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmedEvent
     */
    function confirmTrade(string memory _tradeData)
        external
        override
        onlyCounterparty
        onlyWhenIncepted
    {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 tradeIDConf = uint256(keccak256(abi.encode(_tradeData)));
        require(
            pendingRequests[tradeIDConf] == pendingRequestParty,
            "Confirmation fails due to inconsistent trade data or wrong party address"
        );
        delete pendingRequests[tradeIDConf]; // Delete Pending Request
        tradeState = TradeState.Confirmed;
        emit TradeConfirmedEvent(msg.sender, tradeID);
        _processMarginUnlock(); // unlock Margin Accounts and set Process State to Funding
    }

    /*
     * Unlocks a Margin Amount only when Trade is Initiated or if Trade is Active and Process State is Settled
     * Puts Process State to Funding
     */
    function marginAccountUnlockRequest()
        external
        override
        onlyWhenConfirmedOrSettled
    {
        // emit MarginAccountUnlockRequestEvent(msg.sender);
        _processMarginUnlock();
    }

    function _processMarginUnlock() internal {
        if (tradeState == TradeState.Confirmed) {
            processState = ProcessState.Funding;
            return;
        }
        if (
            tradeState == TradeState.Active &&
            processState == ProcessState.Settled
        ) {
            // If we have a preceeding settlement then release margin buffer amounts (termination fees remain locked)
            if (
                liquidityToken.balanceOf(address(this)) !=
                uint256(sdcBalances[party1] + sdcBalances[party2])
            )
                //Consistency check of SDC Contract Balances in case of reqular settlement
                tradeState = TradeState.Terminated; // if balance check fails: Terminate;
            else {
                liquidityToken.approve(
                    party1,
                    uint256(
                        sdcBalances[party1] -
                            marginRequirements[party1].terminationFee
                    )
                ); // unlock all except termination fee
                liquidityToken.approve(
                    party2,
                    uint256(
                        sdcBalances[party2] -
                            marginRequirements[party2].terminationFee
                    )
                ); // unlock all except termination fee
                processState = ProcessState.Funding; // Funding Period is started
            }
        }
        if (tradeState == TradeState.Terminated) {
            // Process Termination - Release all sdcBalances
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
    function performMarginAccountCheck(
        uint256 balanceParty1,
        uint256 balanceParty2
    ) external override onlyWhenMarginAccountCheck {
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
            adjustSDCBalances(marginRequirements[party1].terminationFee,marginRequirements[party2].terminationFee); // Update internal balances
        }

        /* Calculate gap amount for each party, i.e. residual between buffer and termination fee and actual balance*/
        uint gapAmountParty1 = marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1] > 0 ? uint(marginRequirements[party1].buffer + marginRequirements[party1].terminationFee - sdcBalances[party1]) : 0;
        uint gapAmountParty2 = marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2] > 0 ? uint(marginRequirements[party2].buffer + marginRequirements[party2].terminationFee - sdcBalances[party2]) : 0;

        /* Good case: Balances are sufficient and token has enough approval */
        if ( (balanceParty1 >= gapAmountParty1 && liquidityToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && liquidityToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            liquidityToken.approve(party1,0);    // Remove Approvals
            liquidityToken.approve(party2,0);
            liquidityToken.transferFrom(party1,address(this),gapAmountParty1);  // Transfer of GapAmount to sdc contract
            liquidityToken.transferFrom(party2,address(this),gapAmountParty2);  // Transfer of GapAmount to sdc contract
            adjustSDCBalances(int(gapAmountParty1),int(gapAmountParty2));  // Update internal balances
            tradeState = TradeState.Active;
            processState = ProcessState.MarginAccountLocked;
            emit MarginAccountLockedEvent();
            emit TradeActivatedEvent(tradeID);
        }
        /* Party 1 - Bad case: Balances are insufficient or token has not enough approval */
        else if ( (balanceParty1 < gapAmountParty1 || liquidityToken.allowance(party1,address(this)) < gapAmountParty1) &&
            (balanceParty2 >= gapAmountParty2 && liquidityToken.allowance(party2,address(this)) >= gapAmountParty2) ) {
            tradeState = TradeState.Terminated;
            liquidityToken.transfer(party2,uint(marginRequirements[party1].terminationFee));   // Transfer termination fee to party2
            adjustSDCBalances(-marginRequirements[party1].terminationFee,marginRequirements[party1].terminationFee); // Update internal balances
            _processMarginUnlock(); // Release all buffers
            emit TerminationDueToMarginInsufficientEvent(party1, balanceParty1);
        }
        /* Party 2 - Bad case: Balances are insufficient or token has not enough approval */
        else if ( (balanceParty1 >= gapAmountParty1 && liquidityToken.allowance(party1,address(this)) >= gapAmountParty1) &&
            (balanceParty2 < gapAmountParty2 || liquidityToken.allowance(party2,address(this)) < gapAmountParty2) ) {
            tradeState = TradeState.Terminated;
            liquidityToken.transfer(party1,uint(marginRequirements[party2].terminationFee));      // Transfer termination fee to party1
            adjustSDCBalances(marginRequirements[party2].terminationFee,-marginRequirements[party2].terminationFee); // Update internal balances
            _processMarginUnlock(); // Release all buffers
            emit TerminationDueToMarginInsufficientEvent(party2, balanceParty2);
        }
        /* Both parties fail: Cross Transfer of Termination Fee */
        else {
            // if ( (balanceParty1 < gapAmountParty1 || liquidityToken.allowance(party1,address(this)) < gapAmountParty1) &&  (balanceParty2 < gapAmountParty2 || liquidityToken.allowance(party2,address(this)) < gapAmountParty2) ) { tradeState = TradeState.Terminated;
            adjustSDCBalances(marginRequirements[party2].terminationFee-marginRequirements[party1].terminationFee,marginRequirements[party1].terminationFee-marginRequirements[party2].terminationFee); // Update internal balances: Cross Booking of termination fee
            _processMarginUnlock(); // Release all buffers
            emit TerminationDueToMarginInsufficientEvent(address(0), 0);
        }
    }

    /*
     * Settlement can be initiated when margin accounts are locked, a valuation request event is emitted containing tradeData and valuationViewParty
     * Changes Process State to Valuation&Settlement
     */
    function initiateSettlement()
        external
        override
        onlyWhenMarginAccountLocked
        onlyWhenActive
    {
        processState = ProcessState.ValuationAndSettlement;
        emit ValuationRequestEvent(tradeData, valuationViewPartyAddress);
    }

    /*
     * Performs a settelement only when processState is ValuationAndSettlement
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     */
    function performSettlement(int256 settlementAmount)
        external
        override
        onlyWhenValuationAndSettlement
        onlyWhenActive
    {
        int256 transferAmount = settlementAmount < 0
            ? -settlementAmount
            : settlementAmount; // casting
        address payingParty = settlementAmount < 0
            ? valuationViewPartyAddress
            : (valuationViewPartyAddress == party1 ? party2 : party1);
        address receivingParty = payingParty == party1 ? party2 : party1;

        // #139
        if (marketData.length < numberOfSettlements + 1) {
            // Checks if market data were provided for last settlement... +1 because of initial marketData when confirmed...
            tradeState = TradeState.Terminated;
            // Same behaviour as in mutual terminaion scenario => No penalty fee payments
            emit TerminationDueToInconsistentMarketDataHistoryEvent(tradeID);
            return;
        }

        if (transferAmount > marginRequirements[payingParty].buffer) {
            // Termination Event, buffer not sufficient
            tradeState = TradeState.Terminated;
            transferAmount =
                marginRequirements[payingParty].buffer +
                marginRequirements[payingParty].terminationFee; // Override with Buffer and Termination Fee: Max Transfer
            liquidityToken.transfer(receivingParty, uint256(transferAmount)); // SDC performs transfer to receiving party
            emit TerminationDueToMarginExceedanceEvent(
                payingParty,
                uint256(transferAmount)
            );
        } else {
            // Regular Settlement
            liquidityToken.transfer(receivingParty, uint256(transferAmount)); // SDC Contract performs transfer to receiving party
            emit SettlementCompletedEvent();
        }
         payingParty == party1 ? adjustSDCBalances(-transferAmount,0) : adjustSDCBalances(0,-transferAmount); // Update Internal Balances
        processState = ProcessState.Settled;  // Set Process State to Settled

        // ##139
        ++numberOfSettlements;
        marketDataUpdateEnabled = true;
    }

    /*
     * Can be called by a party for mutual termination
     * Hash is generated an entry is put into pendingRequests
     * TerminationRequest is emitted
     */
    function requestTradeTermination(string memory _tradeID)
        external
        override
        onlyCounterparty
        onlyWhenActive
        onlyWhenMarginAccountLocked
    {
        require(
            keccak256(abi.encodePacked(tradeID)) ==
                keccak256(abi.encodePacked(_tradeID)),
            "Trade ID mismatch"
        );
        uint256 hash = uint256(keccak256(abi.encode(_tradeID, "terminate")));
        pendingRequests[hash] = msg.sender;
        emit TerminationRequestEvent(msg.sender, _tradeID);
    }

    /*
     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     */
    function confirmTradeTermination(string memory tradeId)
        external
        override
        onlyCounterparty
        onlyWhenActive
        onlyWhenMarginAccountLocked
    {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(
            keccak256(abi.encode(tradeId, "terminate"))
        );
        require(
            pendingRequests[hashConfirm] == pendingRequestParty,
            "Confirmation of termination failed due to wrong party or missing request"
        );
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

    function setTradeID(string memory _tradeID) public {
        tradeID = _tradeID;
    }

    function getTradeData() public view returns (string memory) {
        return tradeData;
    }

    function storeSettlementInformation(
        string memory _timestamp,
        int256 _amount,
        string memory _marketData
    ) public onlyCounterparty onlyWhenInceptedOrConfirmedOrSettled {
        require(
            marketDataUpdateEnabled,
            "Market data cannot be set for the current settlement period anymore."
        );

        if (numberOfSettlements == 0 && _amount != 0) {
            revert(
                "Settlement information to be called with zero amount initially."
            );
        }

        settlementTimestamps.push(_timestamp);
        settlementAmounts.push(_amount);
        marketData.push(_marketData);

        marketDataUpdateEnabled = false;
    }

    function getSettlementHistory()
        public
        view
        onlyCounterparty
        returns (
            string[] memory,
            int256[] memory,
            string[] memory
        )
    {
        return (settlementTimestamps, settlementAmounts, marketData);
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

    function getMarginBufferAmount(address cpAddress)
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

    function getSdcBalancesByAddress(address _address)
        public
        view
        /* onlyCounterparty */
        returns (int256)
    {
        return sdcBalances[_address];
    }

    /**END OF FUNCTIONS WHICH ARE ONLY USED FOR TESTING PURPOSES */
}
