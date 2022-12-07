---
eip: <to be assigned>
title: Smart Derivative Contract
description: A deterministic protocol for frictionless post-trade processing of OTC financial contracts
author: Christian Fries (@cfries), Peter Kohl-Landgraf (@pekola), Alexandros Korpis
discussions-to: https://ethereum-magicians.org/
status: Draft
type: Standard Track
category (*only required for Standards Track): ERC
created: 2022-11-21
requires (*optional): <EIP number(s)>
---

## Abstract
The Smart Derivative Contract is a deterministic protocol to trade and process 
financial derivative contracts frictionless in a fully automated way. Counterparty credit risk ís removed. 
Known operational risks and complexities in post-trade processing are removed by construction as all process states are fully specified and are known to the counterparties.

## Motivation
### Rethinking Financial Derivatives
By their very nature so-called "over-the-counter (otc)" financial contracts are bilateral contractual agreements on the exchange of long-dated cash flow schedules.
Since these contracts change their intrinsic market value due to changing market environments they are subject to couterparty credit risk.
The initial white paper describes the concept of a Smart Derivative Contract with the central aim 
to detach bilaleral financial transactions from counterparty credit risk and to remove complexities in bilateral post-trade processing by a complete redesign.
https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3249430

### Concept of a Smart Derivative Contract
A Smart Derivative Contract is a deterministic settlement protocol which has economically the same behaviour as a collateralized OTC
Derivative. Every process state is specified and therefore known in advance.
A Smart Derivative Contract (SDC) settles outstanding net present value of the underlying financial contract on a frequent basis. With each settlement flow  net present value of the underlying contract is
exchanged and the value of the contract is reset to zero. Preagreed margin buffers are locked at the beginning of each settlement cycle such that settlement will be guaranteed up to a certain amount. 
In case a counterparty fails to obey contract rules, e.g. not provide sufficient prefunding, SDC will terminate automatically with the guaranteed transfer of a termination fee by the causing party.
These features enable two counterparties to process their financial contract fully decentralized without relying on a third central intermediary agent.
Process logic of SDC can be implemented as a finite state machine on solidity. ERC20 token standard can be used for frictionless decentralized settlement - see reference implementation.
Combined with an appropriate external market data and valuation oracle which calculates the net present value, every known otc derivative contract is able to be processed using this standard.


## Specification

### Methods
The following methods specify  inception and post-trade live cycle of a Smart Derivative Contract. For futher information also please look at the interface documentation ISDC.sol.
#### inceptTrade
A counterparty can initiate a trade by providing trade data as string and calling inceptTrade and initial settlement data. Only registered counteparties are allowed to use that function.
``` js
function inceptTrade(string memory _tradeData, string memory _initialSettlementData) external
```
#### confirmTrade
A counterparty can confirm a trade by providing the identical trade data and initial settlement information which are already stored from inceptTrade call.

``` js
function confirmTrade(string memory _tradeData, string memory _initialSettlementData) external;
```

#### initiatePrefunding
This method checks whether contractual prefunding is provided by both counterparties as agreed in the contract terms. Triggers a contract termination if not. 
``` js
function initiatePrefunding() external;
```

#### initiateSettlement
Allowes eligible particiants - e.g. counterparties or a delegated agent - to initiate a settlement.
``` js
function initiateSettlement() external;
```

#### performSettlement
Valuation may be provided off-chain via an external oracle service with calculates net present value and uses external market data. 
Method serves as callback called from an external oracle providing settlement amount and used settlement data which also get stored.
Settlement amount will be checked according to contract terms resulting in either a reqular settlement or a termination of the trade.
``` js
function performSettlement(int256 settlementAmount, string memory settlementData) external;
```

#### requestTermination
Method enables an eligible party to request a mutual termination.
``` js
function requestTradeTermination(string memory tradeId) external;
```

#### confirmTradeTermination
With this method eligible party is allowed to confirm a former requested mutual trade termination
``` js
function confirmTradeTermination(string memory tradeId) external;
```

### Trade Events
The following events specifiy a trade live cycle.

#### TradeIncepted
Emitted on trade inception - method `inceptTrade`
``` js
event TradeIncepted(address initiator, string tradeId, string tradeData);
```

#### TradeConfirmed
Emitted on trade confirmation - method `confirmTrade`
``` js
event TradeConfirmed(address confirmer, string tradeId);
```

#### TradeActivated
Emitted when trade is activated
``` js
event TradeActivated(string tradeId);
```

#### TradeTerminationRequest
Emitted when termination request is initiated by a counterparty
``` js
event TradeTerminationRequest(address cpAddress, string tradeId);
```

#### TradeTerminationConfirmed
Emitted when termination request is confirmed by a counterparty
``` js
event TradeTerminationConfirmed(address cpAddress, string tradeId);
```

#### TradeTerminated
Emitted when trade is terminated
``` js
event TradeTerminated(string cause);
```

### Process Events
The following events define the SDC's process live cycle.

#### ProcessAwaitingFunding
Emitted when funding phase is initiated
``` js
event ProcessAwaitingFunding();
```
#### ProcessFunded
Emitted when funding has completed successfully - method `initiatePrefunding`
``` js
event ProcessFunded();
```
#### ProcessSettlementRequest
Emitted when a settlement is initiated - method `initiateSettlement`
``` js
event ProcessSettled(string tradeData, string lastSettlementData);
```
#### ProcessSettled
Emitted when settlement was processed successfully - method 'performSettlement'
``` js
event ProcessSettled();
```

## Rationale
The rationale fleshes out the specification by describing what motivated the design and why particular design decisions were made. It should describe alternate designs that were considered and related work, e.g. how the feature is supported in other languages.

## Test Cases
A full live-cycle unit test based on the sample implementation and usage of erc20 token is provided. See folder '/assets'.

## Reference Implementations
A first native implementation of SDC is provided based on ERC20 token standard.

### Oracle Valuation Service Functionality
External Valuation Oracle functionality is available on github.

## Literature

## Copyright
Copyright and related rights waived via ...


<!--
The key words “MUST”, “MUST NOT”, “REQUIRED”, “SHALL”, “SHALL NOT”, “SHOULD”, “SHOULD NOT”, “RECOMMENDED”, “MAY”, and “OPTIONAL” in this document are to be interpreted as described in RFC 2119.
The technical specification should describe the syntax and semantics of any new feature. The specification should be detailed enough to allow competing, interoperable implementations for any of the current Ethereum platforms (go-ethereum, parity, cpp-ethereum, ethereumj, ethereumjs, and [others](https://github.com/ethereum/wiki/wiki/Clients)).
## Rationale
## Backwards Compatibility
All EIPs that introduce backwards incompatibilities must include a section describing these incompatibilities and their severity. The EIP must explain how the author proposes to deal with these incompatibilities. EIP submissions without a sufficient backwards compatibility treatise may be rejected outright.

## Test Cases
Test cases for an implementation are mandatory for EIPs that are affecting consensus changes.  If the test suite is too large to reasonably be included inline, then consider adding it as one or more files in `../assets/eip-####/`.

## Reference Implementation
An optional section that contains a reference/example implementation that people can use to assist in understanding or implementing this specification.  If the implementation is too large to reasonably be included inline, then consider adding it as one or more files in `../assets/eip-####/`.

## Security Considerations
All EIPs must contain a section that discusses the security implications/considerations relevant to the proposed change. Include information that might be important for security discussions, surfaces risks and can be used throughout the life cycle of the proposal. E.g. include security-relevant design decisions, concerns, important discussions, implementation-specific guidance and pitfalls, an outline of threats and risks and how they are being addressed. EIP submissions missing the "Security Considerations" section will be rejected. An EIP cannot proceed to status "Final" without a Security Considerations discussion deemed sufficient by the reviewers.
-->

