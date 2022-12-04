---
eip: <to be assigned>
title: Smart Derivative Contract
description: A protocol for deterministic post-trade processing of OTC financial contracts
author: Christian Fries (@cfries), Peter Kohl-Landgraf (@pekola), ...
discussions-to: https://ethereum-magicians.org/
status: Draft
type: Standard Track
category (*only required for Standards Track): ERC
created: 2022-11-21
requires (*optional): <EIP number(s)>
---

## Abstract
The Smart Derivative Contract is a fully automatable protocol to incept and process 
financial derivative contracts frictionless in a fully decentralized way. Counterparty related risks are removed. 
Operational risks are removed by construction as all process states are fully specified in advance. 


## Motivation
By their very nature so-called "over-the-counter (otc)" financial contracts are bilateral contractual agreements on the exchange of future cash flow schedules.
Since these contracts change their intrinsic market value due to changing market environments they are subject to couterparty related risks.
At a smart derivative contract net present value of such a contract will be reset to zero on pre-adreed scheduled basis. Margin buffers are locked in advance 
such that settlement is guaranteed. In case a counterparty fails to obey contract rules the contract will terminate with pre-adreed rules.
These features enable two counterparties to process their financial contract without trusting in a third central intermediary agent.
The process logic can be implemtend as a finite state machine on solidity. ERC20 Standard can be used (see implementation) that protocol can be made applicable for any token, on which both counterparties agree on.
Combined with an external market data or valuation oracle every known otc derivative contract can be processed using this standard.

### Rethinking Financial Derivatives
The initial white paper describes the concept of a Smart Derivative Contract with the central aim to detach bilaleral financial transactions from counterparty credit risk.
https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3249430

### Concept of a Smart Derivative Contract
The concept of a Smart Derivative Contract was introduced in the following paper: https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3163074
A Smart Derivative Contract is a deterministic settlement protocol which has economically the same behaviour as a collateralized OTC
Derivative. In contrast to a collateralized derivative contract based and collateral flows are netted such that the smart derivative
introduces a high frequent - e.g. daily - settlement flow schedule. With each settlement flow  net present value of the underlying contract is
exchanged and the value of the contract is reset to zero.
To automatically process the settlement counterparties need to provide sufficient prefunded margin amounts and termination fees at the
beginning of each settlement cycle. Through a settlement cycle the margin amounts are locked.
A SDC contract automatically terminates the derivatives contract if there is insufficient prefunding or if the settlement amount exceeds a
prefunded margin balance. Beyond mutual termination is also intended by the function specification.


## Specification
The key words “MUST”, “MUST NOT”, “REQUIRED”, “SHALL”, “SHALL NOT”, “SHOULD”, “SHOULD NOT”, “RECOMMENDED”, “MAY”, and “OPTIONAL” in this document are to be interpreted as described in RFC 2119.
The technical specification should describe the syntax and semantics of any new feature. The specification should be detailed enough to allow competing, interoperable implementations for any of the current Ethereum platforms (go-ethereum, parity, cpp-ethereum, ethereumj, ethereumjs, and [others](https://github.com/ethereum/wiki/wiki/Clients)).

### Methods
The following methods specify the inception and post-trade live cycle of a Smart Derivative Contract.
#### inceptTrade
A counterparty can initiate a trade by providing trade data as string and calling inceptTrade. Only registered counteparties are allowed to use that function.
``` js
function inceptTrade(string memory _tradeData) external;
```
#### confirmTrade
A counterparty can confirm a trade by providing the identical trade data which are already stored from inceptTrade call.

``` js
function confirmTrade(string memory _tradeData) external;
```

#### marginAccountUnlockRequest
Calling this method a request to unlock the margin accounts will be initiated. In a possible implementation unlock is called on a scheduled basis.
``` js
function marginAccountUnlockRequest() external;
```

### initiateMarginAccountCheck
Allows registered participants to initiate margin account check on which margin balances will be checked against contract terms
``` js
function initiateMarginAccountCheck() external;
```

### performMarginAccountCheck
Method to perform a margin account check. In case of external services involved this method could serve as callback where balances are provided from e.g. an oracle service
``` js
function performMarginAccountCheck(uint256 balanceParty1, uint256 balanceParty2) external;
```

### initiateSettlement
Allowes eligible particiants (e.g. counterparties) to initiate a settlement.
``` js
function initiateSettlement() external;
```

### performSettlement
Valuation may be provided off-chain via an Oracle Service. 
Method serves as callback called from an external service providing settlement amount and market data. 
Provided Settlement Amount will be checked according to contract terms.
``` js
function performSettlement(int256 settlementAmount, string memory marketData) external;
```

### initiateMarginRequirementUpdate
Method initates an update of margin buffer amounts. 
``` js
function initiateMarginReqirementUpdate() external;
```

### performMarginRequirementUpdate
Method to perform an update of margin buffer amounts for a registered party address
``` js
function performMarginRequirementUpdate(address _address, uint256 amount) external;
```

### requestTermination
Method enables eligible party to request a mutual termination.
``` js
function requestTradeTermination(string memory tradeId) external;
```

### confirmTradeTermination
With this method eligible party is allowed to confirm a former requested mutual trade termination
``` js
function confirmTradeTermination(string memory tradeId) external;
```


## Test Cases
A full live-cycle unit test based on the sample implementation and usage of erc20 token is provided. See folder '/assets'.

## Reference Implementations
### SDC sample implementation in solidity
A first native implementation of SDC is provided. 

### Oracle Valuation Service Functionality

## Literature



<!--
## Rationale
The rationale fleshes out the specification by describing what motivated the design and why particular design decisions were made. It should describe alternate designs that were considered and related work, e.g. how the feature is supported in other languages.

## Backwards Compatibility
All EIPs that introduce backwards incompatibilities must include a section describing these incompatibilities and their severity. The EIP must explain how the author proposes to deal with these incompatibilities. EIP submissions without a sufficient backwards compatibility treatise may be rejected outright.

## Test Cases
Test cases for an implementation are mandatory for EIPs that are affecting consensus changes.  If the test suite is too large to reasonably be included inline, then consider adding it as one or more files in `../assets/eip-####/`.

## Reference Implementation
An optional section that contains a reference/example implementation that people can use to assist in understanding or implementing this specification.  If the implementation is too large to reasonably be included inline, then consider adding it as one or more files in `../assets/eip-####/`.

## Security Considerations
All EIPs must contain a section that discusses the security implications/considerations relevant to the proposed change. Include information that might be important for security discussions, surfaces risks and can be used throughout the life cycle of the proposal. E.g. include security-relevant design decisions, concerns, important discussions, implementation-specific guidance and pitfalls, an outline of threats and risks and how they are being addressed. EIP submissions missing the "Security Considerations" section will be rejected. An EIP cannot proceed to status "Final" without a Security Considerations discussion deemed sufficient by the reviewers.
-->
## Copyright
Copyright and related rights waived via [CC0](../LICENSE.md).
