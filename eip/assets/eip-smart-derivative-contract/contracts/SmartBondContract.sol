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

    enum IssuanceType {
        FreeFloat,
        PublicAuction,
        Auction
    }

    modifier onlyIssuer() {
        require(msg.sender == issuerAddress , "You are not issuer"); _;
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

    mapping(uint256 => TransactionState) transactionStates;
    mapping(uint256 => TransactionSpec) transactionSpecs;
    mapping(address => uint256) bondHolderBalances;
    address[] bondHolderAddresses;
//    uint256 bondIssuerBalance;

    string private securityData;
    string private securityID;

//    mapping(uint256 => address) private pendingInceptions;

    uint        subscriptionIndex;
    uint        maturityTimeStamp;
    uint256     couponPercent;
    uint256[]   couponTimeStamps;
    uint256     nextCouponIndex;
    uint256     processIntervalSec;

    BondState bondState;


    constructor(
        address         _issuerAddress,
        string memory   _securityData,
        string memory   _securityID,
//        uint            _initialBalance,
        address         _settlementToken
    ) {
        issuerAddress = _issuerAddress;
        securityData = _securityData;
        securityID = _securityID;
//        bondHolderBalances[issuerAddress] = _initialBalance;
        settlementToken = ERC20Settlement(_settlementToken);
        bondState = BondState.NotIssued;
//        bondIssuerBalance = _initialBalance;
        nextCouponIndex = 0;
        subscriptionIndex = 0;
     }

    function initIssuancePhase() external onlyIssuer{
        bondState = BondState.IssuancePhase;

    }

    function terminateIssuancePhase() external onlyIssuer{
        bondState = BondState.Issued;
    }

    /*
     * generates a hash from tradeData and generates a map entry in openRequests
     * emits a TradeIncepted
     * can be called only when TradeState = Incepted
     */
    function inceptTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external  override {
        if ( bondState == BondState.IssuancePhase && !( _withParty == issuerAddress && _position == 1 ) ){
            revert("Issuance Phase - only Trade Inceptions with Issuer allowed");
        }
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        require(_position * _paymentAmount < 0, "Positive Position expects negative payment amount and vice versa");
        require(keccak256(abi.encode(securityData)) == keccak256(abi.encode(_tradeData)), "Trade Inception request does not meet contract's underlying security specification");
        uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData, _position, _paymentAmount)));

        address buyer = _position > 0 ? msg.sender : _withParty;  // buyer receives the bond + coupons and pays cash
        address seller = _position > 0 ? _withParty : msg.sender; // seller sells the bonds and receives cash
        uint absPosition = uint256(_position);
        uint256 transferAmount = uint256(_paymentAmount);

        if ( bondState == BondState.IssuancePhase && seller == issuerAddress) // issuance
            transactionSpecs[transactionHash] = TransactionSpec(TransactionType.Subscription,msg.sender,buyer,seller,absPosition,transferAmount,block.timestamp);
        else{ // Secondary Market
            require(bondHolderBalances[seller] >= absPosition, "Balance of selling party not sufficient");
            transactionSpecs[transactionHash] = TransactionSpec(TransactionType.SecondaryMarketTrade,msg.sender,buyer,seller,absPosition,transferAmount,block.timestamp);
        }

        transactionStates[transactionHash] = TransactionState.Incepted;
        emit TradeIncepted(msg.sender, Strings.toString(transactionHash), "");
    }


    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmed
     * can be called only when TradeState = Incepted
     */
    function confirmTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external override{
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        uint256 transactionHash = uint256(keccak256(abi.encode(_withParty,msg.sender,_tradeData,-_position, _paymentAmount)));
        require(transactionSpecs[transactionHash].inceptor == _withParty, "No pending inception available to be confirmed for this trade specification");
        TransactionSpec memory txSpec = transactionSpecs[transactionHash];
        /*Lock Bond to internal balance and trigger transfer of the paymentAmount*/
        bondHolderBalances[txSpec.seller]  -= txSpec.notional;
        bondHolderBalances[address(this)]  += txSpec.notional;
        transactionStates[transactionHash] = TransactionState.InTransfer;

        emit TradeConfirmed(msg.sender, Strings.toString(transactionHash));

        if ( _paymentAmount < 0 )
            settlementToken.checkedTransferFrom(txSpec.buyer,txSpec.seller,txSpec.paymentAmount,transactionHash); // trigger transfer upfrontPayment
        else
            settlementToken.checkedTransferFrom(txSpec.seller,txSpec.buyer,txSpec.paymentAmount,transactionHash); // trigger transfer upfrontPayment
    }

    function afterTransfer(uint256 transactionHash, bool success) external   {
        require(transactionStates[transactionHash] == TransactionState.InTransfer, "No existing Transfer phase for Transaction Hash");

        if (transactionSpecs[transactionHash].typ == TransactionType.CouponPayment){
            if (success == true){
                transactionStates[transactionHash] = TransactionState.Settled;
                nextCouponIndex++;
            }
            else{
                bondState = BondState.Defaulted;
                emit Defaulted();
                revert("Failure to pay");
            }
        }
        else if (transactionSpecs[transactionHash].typ == TransactionType.Redemption){
            if (success == true){
                transactionStates[transactionHash] = TransactionState.Settled;
                bondState = BondState.Matured;
            }
            else{
                bondState = BondState.Defaulted;
                emit Defaulted();
                revert("Failure to pay");
            }
        }
        else {
            if (success == true){ /* Transfer units to buyer */
                address addressBuyer = transactionSpecs[transactionHash].buyer;
                bondHolderBalances[address(this)]  -= transactionSpecs[transactionHash].notional;
                bondHolderBalances[addressBuyer]   += transactionSpecs[transactionHash].notional;
//                if (transactionSpecs[transactionHash].seller == issuerAddress)  // if Seller is Issuer than update issuer balance
//                    bondIssuerBalance += transactionSpecs[transactionHash].notional;
                transactionStates[transactionHash] = TransactionState.Settled;
                // TODO - transfer open Coupon Transactions
            }
            else{
                transactionStates[transactionHash] = TransactionState.Terminated;
                address adressSeller                = transactionSpecs[transactionHash].seller;
                bondHolderBalances[adressSeller]   += transactionSpecs[transactionHash].notional; /* transfer back the units */
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
        uint256 nextCouponTimeStamp = couponTimeStamps[nextCouponIndex];
        if (block.timestamp < nextCouponTimeStamp)
            revert("Not able to process coupon at current time stamp - Coupon time stamp at a future time point");
        else if (block.timestamp > nextCouponTimeStamp + processIntervalSec){
            bondState = BondState.Defaulted;
            emit Defaulted();
            revert("Failure to pay");
        }
        else{
            uint256 amountToPay = couponPercent * getBondHolderNotional() / 100;
            uint256[] memory couponAmounts = new uint256[](bondHolderAddresses.length);
            for (uint i = 0; i < bondHolderAddresses.length; i++){
                couponAmounts[i] = couponPercent * bondHolderBalances[bondHolderAddresses[i]] / 100;
            }
            uint256 transactionID = uint256(keccak256(abi.encodePacked(bondHolderAddresses,couponAmounts,nextCouponTimeStamp)));
            transactionSpecs[transactionID] = TransactionSpec(TransactionType.CouponPayment,issuerAddress,address(0),address(0),1,amountToPay,block.timestamp);
            transactionStates[transactionID] = TransactionState.InTransfer;
            settlementToken.checkedBatchTransfer(bondHolderAddresses,couponAmounts,transactionID);


            // TODO: Can we transfer coupons to bonds own address - refers to secondry market transactions which are in transfer?

            /*split settlementAmount and transfer to all bond holders*/
            /* Question: Keep in mind that balances might be locked at that point since a transfer might be running - who is getting the payment ? */
            /* settlementAmount is defined in cents per mimimumLotSize - e.g. 1% of mimimumLotSize 1000 EUR => 10EUr = 1000 ct
            So for each address pay = settlementAmount * lotBalances[address] */
            /* Handle Case: Failure to pay if issuerAdress has less Balance then settlementAmount * sum (lotBalances) */
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


    function getBondHolderNotional() internal returns (uint256) {
        uint256 grossNotional = 0;
        for (uint256 i=0;i<bondHolderAddresses.length;i++)
            grossNotional += bondHolderBalances[ bondHolderAddresses[i] ];
        return grossNotional;
    }

    function getSecurityID() public view returns (string memory) {
        return securityID;
    }


    function getSecurityData() public view returns (string memory) {
        return securityData;
    }

    /**
     * Absolute value of an integer
     */
    function abs(int x) internal pure returns (int256) {
        return x >= 0 ? x : -x;
    }


}
