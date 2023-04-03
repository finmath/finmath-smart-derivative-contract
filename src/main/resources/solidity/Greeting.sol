// SPDX-License-Identifier: MIT
pragma solidity >=0.7.0 <0.9.0;

contract Greeter {
    string greeting;

    constructor() {
        greeting = "Hello World";
    }

    function greet() public view returns (string memory){
        return greeting;
    }

    function store(string calldata str) public {
        greeting = str;
    }
}