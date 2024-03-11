// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0;

import "./interface/ILockingContract.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract AssetContract is ILockingContract {

    //   DEFINED in INTERFACE
    //   event AssetTransferIncepted(address initiator, uint id);
    //   event AssetTransferConfirmed(address confirmer, uint id);
    //   event AssetClaimed(uint id, string key);
    //   event AssetReclaimed(uint id, string key);

    event TradeIncepted(bytes32 tradeId, address inceptor);
    event TradeConfirmed(bytes32 tradeId, address confirmer);

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

    struct TransactionKey {
        string  encryptedKeyBuyer;
        string  encryptedKeySeller;
        uint hashedKeySeller;
        uint hashedKeyBuyer;
    }

    mapping(address => uint256)             bondHolderBalances;
    mapping(bytes32 => TransactionState)    transactionStates;
    mapping(bytes32 => TransactionSpec)     transactionSpecs;
    mapping(bytes32 => TransactionKey)     transactionKeys;

    constructor() {
    }


    function inceptTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, uint256 hashedKey) external{
        address buyerAddress = _position > 0 ? msg.sender : _withParty;  // buyer receives the bond + coupons and pays cash
        address sellerAddress = _position > 0 ? _withParty : msg.sender; // seller sells the bonds and receives cash
        uint absPosition = uint256(_position);
        uint256 transferAmount = uint256(_paymentAmount);
        bytes32 transactionHash =keccak256(abi.encode(msg.sender,_withParty,_tradeData, _position, _paymentAmount));
        transactionSpecs[transactionHash] = TransactionSpec(msg.sender,buyerAddress,sellerAddress,absPosition,transferAmount,block.timestamp);
        if (msg.sender == buyerAddress)
            transactionKeys[transactionHash].hashedKeyBuyer = hashedKey;
        else
            transactionKeys[transactionHash].hashedKeySeller = hashedKey;
        transactionStates[transactionHash] = TransactionState.TradeIncepted;
        emit TradeIncepted(transactionHash, transactionSpecs[transactionHash].buyer);
    }

    function confirmTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, uint256 hashedKey) external{
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        bytes32 transactionHash = keccak256(abi.encode(_withParty, msg.sender,_tradeData,-_position, _paymentAmount ));
        require(transactionSpecs[transactionHash].inceptor == _withParty, "No pending inception available to be confirmed for this trade specification");
        if (msg.sender == transactionSpecs[transactionHash].buyer)
            transactionKeys[transactionHash].hashedKeyBuyer = hashedKey;
        else
            transactionKeys[transactionHash].hashedKeySeller = hashedKey;
        transactionStates[transactionHash] = TransactionState.TradeConfirmed;
        emit TradeConfirmed(transactionHash,msg.sender);
    }



    function inceptTransfer(bytes32 id, int amount, address from, string memory keyEncryptedSeller) external override {
        require(msg.sender == transactionSpecs[id].buyer, "You are not the Buyer.");
        require(transactionStates[id] == TransactionState.TradeConfirmed, "TransactionState State is not 'TradeConfirmed'");
        transactionStates[id] = TransactionState.TransferIncepted;
        transactionKeys[id].encryptedKeySeller = keyEncryptedSeller;
        emit TransferIncepted(transactionSpecs[id].buyer, id);
    }


    function confirmTransfer(bytes32 id, int amount, address to, string memory keyEncryptedBuyer) external override {
        require(msg.sender == transactionSpecs[id].seller, "You are not the Seller.");
        require(transactionStates[id] == TransactionState.TransferIncepted, "TransactionState State is not 'TransferIncepted'");
        transactionStates[id] = TransactionState.TradeConfirmed;
        transactionKeys[id].encryptedKeyBuyer = keyEncryptedBuyer;
        TransactionSpec memory txSpec = transactionSpecs[id];
        bondHolderBalances[txSpec.seller]  -= txSpec.notional; // Decrement Notional from Seller Holder Balance
        bondHolderBalances[address(this)]  += txSpec.notional;  // Transfer Bond to INTERNAL Balance and trigger transfer of the paymentAmount
        emit TransferConfirmed(transactionSpecs[id].seller, id);
    }


    function transferWithKey(bytes32 id, string memory key) external{
        require(msg.sender == transactionSpecs[id].seller || msg.sender == transactionSpecs[id].buyer, "You are not Seller or Buyer of the referenced Transaction.");
        uint256 hashedKey = uint256(keccak256(abi.encode(key)));
        if (msg.sender == transactionSpecs[id].seller)
            emit TokenReclaimed(id, key);
        if (msg.sender == transactionSpecs[id].buyer)
            emit TokenClaimed(id, key);
    }

    function checkHashFunction( string memory key, uint256 hashOfKey ) external returns (bool) {
        uint256 keyHashed = uint256(keccak256(abi.encode(key)));
        if ( keyHashed == hashOfKey)
            return true;
        else
            return false;
    }
}