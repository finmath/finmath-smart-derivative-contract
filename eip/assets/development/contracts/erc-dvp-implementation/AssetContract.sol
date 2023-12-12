// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0;

import "./interface/IDeliveryWithKey.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract DeliveryContract is IDeliveryWithKey {

    //   DEFINED in INTERFACE
    //   event AssetTransferIncepted(address initiator, uint id);
    //   event AssetTransferConfirmed(address confirmer, uint id);
    //   event AssetClaimed(uint id, string key);
    //   event AssetReclaimed(uint id, string key);

    event TradeIncepted(address initiator, string tradeId, string tradeData);
    event TradeConfirmed(address confirmer, string tradeId);

    enum TransactionState {
        None,
        TradeIncepted,
        TradeConfirmed,
        TransferIncepted,
        TransferConfirmed,
        TradeSettled,
        TradeUnwind
    }


    struct TransactionSpec {
        address inceptor;
        address buyer;
        address seller;
        uint notional;
        uint paymentAmount;
        uint timestamp;
    }

    struct TransactionKeys {
        uint keyBuyer;
        uint keySelle;
    }

    mapping(address => uint256)             bondHolderBalances;
    mapping(uint256 => TransactionState)    transactionStates;
    mapping(uint256 => TransactionSpec)     transactionSpecs;
    mapping(uint256 => TransactionKeys)     transactionKeys;
    address sellerAddress;
    address buyerAddress;

    constructor(address _sellerAddress, address _buyerAddress){
        sellerAddress = _sellerAddress;
        buyerAddress = _buyerAddress;
    }

    modifier onlySeller() {
        require(msg.sender == sellerAddress, "You are not the seller."); _;
    }
    modifier onlyBuyer() {
        require(msg.sender == sellerAddress, "You are not the buyer."); _;
    }

    function inceptTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external{
        address buyer = _position > 0 ? msg.sender : _withParty;  // buyer receives the bond + coupons and pays cash
        address seller = _position > 0 ? _withParty : msg.sender; // seller sells the bonds and receives cash
        uint absPosition = uint256(_position);
        uint256 transferAmount = uint256(_paymentAmount);
        uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData, _position, _paymentAmount)));
        transactionSpecs[transactionHash] = TransactionSpec(msg.sender,buyer,seller,absPosition,transferAmount,block.timestamp);
        transactionStates[transactionHash] = TransactionState.TradeIncepted;
        emit TradeIncepted(msg.sender, Strings.toString(transactionHash), "");
    }

    function confirmTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external{
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData,_position, _paymentAmount )));
        require(transactionSpecs[transactionHash].inceptor == _withParty, "No pending inception available to be confirmed for this trade specification");
        TransactionSpec memory txSpec = transactionSpecs[transactionHash];
        bondHolderBalances[txSpec.seller]  -= txSpec.notional; // Decrement Notional from Seller Holder Balance
        bondHolderBalances[address(this)]  += txSpec.notional;  // Transfer Bond to INTERNAL Balance and trigger transfer of the paymentAmount
        transactionStates[transactionHash] = TransactionState.TradeConfirmed;
        emit TradeConfirmed(msg.sender, Strings.toString(transactionHash));
    }



    function inceptTransfer(uint id, int amount, address from, string memory keyEncryptedSeller) external override onlyBuyer{
        require(transactionStates[id] == TransactionState.TradeConfirmed, "TransactionState State is not 'TradeConfirmed'");
        transactionStates[transactionHash] = TransactionState.TransferIncepted;
        transactionKeys[transactionHash].keySeller = keyEncryptedSeller;
        emit AssetTransferIncepted(buyerAddress,id);

    }


    function confirmTransfer(uint id, int amount, address to, string memory keyEncryptedBuyer) external override onlySeller{
        require(transactionStates[id] == TransactionState.TransferIncepted, "TransactionState State is not 'TransferIncepted'");
        transactionStates[transactionHash] = TransactionState.TradeConfirmed;
        transactionKeys[transactionHash].keyBuyer = keyEncryptedBuyer;
        emit AssetTransferConfirmed(sellerAddress,id);
    }


    function transferWithKey(uint id, string memory key) external{
        uint256 hashedKey = uint256(keccak256(abi.encode(key)));
        if (msg.sender == sellerAddress)
            emit AssetReclaimed(id,key);
        if (msg.sender == buyerAddress)
            emit AssetClaimed(id,key);
    }
}