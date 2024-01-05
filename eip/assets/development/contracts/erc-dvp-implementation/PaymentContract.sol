// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0;


import "./interface/IDecryptionContract.sol";

contract PaymentContract is IDecryptionContract {

    address sellerAddress;
    address buyerAddress;

    constructor(address _sellerAddress, address _buyerAddress){
        sellerAddress = _sellerAddress;
        buyerAddress = _buyerAddress;
    }


    function inceptTransfer(uint id, int amount, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override{

    }


    function transferAndDecrypt(uint id, address from, address to, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external override{

    }

    function cancelAndDecrypt(uint id, address from, address to, string memory keyEncryptedSuccess, string memory keyEncryptedFailure) external{

    }
}