// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0 <0.9.0;

import "./ISDC.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "./IERC20Settlement.sol";

contract ERC20SettlementTrigger is ERC20, IERC20Settlement {

    modifier onlySDC() {
        require(msg.sender == sdcAddress, "Only allowed to be called from SDC Address"); _;
    }

    event TriggerTransferEvent(address[] from, address[] to, uint256[] amounts);


    address   sdcAddress;
    address[] partyAddresses;

    constructor() ERC20("SDCToken", "SDCT") {

    }

    //TODO how to translate bookings to SDCAdress
    // TODO what to do with tokens in transfer

    function settlementTransfer(address to, uint256 value, uint256 transactionID) public onlySDC{

    }

    function settlementTransferFrom(address from, address to, uint256 value, uint256 transactionID) external onlySDC {

    }

    function settlementBatchTransfer(address[] memory to, uint256[] memory values, uint256 transactionID ) public onlySDC{

    }


    function settlementBatchTransferFrom(address[] memory from, address[] memory to, uint256[] memory values, uint256 transactionID ) public onlySDC{

    }


    function performBalanceUpdateAfterTransfer(uint256 transactionID, bool success, address[] memory partyAddresses, uint256[] memory balances) public{
        ISDC(sdcAddress).afterTransfer(transactionID, success);
    }
}
