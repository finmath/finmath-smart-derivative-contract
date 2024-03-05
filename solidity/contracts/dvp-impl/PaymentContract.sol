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

    mapping(uint256 => TransactionSpec)     transactionMap;

    address sellerAddress;
    address buyerAddress;

    constructor(address _sellerAddress, address _buyerAddress){
        sellerAddress = _sellerAddress;
        buyerAddress = _buyerAddress;
    }


    function inceptTransfer(uint id, int amount, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override{
        transactionMap[id] = TransactionSpec(from,msg.sender,amount,keyEncryptedSuccess,keyEncryptedFailure);
        emit PaymentTransferIncepted(msg.sender, id, amount);
    }


    function transferAndDecrypt(uint id, address from, address to, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override{

    }

    function cancelAndDecrypt(uint id, address from, address to, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external{

    }
}