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

    // Implementation of the interface IDecryptionContract

    function inceptTransfer(bytes32 id, int amount, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override {
        transactionMap[id] = TransactionSpec(from, msg.sender, amount, keyEncryptedSuccess, keyEncryptedFailure);
        emit TransferIncepted(id, amount, from, msg.sender, keyEncryptedSuccess, keyEncryptedFailure);
    }

    function transferAndDecrypt(bytes32 id, int amount, address to, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override {
        // verify transaction spec
        require(amount == transactionMap[id].amount);
        require(msg.sender == transactionMap[id].from);
        require(to == transactionMap[id].to);
        require(keccak256(abi.encodePacked(keyEncryptedSuccess)) == keccak256(abi.encodePacked(transactionMap[id].encryptedKeySuccess)));
        require(keccak256(abi.encodePacked(keyEncryptedFailure)) == keccak256(abi.encodePacked(transactionMap[id].encryptedKeyFailure)));
    }

    function cancelAndDecrypt(bytes32 id, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external {
        // verify transaction spec
        require(from == transactionMap[id].from);
        require(msg.sender == transactionMap[id].to);
        require(keccak256(abi.encodePacked(keyEncryptedSuccess)) == keccak256(abi.encodePacked(transactionMap[id].encryptedKeySuccess)));
        require(keccak256(abi.encodePacked(keyEncryptedFailure)) == keccak256(abi.encodePacked(transactionMap[id].encryptedKeyFailure)));
    }
}