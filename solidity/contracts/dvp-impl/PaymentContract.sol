// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0;


import "./interface/IDecryptionContract.sol";

contract PaymentContract is IDecryptionContract {


    struct TransactionSpec {
        address from;
        address to;
        int amount;
        string  encryptedKeySuccess;
        string  encryptedKeyFailure;
    }

    mapping(bytes32 => TransactionSpec)     transactionMap;

    address sellerAddress;
    address buyerAddress;

    constructor(address _sellerAddress, address _buyerAddress){
        sellerAddress = _sellerAddress;
        buyerAddress = _buyerAddress;
    }


    function inceptTransfer(bytes32 id, int amount, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override {
        transactionMap[id] = TransactionSpec(from, msg.sender, amount, keyEncryptedSuccess, keyEncryptedFailure);
        emit TransferIncepted(msg.sender, id, amount);
    }


    function transferAndDecrypt(bytes32 id, int amount, address to, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override {
        // verify transaction spec
        TransactionSpec memory transactionSpec = TransactionSpec(msg.sender, to, amount, keyEncryptedSuccess, keyEncryptedFailure);
        require(transactionSpec.amount == transactionMap[id].amount);
        require(transactionSpec.from == transactionMap[id].from);
        require(transactionSpec.to == transactionMap[id].to);
        require(transactionSpec.encryptedKeySuccess == transactionMap[id].encryptedKeySuccess);
        require(transactionSpec.encryptedKeyFailure == transactionMap[id].encryptedKeyFailure);
    }

    function cancelAndDecrypt(bytes32 id, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external {
        // verify transaction spec
        TransactionSpec memory transactionSpec = TransactionSpec(from, msg.sender, amount, keyEncryptedSuccess, keyEncryptedFailure);
        require(transactionSpec.amount == transactionMap[id].amount);
        require(transactionSpec.from == transactionMap[id].from);
        require(transactionSpec.to == transactionMap[id].to);
        require(transactionSpec.encryptedKeySuccess == transactionMap[id].encryptedKeySuccess);
        require(transactionSpec.encryptedKeyFailure == transactionMap[id].encryptedKeyFailure);
    }
}