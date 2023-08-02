# SDC - Smart Derivative Contract

## ISDC.sol

Interface:

- Events:
  - TradeIncepted(address initiator, string tradeId, string tradeData);
  - TradeConfirmed(address confirmer, string tradeId);
  - TradeActivated(string tradeId);
  - TradeTerminated(string cause);
  - ProcessSettlementPhase();
  - ProcessSettled();
  - ProcessSettlementRequest(string tradeData, string lastSettlementData);
  - TradeTerminationRequest(address cpAddress, string tradeId);
  - TradeTerminationConfirmed(address cpAddress, string tradeId);
  - ProcessHalted(string message);
- Functions:
  - inceptTrade(string memory _tradeData, string memory _initialSettlementData, int256 _upfrontPayment) external;
  - confirmTrade(string memory _tradeData, string memory _initialSettlementData, int256 _upfrontPayment) external;
  - afterSettlement(bool success) external;
  - initiateSettlement() external;
  - performSettlement(int256 settlementAmount, string memory settlementData) external;
  - requestTradeTermination(string memory tradeId, int256 _terminationPayment) external;
  - confirmTradeTermination(string memory tradeId, int256 _terminationPayment) external;

## SDC.sol

Abstract Contract: abstract contract SDC is ISDC

- Defines TradeState and ProcessStates
- Holds adresses of party1 and party2
- Holds tradeID and tradeData
- Holds IERC20 settlementToken
- Defines utility functions (abs, min, max, otherParty)

## SDCOwnBalance.sol

- Defines MargiRequirement
- Holds mapping(address => MarginRequirement) private marginRequirements;
- Holds mapping(address => int256) private sdcBalances;

Transfers margin requirements from settlement token to this.
Tracks balance in sdcBalances

Internal Functions:

- function _lockTerminationFees() internal returns(bool)
- function _processTermination() internal

Implementation

- afterSettlement(bool success) external override onlyWhenSettlementPhase
  - calculate "gap amounts" after previous settlement
  - transfer gap amounts from settlement token
  - terminate if required

- initiateSettlement() external override onlyCounterparty onlyWhenSettled
  - change state
  - emit message

- performSettlement(int256 settlementAmount, string memory settlementData)
  - adjust internal balance
  - terminate if required

