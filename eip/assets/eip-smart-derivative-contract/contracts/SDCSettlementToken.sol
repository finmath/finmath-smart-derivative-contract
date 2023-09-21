// SPDX-License-Identifier: CC0-1.0

pragma solidity ^0.8.0;

import "./ISDC.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/utils/introspection/ERC165Checker.sol";

contract SDCSettlementToken is ERC20{

    using ERC165Checker for address;

    address sdcAddress;

    constructor() ERC20("SDCToken", "SDCT") {

    }

    function setSDCAddress(address _sdcAddress) public{
//        bool supportsISDB = _sdcAddress.supportsInterface(bytes4(keccak256(bytes("ISDB"))));
//        require ( supportsISDB == true , "not a valid SDC Address");
        sdcAddress = _sdcAddress;
    }

    function mint(address to, uint256 amount) public {
        _mint(to, amount);
    }

    function checkedTransferAndCall(address to, uint256 value, uint256 transactionID) public{
        require(msg.sender == sdcAddress, "call not allowed from other than SDC Address");
        try this.transfer(to,value) returns (bool transferSuccessFlag) {
            ISDC(sdcAddress).afterSettlement(transactionID, transferSuccessFlag);
        }
        catch{
            ISDC(sdcAddress).afterSettlement(transactionID, false);
        }
    }

    function checkedTransferFromAndCall(address from, address to, uint256 value, uint256 transactionID) external {
        require(msg.sender == sdcAddress, "call not allowed from other than SDC Address");
        if (this.balanceOf(from)< value || this.allowance(from,address(msg.sender)) < value )
            ISDC(sdcAddress).afterSettlement(transactionID, false);
        try this.transfer(to,value) returns (bool transferSuccessFlag) {
            ISDC(sdcAddress).afterSettlement(transactionID, transferSuccessFlag);
        }
        catch{
            ISDC(sdcAddress).afterSettlement(transactionID, false);
        }
        address owner = _msgSender();
    }

    function checkedBatchTransferAndCall(address[] memory to, uint256[] memory values, uint256 transactionID ) public{
        require (to.length == values.length, "Array Length mismatch");
        require(msg.sender == sdcAddress, "Call not allowed from other than SDC Address");
        uint256 requiredBalance = 0;
        for(uint256 i = 0; i < values.length; i++)
            requiredBalance += values[i];
        if (balanceOf(msg.sender) < requiredBalance){
            ISDC(sdcAddress).afterSettlement(transactionID, false);
            return;
        }
        else{
            for(uint256 i = 0; i < to.length; i++){
                transfer(to[i],values[i]);
            }
            ISDC(sdcAddress).afterSettlement(transactionID, true);
        }
    }


    function checkedBatchTransferFromAndCall(address[] memory from, address[] memory to, uint256[] memory values, uint256 transactionID ) public{
        require (from.length == to.length, "Array Length mismatch");
        require (to.length == values.length, "Array Length mismatch");
        require(msg.sender == sdcAddress, "Call not allowed from other than SDC Address");
        uint256[] memory requiredBalances;
        for(uint256 i = 0; i < from.length; i++){
            address fromAddress = from[i];
            uint256 totalRequiredBalance = 0;
            for(uint256 j = 0; j < from.length; j++){
                if (from[j] == fromAddress)
                    totalRequiredBalance += values[j];
            }
            if (balanceOf(fromAddress) <  totalRequiredBalance){
                ISDC(sdcAddress).afterSettlement(transactionID, false);
                break;
            }

        }
        for(uint256 i = 0; i < to.length; i++){
            transferFrom(from[i],to[i],values[i]);
        }
        ISDC(sdcAddress).afterSettlement(transactionID, true);
    }

}