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

### SettlementToken

Hold the balance of
- SDC
- Party 1
- Party 2

Two different versions:

- SDCOwnBalance.sol
  - In every settlement cycle the SDC is locking the settlement amount from the SettlementToken: 
  - tranfer between SDC and SettlementToken in every Settlement Cycle.
- SDCPldegedBalance.sol
  - During settlement transfer between adresses on the SettlementToken
  - If that fails, transfer from SDC to Parties

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

## SDCPledgedBalance

contract SDCPledgedBalance is SDC

Past and current settlement

- Holds int256[] private settlementAmounts;
- Holds string[] private settlementData;

Implementation

- afterSettlement(bool success) external override onlyWhenSettlementPhase
  - Change trade states
- initiateSettlement()
  - emit message
- performSettlement(int256 settlementAmount, string memory _settlementData) onlyWhenValuation external override
  - push settlement data
  - try to transfer from settlementToken
    - if fail initiate pledge case


**Is this the current version?**


    * Setup with SDC holding tokens
     *
     *  Settlement:
     *  _bookSettlement
     *      Update internal balances
     *      Message
     *  _transferSettlement
     *      Book SDC -> Party1:   X
     *      Book SDC -> Party2:   0
     *  Rebalance (was: Perform Funding)
     *      Book Party2 -> SDC:   X
     *      Rebalance Check
     *          Failed
     *              Terminate
     *
     * Setup with Pledge Account
     *
     *  Settlement:
     *  _bookSettlement
     *      Update internal balances
     *      Message
     *  Rebalance:
     *      Book Party2 -> Party1:   X
     *      Rebalance Check
     *          Failed
     *              Book SDC -> Party1:   X
     *              Terminate

# Questions

- settlementAmounts not pushed in inceptTrade
- performSettlement with try-catch?

- Why is SDCBond not an SDC
- 
# SDCDefaultable

Wie SDCPledge nur ohne Pledge


# SDCBond


