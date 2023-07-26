// SPDX-License-Identifier: CC0-1.0

pragma solidity ^0.8.0;

import "./SDC.sol";
import "./SDCBond.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/utils/introspection/ERC165Checker.sol";

contract SettlementToken is ERC20{

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

    function checkedTransferAndCallSender(address to, uint256 value, uint256 transactionID) public{
        require(msg.sender == sdcAddress, "call not allowed from other than SDC Address");

        if ( transfer(to,value) )
            SDCBond(sdcAddress).afterSettlement(transactionID, true);
        else
            SDCBond(sdcAddress).afterSettlement(transactionID, false);
        address owner = _msgSender();
    }

    function checkedTransferFromAndCallSender(address from, address to, uint256 value, uint256 transactionID) external {
        require(msg.sender == sdcAddress, "call not allowed from other than SDC Address");
        if (balanceOf(from)< value || allowance(from,address(msg.sender)) < value )
            SDCBond(sdcAddress).afterSettlement(transactionID, false);
        if ( transferFrom(from, to,value) )
            SDCBond(sdcAddress).afterSettlement(transactionID, true);
        else
            SDCBond(sdcAddress).afterSettlement(transactionID, false);
        address owner = _msgSender();
    }

    function checkedBatchTransferAndCallSender(address[] memory from, address[] memory to, uint256[] memory values ) public{
    }

}