// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0 <0.9.0;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

interface IERC20Settlement is IERC20 {

    function settlementTransfer(address to, uint256 value, uint256 transactionID) external;

    function settlementTransferFrom(address from, address to, uint256 value, uint256 transactionID) external ;

    function settlementBatchTransfer(address[] memory to, uint256[] memory values, uint256 transactionID ) external;

    function settlementBatchTransferFrom(address[] memory from, address[] memory to, uint256[] memory values, uint256 transactionID ) external;


}
