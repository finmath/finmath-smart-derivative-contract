# SDC - Smart Derivative Contract

## DvP at Trade Inception - Upfront Payments


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
  - inceptTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external;
  - confirmTrade(address _withParty, string memory _tradeData, int _position, int256 _paymentAmount, string memory _initialSettlementData) external;
  - initiateSettlement() external;
  - performSettlement(int256 settlementAmount, string memory settlementData) external;
  - requestTradeTermination(string memory tradeId, int256 _terminationPayment) external;
  - confirmTradeTermination(string memory tradeId, int256 _terminationPayment) external;
  - afterTransfer(uint256 transactionHash, bool success) external;

## SDC.sol

Abstract Contract: abstract contract SDC is ISDC
- Defines Trade states: Inactive, Incepted, Confirmed, Valuation, InTransfer, Settled, Terminated
- Holds internal tradeState variable
- Holds addresses of party1 and party2 and receivingParty
- Holds tradeID and tradeData
- Holds arrays for settlement data and amounts
- Holds IERC20Settlement settlementToken
- Defines utility functions (abs, min, max, otherParty)
- Defines modifiers

### IERC20Settlement.sol
- Extension of the IERC20 token standard
- additional functions check before doing (batch) transfers

### ERC20Settlement.sol
- Implements IERC20Settlement Interface
- Hold the balance of: SDC, Party 1, Party 2
- ERC20 based transfers get checked and token transaction exceptions get catched
- SDC gets called backed by calling the function afterTransfer with a successflag
- Batch transfers are enabled also where balances get checked in advance - all or nothing

### ERC20SettlementTrigger.sol
- Implements IERC20Settlement Interface
- Hold the balance of: SDC, Party 1, Party 2
- implements the pattern of a payment booking to be executed off chain
- all settlementTransfer functions just emit an event with the transfer information which can be catched by an off chain listener
- a callback function performBalanceUpdateAfterTransfer updates the token balances and calls back SDC afterTransfer


### Implementations of ISDC

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
- Interface should be used in the bond case for: issuing a bond, selling or buying a bond, paying a coupon and redeeming a bond
- In comparision to an OTC derivative the contractual structure is different: 
- We have: One issuer, N subscriber, and after issuance is finished whe have N:M buyers and sellers
- The position of the issuer is unique. He is allowed to mint the inintial balance and control the issuance process, pay the coupons and redeem the bond
- possible implementation and meaning of the interface functions
  - incept and confirm:
  - initiate and performSettlement:
  - request and confirmTradeTermiantion;

