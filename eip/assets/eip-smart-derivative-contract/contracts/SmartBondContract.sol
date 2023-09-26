// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./ISDC.sol";
import "./ERC20Settlement.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/introspection/IERC165.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract SmartBondContract is ISDC  {

    /**
     * @dev Emitted when margin balance was updated and sufficient funding is provided
     */
    event Defaulted();
    event Issued();
    event Matured();


    enum BondState {
        NotIssued,
        IssuancePhase,
        Issued,
        RedemptionPhase,
        Defaulted,
        Matured
    }


    /*
     * We have several Events in a security (bond) live cycle)
     */
    enum TransactionType {
        Subscription,
        Allocation,
        SecondaryMarketTrade,
        CouponPayment,
        Redemption
    }
    /*
     * Multiple Trade Transactions take place in a bond live cycle:
     */
    enum TransactionState {
        Incepted,
        InTransfer,
        Settled,
        Terminated
    }


    modifier onlyIssuer() {
        require(msg.sender == issuerAddress , "You are not issuer"); _;
    }

    modifier onlyWhenIssued() {
        require(bondState == BondState.Issued, "Issuance Phase not ended"); _;
    }

    struct TransactionSpec {
        TransactionType typ;
        address inceptor;
        address buyer;
        address seller;
        uint notional;
        uint paymentAmount;
        uint timestamp;
    }

    address issuerAddress;
    IERC20Settlement internal settlementToken;

    address[] bondHolderAddresses;
    mapping(uint256 => TransactionSpec)     transactionSpecs;
    mapping(uint256 => TransactionState)    transactionStates;
    mapping(address => uint256)             bondHolderBalances;

    string private securityData;
    string private securityID;

    uint256     couponPercent;
    uint        maturityTimeStamp;
    uint256[]   couponTimeStamps;
    uint256     nextCouponIndex;
    uint256     processIntervalSec;
    uint256     withheldCouponAmount;

    BondState bondState;

    constructor(
        address         _issuerAddress,
        string memory   _securityData,
        string memory   _securityID,
        address         _settlementToken
    ) {
        issuerAddress = _issuerAddress;
        securityData = _securityData;
        securityID = _securityID;
        settlementToken = ERC20Settlement(_settlementToken);
        bondState = BondState.NotIssued;
        nextCouponIndex = 0;
     }

    function initIssuancePhase() external onlyIssuer{
        bondState = BondState.IssuancePhase;

    }

    function terminateIssuancePhase() external onlyIssuer{
        uint nOpenSubscriptions = 0;//= nOpenSubscriptions();
        require(nOpenSubscriptions == 0, "Subscriptions pending.");
        bondState = BondState.Issued;
        emit Issued();
    }

    /*
     * generates a hash from tradeData and generates a map entry in openRequests
     * emits a TradeIncepted
     * can be called only when TradeState = Incepted
     */
    function inceptTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external  override {
        if ( bondState == BondState.IssuancePhase && !( _withParty == issuerAddress && _position == 1 ) ){
            revert("Issuance Phase - only Trade Inceptions with Issuer (i.e. Subscriptions) allowed");
        }
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        require(_position * _paymentAmount < 0, "Positive Position expects negative payment amount and vice versa");
        require(keccak256(abi.encode(securityData)) == keccak256(abi.encode(_tradeData)), "Trade Inception request does not meet contract's underlying security specification");

        address buyer = _position > 0 ? msg.sender : _withParty;  // buyer receives the bond + coupons and pays cash
        address seller = _position > 0 ? _withParty : msg.sender; // seller sells the bonds and receives cash
        uint absPosition = uint256(_position);
        uint256 transferAmount = uint256(_paymentAmount);

        if ( bondState == BondState.IssuancePhase){ // issuance
            uint256 subscriptionHash = uint256(keccak256(abi.encode(msg.sender)));
            transactionSpecs[subscriptionHash] = TransactionSpec(TransactionType.Subscription,msg.sender,msg.sender,issuerAddress,absPosition,transferAmount,block.timestamp);
        }
        else{ // Secondary Market
            uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData, _position, _paymentAmount)));
            require(bondHolderBalances[seller] >= absPosition, "Balance of selling party not sufficient");
            transactionSpecs[transactionHash] = TransactionSpec(TransactionType.SecondaryMarketTrade,msg.sender,buyer,seller,absPosition,transferAmount,block.timestamp);
            transactionStates[transactionHash] = TransactionState.Incepted;
            emit TradeIncepted(msg.sender, Strings.toString(transactionHash), "");
        }
    }


    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmed
     * can be called only when TradeState = Incepted
     */
    function confirmTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) public override{
        require(bondState == BondState.IssuancePhase && msg.sender == issuerAddress, "Issuance Phase - Confirm only allowed by Issuer");
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");

        TransactionSpec memory txSpec;
        uint256 transactionHash;
        if ( bondState == BondState.IssuancePhase){ // Issuance Phase
            uint256 subscriptionHash = uint256(keccak256(abi.encode(msg.sender)));             //TODO - check wether we have a subscription and the details
            if ( transactionSpecs[subscriptionHash].inceptor != address(0)){
                require(transactionSpecs[subscriptionHash].notional > uint(_paymentAmount), "Subscribed Notional should be larger than allocated notional" );
                uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData, _position, _paymentAmount)));
                transactionSpecs[transactionHash] = TransactionSpec(TransactionType.Allocation,issuerAddress,_withParty,issuerAddress,uint256(_position), uint256(_paymentAmount),block.timestamp);
                txSpec = transactionSpecs[transactionHash];
            }
            else
                revert("No subscription found");
        }
        else { // Secondary Market
            uint transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData,_position, _paymentAmount )));
            require(transactionSpecs[transactionHash].inceptor == _withParty, "No pending inception available to be confirmed for this trade specification");
            txSpec = transactionSpecs[transactionHash];
            bondHolderBalances[txSpec.seller]  -= txSpec.notional; // Decrement Notional from Seller Holder Balance
            if ( bondHolderBalances[txSpec.seller] == 0)
                deleteHolderAddress(txSpec.seller);
        }
        bondHolderBalances[address(this)]  += txSpec.notional;  // Transfer Bond to INTERNAL Balance and trigger transfer of the paymentAmount
        transactionStates[transactionHash] = TransactionState.InTransfer;
        emit TradeConfirmed(msg.sender, Strings.toString(transactionHash));

        if ( _paymentAmount < 0 )
            settlementToken.checkedTransferFrom(txSpec.buyer,txSpec.seller,txSpec.paymentAmount,transactionHash); // trigger transfer upfrontPayment
        else
            settlementToken.checkedTransferFrom(txSpec.seller,txSpec.buyer,txSpec.paymentAmount,transactionHash); // trigger transfer upfrontPayment
    }

    function afterTransfer(uint256 transactionHash, bool success) external   {
        require(transactionStates[transactionHash] == TransactionState.InTransfer, "No existing Transfer phase for Transaction Hash");
        // 1. Subscription -> Allocation Phase
        if (transactionSpecs[transactionHash].typ == TransactionType.Allocation ){
            if (success){
                transactionStates[transactionHash] = TransactionState.Settled;
                address addressBuyer = transactionSpecs[transactionHash].buyer;
                bondHolderBalances[addressBuyer]   += transactionSpecs[transactionHash].notional; // Allocate Notional
                addHolderAddress(addressBuyer); // add to bondHolderAddresses;
                uint256 subscriptionHash = uint256(keccak256(abi.encode(msg.sender)));
                delete transactionSpecs[subscriptionHash]; // Delete Subscription Hash
            }
            else {
                transactionStates[transactionHash] = TransactionState.Terminated;
            }
        }
        // 2. Coupon Payment
        else if (transactionSpecs[transactionHash].typ == TransactionType.CouponPayment){
            if (success == true){
                if (transactionSpecs[transactionHash].buyer != address(0)){
                    withheldCouponAmount -= transactionSpecs[transactionHash].paymentAmount;
                    transactionStates[transactionHash] = TransactionState.Settled;
                }
                else {
                    transactionStates[transactionHash] = TransactionState.Settled;
                    nextCouponIndex++;
                }
            }
            else{
                transactionStates[transactionHash] = TransactionState.Terminated;
                bondState = BondState.Defaulted;
                emit Defaulted();
                revert("Failure to pay");
            }
        }
        // 3. Redemption Phase
        else if (transactionSpecs[transactionHash].typ == TransactionType.Redemption){
            if (success == true){
                transactionStates[transactionHash] = TransactionState.Settled;
                bondState = BondState.Matured;
                emit Matured();
            }
            else{
                transactionStates[transactionHash] = TransactionState.Terminated;
                bondState = BondState.Defaulted;
                emit Defaulted();
                revert("Failure to pay");
            }
        }
        // 4. Secondary Market Transaction
        else {
            if (success == true){ /* Transfer units to buyer */
                address addressBuyer = transactionSpecs[transactionHash].buyer;
                bondHolderBalances[address(this)]  -= transactionSpecs[transactionHash].notional;
                bondHolderBalances[addressBuyer]   += transactionSpecs[transactionHash].notional;
                addHolderAddress(addressBuyer); // add to bondHolderAddresses;
                transactionStates[transactionHash] = TransactionState.Settled;
                if ( withheldCouponAmount > 0 ) { // Transfer of Coupon which is open
                    uint256 couponAmountBuyer =  couponPercent * transactionSpecs[transactionHash].notional / 100;
                    uint256 transactionHash = uint256(keccak256(abi.encodePacked(addressBuyer,couponAmountBuyer)));
                    transactionSpecs[transactionHash] = TransactionSpec(TransactionType.CouponPayment,issuerAddress,addressBuyer,address(0),0,couponAmountBuyer,block.timestamp);
                    transactionStates[transactionHash] = TransactionState.InTransfer;
                    settlementToken.checkedTransfer(addressBuyer,couponAmountBuyer,transactionHash);
                }

            }
            else{
                transactionStates[transactionHash] = TransactionState.Terminated;
                address addressSeller                = transactionSpecs[transactionHash].seller;
                bondHolderBalances[addressSeller]   += transactionSpecs[transactionHash].notional; /* transfer back the units */
                bondHolderBalances[address(this)]  -= transactionSpecs[transactionHash].notional;
                delete transactionSpecs[transactionHash];
                emit TradeTerminated("Settlement Transfer failed - Trade ist terminated");
            }
        }
    }

    /*
     * Settlement relates to coupon payments only allowed by the issuer and only allowed in a certain time window
     */
    function initiateSettlement() external onlyIssuer  {
        require(bondState == BondState.Issued, "Issuer can trigger settlement only for issued bonds");
        uint256 nextCouponTimeStamp = couponTimeStamps[nextCouponIndex];
        if (block.timestamp < nextCouponTimeStamp)
            revert("Not able to process coupon at current time stamp - Coupon time stamp at a future time point");
        else if (block.timestamp > nextCouponTimeStamp + processIntervalSec){
            bondState = BondState.Defaulted;
            emit Defaulted();
            revert("Failure to pay");
        }
        else{
            uint256 amountToPay = couponPercent * getGrossNotional() / 100;
            uint256[] memory couponAmounts = new uint256[](bondHolderAddresses.length);
            for (uint i = 0; i < bondHolderAddresses.length; i++){
                uint256 couponAmount =  couponPercent * bondHolderBalances[bondHolderAddresses[i]] / 100;
                couponAmounts[i] = couponAmount;
            }

            if ( bondHolderBalances[address(this)] > 0 ){ // In case we have secondary market transactions inTransfer (so bond balance is currently locked)
                withheldCouponAmount = couponPercent * bondHolderBalances[address(this)] / 100;
            }
            uint256 transactionHash = uint256(keccak256(abi.encodePacked(bondHolderAddresses,couponAmounts,nextCouponTimeStamp)));
            transactionSpecs[transactionHash] = TransactionSpec(TransactionType.CouponPayment,issuerAddress,address(0),address(0),0,amountToPay,block.timestamp);
            transactionStates[transactionHash] = TransactionState.InTransfer;
            settlementToken.checkedBatchTransfer(bondHolderAddresses,couponAmounts,transactionHash);


        }
    }

    /*
     * Performs a Coupon Payment
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     * can be called only when ProcessState = ValuationAndSettlement
     */

    function performSettlement(int256 settlementAmount, string memory _settlementData) onlyIssuer public  {
        revert("Not implemented, Settlement is processed on chain");
    }

    /*
     * Can be called by a party for mutual termination
     * Hash is generated an entry is put into pendingRequests
     * TerminationRequest is emitted
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function requestTradeTermination(string memory _tradeId, int256 _terminationPayment) external  onlyIssuer {
        /*require(keccak256(abi.encodePacked(tradeId)) == keccak256(abi.encodePacked(_tradeId)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeId, "terminate", terminationPayment)));
        pendingRequests[hash] = msg.sender;
        terminationPayment = _terminationPayment;
        emit TradeTerminationRequest(msg.sender, _tradeId);*/
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function confirmTradeTermination(string memory _tradeId, int256 _terminationPayment) external  onlyIssuer {
        /*address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(_tradeId, "terminate", terminationPayment)));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        mutuallyTerminated = true;
        emit TradeTerminationConfirmed(msg.sender, _tradeId);
        _emitSettlementRequest();*/
    }

    function deleteHolderAddress(address _address) internal { // Delete and does not preserve the array order
        for ( uint256 i = 0; i< bondHolderAddresses.length;i++){
            if ( bondHolderAddresses[i] == _address){
                bondHolderAddresses[i] = bondHolderAddresses[bondHolderAddresses.length-1];
                bondHolderAddresses.pop();
                return;
            }
        }

    }

    function addHolderAddress(address _address) internal {
        for ( uint256 i = 0; i< bondHolderAddresses.length;i++){
            if ( bondHolderAddresses[i] == _address){
                return;
            }
        }
        bondHolderAddresses.push(_address);
        return;
    }

    function getGrossNotional() internal returns (uint256) {
        uint256 grossNotional = 0;
        for (uint256 i=0;i< bondHolderAddresses.length;i++)
            grossNotional += bondHolderBalances[ bondHolderAddresses[i] ];
        return grossNotional;
    }

    function getSecurityID() public view returns (string memory) {
        return securityID;
    }


    function getSecurityData() public view returns (string memory) {
        return securityData;
    }

/*    function nOpenSubscriptions() internal view returns (uint256){
        uint openSubscriptions =0;
        for (uint i=0;i<bondHolderAddresses.length;i++){
            uint256 transactionHash = uint256(keccak256(abi.encode(bondHolderAddresses[i])));
            if (transactionSpecs[transactionHash] != 0)
                openSubscriptions++;
        }
        return openSubscriptions;
    }*/

    /**
     * Absolute value of an integer
     */
    function abs(int x) internal pure returns (int256) {
        return x >= 0 ? x : -x;
    }


}
