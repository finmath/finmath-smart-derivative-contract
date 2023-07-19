// SPDX-License-Identifier: CC0-1.0

pragma solidity ^0.8.0;

import "./SDC.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract SettlementToken is ERC20{

    address sdcAddress;

    constructor() ERC20("SDCToken", "SDCT") {

    }

    function mint(address to, uint256 amount) public {
        _mint(to, amount);
    }

    function transferAndCallSender(bytes memory data) public{
//        bytes memory transferPayload = abi.encodeWithSignature("transfer(address,address,uint256)", from, to, amount);
        address owner = _msgSender();
//        _transfer(owner, to, amount);
//        return true;
    }

    function transferFromAndCallSender(bytes memory data) public{
        SDC(sdcAddress).afterSettlement(true);
        // check interface ISDC
        //ISDC(msg.sender).afterSettlement()
    }
}