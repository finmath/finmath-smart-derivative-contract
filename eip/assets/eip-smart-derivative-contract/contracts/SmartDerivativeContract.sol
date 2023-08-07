pragma solidity >=0.7.0 <0.9.0;

import "./ISDC.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";
import "./SDCSettlementToken.sol";



/**
 * @title Reference Implementation of ERC6123 - Smart Derivative Contract
 * @notice This reference implementation is based on a finite state machine with predefined trade and process states (see enums below)
 * Some comments on the implementation:
 * - trade and process states are used in modifiers to check which function is able to be called at which state
 * - trade data are stored in the contract
 * - trade data matching is done in incept and confirm routine (comparing the hash of the provided data)
 * - ERC-20 token is used for three participants: counterparty1 and counterparty2 and sdc
 * - when prefunding is done sdc contract will hold agreed amounts and perform settlement on those
 * - sdc also keeps track on internal balances for each counterparty
 * - during prefunding sdc will transfer required amounts to its own balance - therefore sufficient approval is needed
 * - upon termination all remaining 'locked' amounts will be transferred back to the counterparties
*/

abstract contract SmartDerivativeContract is ISDC {
    /*
     * Trade States
     */
    enum TradeState {

        /*
         * State before the trade is incepted.
         */
        Inactive,

        /*
         * Incepted: Trade data submitted by one party. Market data for initial valuation is set.
         */
        Incepted,

        /*
         * Confirmed: Trade data accepted by other party.
         */
        Confirmed,

        /*
         * Valuation Phase
         */
        Valuation,

        /*
         * A Token-based Transfer is in Progress
         */
        InTransfer,

        /*
         * Settlement is Completed
         */
        Settled,

        /*
         * Terminated.
         */
        Terminated
    }

    /*
    * Modifiers serve as guards whether at a specific process state a specific function can be called
    */


    modifier onlyWhenTradeInactive() {
        require(tradeState == TradeState.Inactive, "Trade state is not 'Inactive'."); _;
    }
    modifier onlyWhenTradeIncepted() {
        require(tradeState == TradeState.Incepted, "Trade state is not 'Incepted'."); _;
    }
    modifier onlyWhenSettled() {
        require(tradeState == TradeState.Settled, "Trade state is not 'Settled'."); _;
    }
    modifier onlyWhenValuation() {
        require(tradeState == TradeState.Valuation, "Trade state is not 'Valuation'."); _;
    }
    modifier onlyWhenSettlementPhase() {
        require(tradeState == TradeState.InTransfer, "Trade state is not 'InTransfer'."); _;
    }

    TradeState internal tradeState;

    modifier onlyCounterparty() {
        require(msg.sender == party1 || msg.sender == party2, "You are not a counterparty."); _;
    }

    address internal party1;
    address internal party2;
    address internal receivingParty;

    string internal tradeID;
    string internal tradeData;
    mapping(uint256 => address) internal pendingRequests; // Stores open request hashes for several requests: initiation, update and termination
    bool internal mutuallyTerminated = false;
    int256 terminationPayment;

    /*
     * SettlementToken holds:
     * - balance of party1
     * - balance of party2
     * - balance for SDC
     */
    SDCSettlementToken internal settlementToken;


    constructor(
        address _party1,
        address _party2,
        address _settlementToken
    ) {
        party1 = _party1;
        party2 = _party2;
        settlementToken = SDCSettlementToken(_settlementToken); // TODO: Check if contract at given address supports interface
        tradeState = TradeState.Inactive;
    }
    /*
         * generates a hash from tradeData and generates a map entry in openRequests
         * emits a TradeIncepted
         * can be called only when TradeState = Incepted
         */
    function inceptTrade(address _withParty, string memory _tradeData, int _position, uint256 _units, uint256 _paymentAmountPerUnit, string memory _initialSettlementData) external override onlyCounterparty onlyWhenTradeInactive {
        require(msg.sender != _withParty, "Calling party cannot be the same as withParty");
        require(_position == 1 || _position == -1, "Position can only be +1 or -1");
        require(_units == 1, "Current Implementation only allows units=1");
        tradeState = TradeState.Incepted; // Set TradeState to Incepted
        uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData,_position, _units, _paymentAmountPerUnit)));
        pendingRequests[transactionHash] = msg.sender;
        receivingParty = _position == 1 ? msg.sender : _withParty;
        tradeID = Strings.toString(transactionHash);
        tradeData = _tradeData; // Set trade data to enable querying already in inception state
        emit TradeIncepted(msg.sender, tradeID, _tradeData);
    }

    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmed
     * can be called only when TradeState = Incepted
     */
    function confirmTrade(address _withParty, string memory _tradeData, int _position, uint256 _units, uint256 _paymentAmountPerUnit, string memory _initialSettlementData) external override onlyCounterparty onlyWhenTradeIncepted
    {
        address inceptingParty = msg.sender == party1 ? party2 : party1;
        uint256 transactionHash = uint256(keccak256(abi.encode(_withParty,msg.sender,_tradeData,-_position, _units, _paymentAmountPerUnit)));
        require(pendingRequests[transactionHash] == inceptingParty, "Confirmation fails due to inconsistent trade data or wrong party address");
        delete pendingRequests[transactionHash]; // Delete Pending Request
        tradeState = TradeState.Confirmed;
        emit TradeConfirmed(msg.sender, tradeID);
        uint256 upfront = _units * _paymentAmountPerUnit;
        processTradeAfterConfirmation(upfront);
    }


    /*
    * Can be called by a party for mutual termination
    * Hash is generated an entry is put into pendingRequests
    * TerminationRequest is emitted
    * can be called only when ProcessState = Funded and TradeState = Active
    */
    function requestTradeTermination(string memory _tradeId, int256 _terminationPayment) external override onlyCounterparty onlyWhenSettled {
        require(keccak256(abi.encodePacked(tradeID)) == keccak256(abi.encodePacked(_tradeId)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeId, "terminate", _terminationPayment)));
        pendingRequests[hash] = msg.sender;
        emit TradeTerminationRequest(msg.sender, _tradeId);
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function confirmTradeTermination(string memory _tradeId, int256 _terminationPayment) external override onlyCounterparty onlyWhenSettled {
        address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(_tradeId, "terminate", _terminationPayment)));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        mutuallyTerminated = true;
        terminationPayment = _terminationPayment;
        emit TradeTerminationConfirmed(msg.sender, _tradeId);
        processTradeAfterMutualTermination(); // to be overridden by implementing contracts
    }



    function processTradeAfterConfirmation(uint256 upfrontPayment) virtual internal;

    function processTradeAfterMutualTermination() virtual internal;

    /*
     * Utilities
    */

    /**
     * Absolute value of an integer
     */
    function abs(int x) internal pure returns (int256) {
        return x >= 0 ? x : -x;
    }

    /**
     * Maximum value of two integers
     */
    function max(int a, int b) internal pure returns (int256) {
        return a > b ? a : b;
    }

    /**
    * Minimum value of two integers
    */
    function min(int a, int b) internal pure returns (int256) {
        return a < b ? a : b;
    }


    function getTokenAddress() public view returns(address) {
        return address(settlementToken);
    }

    function getTradeState() public view returns (TradeState) {
        return tradeState;
    }

    /**
     * Other party
     */
    function otherParty(address party) internal view returns (address) {
        return (party == party1 ? party2 : party1);
    }


    function getTradeID() public view returns (string memory) {
        return tradeID;
    }

    function setTradeId(string memory _tradeID) public {
        tradeID= _tradeID;
    }

    function getTradeData() public view returns (string memory) {
        return tradeData;
    }


}