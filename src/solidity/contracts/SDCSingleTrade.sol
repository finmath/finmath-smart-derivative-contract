// SPDX-License-Identifier: MIT
pragma solidity >=0.7.0 <0.9.0;

import "./ISDC.sol";

contract SDCSingleTrade is ISDC{

    enum  TradeState {
        Initiated,
        Active,
        Terminated
    }

    enum TradeRequestType{
        None,   /* needed since we need to check a null entry in openRequests  */
        Init,
        Update,
        Terminate
    }


    enum  ProcessState {
        Idle,
        Funding,
        MarginAccountCheck,
        MarginAccountLocked,
        ValuationAndSettlement,
        InTransfer,
        Settled
    }


    TradeState public tradeState;
    ProcessState public processState;

    struct MarginRequirement{
        uint buffer;
        uint terminationFee;
    }

    struct RequestSpec{
        TradeRequestType requestType;
        address requestAddress;
    }

    mapping(uint => RequestSpec) openRequests;

    address public party1;
    address public party2;

    uint private tradeID;
    string private tradeData;
    address public valuationViewParty;
    mapping(address => MarginRequirement) marginRequirements;

    constructor(address _party1, address _party2) {
        party1 = _party1;
        party2 = _party2;
        tradeState = TradeState.Terminated;
        processState = ProcessState.Idle;
    }

    // modifiers according to state machine
    modifier onlyWhenTerminated() {
        require(tradeState == TradeState.Terminated, "Trade state is not 'Terminated'.");
        _;
    }

    modifier onlyWhenInitiatedOrSettled() {
        require(tradeState == TradeState.Initiated || (tradeState == TradeState.Active && processState == ProcessState.Settled), "Trade state is not 'Confirmed' or 'Settled'.");
        _;
    }

    modifier onlyWhenFunding() {
        require(processState == ProcessState.Funding, "Process State is not funding");
        _;
    }

    modifier onlyWhenMarginAccountCheck() {
        require(processState == ProcessState.MarginAccountCheck, "Process State is not MarginAccountCheck");
        _;
    }

    modifier onlyWhenActive() {
        require(tradeState == TradeState.Active, "Trade State is not Active");
        _;
    }

    modifier onlyWhenMarginAccountLocked() {
        require(processState == ProcessState.MarginAccountLocked, "Process State is not MarginAccountLocked");
        _;
    }

    modifier onlyWhenValuationAndSettlement() {
        require(processState == ProcessState.ValuationAndSettlement, "Process State is not ValuationAndSettlement");
        _;
    }

    modifier onlyWhenInTransfer() {
        require(processState == ProcessState.InTransfer, "Process State is not InTransfer");
        _;
    }

    modifier isCounterparty() {
        require( msg.sender == party1 || msg.sender == party2, "You are not a counterparty.");
        _;
    }


    function getTradeID() public view returns (uint) {
        return tradeID;
    }

    function getTradeData() public view returns (string memory) {
        return tradeData;
    }

    function getTradeStatus() public view returns (TradeState) {
        return tradeState;
    }


    function requestTradeInitiation(string memory tradeData, address addressValuationView, uint256 marginBuffer, uint256 terminationFee) external override onlyWhenTerminated {
        require( (addressValuationView == party1 || addressValuationView == party2) , "ValuationViewAddress needs to be either Party1 or party2");
        tradeID = uint(keccak256(abi.encode(tradeData, addressValuationView,marginBuffer,terminationFee, msg.sender)));
        openRequests[tradeID] = RequestSpec(TradeRequestType.Init,msg.sender);
        emit TradeInceptedEvent(msg.sender, tradeID, tradeData);
    }

    function confirmTradeInitiation(string memory _tradeData, address addressValuationView, uint256 marginBuffer, uint256 terminationFee) external override  {
        address requestingParty = msg.sender == party1 ? party2 : party1;
        uint tradeIDConf = tradeID = uint(keccak256(abi.encode(_tradeData, addressValuationView,marginBuffer,terminationFee, requestingParty)));
        require(openRequests[tradeIDConf].requestType == TradeRequestType.Init , "Confirmation fails due to missing init or inconsistent trade data");
        require(openRequests[tradeIDConf].requestAddress != msg.sender , "Confirmation cannot be executed by requesting party");
        /* After hashes are checked, member variables are set*/
        tradeData = _tradeData;
        valuationViewParty = addressValuationView;
        marginRequirements[party1] = MarginRequirement(marginBuffer,terminationFee);
        marginRequirements[party2] = MarginRequirement(marginBuffer,terminationFee);
        delete openRequests[tradeID];
        tradeState = TradeState.Initiated;
        emit TradeConfirmedEvent(msg.sender, tradeID);
    }

    function marginAccountUnlockRequest(uint256 tradeId) external override onlyWhenInitiatedOrSettled {
        processState = ProcessState.Funding;
        emit MarginAccountUnlockRequestEvent(msg.sender);
    }

    function initiateMarginLock() external override onlyWhenFunding {
        processState = ProcessState.MarginAccountCheck;
        emit MarginAccountLockRequestEvent(msg.sender);
    }

    function performMarginLockAt(address _party1, uint256 balanceParty1, address _party2, uint256 balanceParty2) external override onlyWhenMarginAccountCheck {
        /* Check Party 1*/
        if (balanceParty1 < marginRequirements[_party1].buffer + marginRequirements[_party1].terminationFee) {
            tradeState = TradeState.Terminated;
            processState = ProcessState.InTransfer;
            emit TerminationDueToMarginInsufficientEvent(tradeID,_party1);
            //@Todo: Trigger TRAP Contract
            return;
        }
        /* Check Party 2*/
        else if (balanceParty2 < marginRequirements[_party2].buffer + marginRequirements[_party2].terminationFee) {
            tradeState = TradeState.Terminated;
            processState = ProcessState.InTransfer;
            emit TerminationDueToMarginInsufficientEvent(tradeID,_party2);
            // @Todo: TRAP: For Termination Call CreatePayment(PayerParty,ReceiverParty,Amount, Message)
            return;
        }
        else{
            processState = ProcessState.MarginAccountLocked;
            emit MarginAccountLockedEvent(true);
            emit TradeActivatedEvent(tradeID);
        }
    }

    function initiateSettlement() external onlyWhenMarginAccountLocked onlyWhenActive override{
        processState = ProcessState.ValuationAndSettlement;
        emit ValuationRequestEvent(tradeData, valuationViewParty);
    }

    function performSettlement(int256 settlementAmount) external onlyWhenValuationAndSettlement onlyWhenActive override{
        processState = ProcessState.InTransfer;
        uint256 transferAmount = uint256(settlementAmount);
        address payingParty = settlementAmount < 0 ? valuationViewParty : (valuationViewParty == party1 ? party2 : party1 );
        address receivingParty = payingParty == party1 ? party2 : party1;

        if (transferAmount > marginRequirements[payingParty].buffer){
            tradeState = TradeState.Terminated;
            uint terminationAmount = marginRequirements[payingParty].buffer + marginRequirements[payingParty].terminationFee;
            // @Todo: TRAP: For Termination Call CreatePayment(PayerParty,ReceiverParty,Amount, Message)
            emit TerminationDueToMarginExceedanceEvent(tradeID, payingParty);
        }
        // @Todo: TRAP: For Reqular Settlement - Call CreatePayment(PayerParty,ReceiverParty,Amount, Message)

    }

    function setTransferCompleted() external onlyWhenInTransfer override{
        if ( tradeState == TradeState.Terminated){
            processState = ProcessState.Idle;
            tradeState = TradeState.Terminated;
            emit TradeTerminatedEvent(tradeID);
        }
        else{
            processState = ProcessState.Settled;
            emit SettlementCompletedEvent(tradeID);
        }
    }


    function requestTradeTermination(uint256 tradeId) isCounterparty onlyWhenActive onlyWhenMarginAccountLocked external  override {
        uint hash = uint(keccak256(abi.encode(tradeId,msg.sender)));
        openRequests[hash] = RequestSpec(TradeRequestType.Terminate,msg.sender);
        emit TerminationRequestEvent(msg.sender,tradeID);

    }

    function confirmTradeTermination(uint256 tradeId) isCounterparty onlyWhenActive onlyWhenMarginAccountLocked external  override {
        address requestingParty = msg.sender == party1 ? party2 : party1;
        uint hashConfirm = uint(keccak256(abi.encode(tradeId,requestingParty)));
        require(openRequests[hashConfirm].requestType==TradeRequestType.Terminate, "No Termination request available");
        require(openRequests[hashConfirm].requestAddress != msg.sender, "Confirmation of termination cannot be performed by requesting party - ");
        delete openRequests[hashConfirm];
        tradeState = TradeState.Terminated;
        emit TerminationConfirmedEvent(msg.sender,tradeID);
    }

    function requestMarginRequirementUpdate(address contractAddress, uint256 newMarginBuffer, uint256 newTerminationFee)  isCounterparty external  override{
        uint hash = uint(keccak256(abi.encode(contractAddress,newMarginBuffer,newTerminationFee,msg.sender)));
        openRequests[hash] = RequestSpec(TradeRequestType.Update,msg.sender);
        emit MarginRequirementUpdateRequestEvent(contractAddress,newMarginBuffer,newTerminationFee);
    }

    function confirmMarginRequirementUpdate (address contractAddress, uint256 newMarginBuffer, uint256 newTerminationFee)  isCounterparty external  override{
        address requestingParty = msg.sender == party1 ? party2 : party1;
        uint hashConfirm = uint(keccak256(abi.encode(contractAddress,newMarginBuffer,newTerminationFee,requestingParty)));
        require(openRequests[hashConfirm].requestType == TradeRequestType.Update, "Confirm margin update: Hashes to not match");
        require(openRequests[hashConfirm].requestAddress != msg.sender, "Confirmation of margin update cannot be performed by requesting party");
        delete openRequests[hashConfirm];
        emit MarginRequirementUpdatedEvent(contractAddress, newMarginBuffer, newTerminationFee);
    }




}