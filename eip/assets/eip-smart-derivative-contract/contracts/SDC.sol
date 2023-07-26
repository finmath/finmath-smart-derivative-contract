pragma solidity >=0.7.0 <0.9.0;

import "./ISDC.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";



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

abstract contract SDC is ISDC {
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
         * Active (Confirmend + Prefunded Termination Fees). Will cycle through process states.
         */
        Active,

        /*
         * Terminated.
         */
        Terminated
    }

    /*
     * Process States. t < T* (vor incept). The process runs in cycles. Let i = 0,1,2,... denote the index of the cycle. Within each cycle there are times
     * T_{i,0}, T_{i,1}, T_{i,2}, T_{i,3} with T_{i,1} = pre-funding of the Smart Contract, T_{i,2} = request valuation from oracle, T_{i,3} = perform settlement on given valuation, T_{i+1,0} = T_{i,3}.
     * Given this time discretization the states are assigned to time points and time intervalls:
     * Idle: Before incept or after terminate
     * Initiation: T* < t < T_{0}, where T* is time of incept and T_{0} = T_{0,0}
     * AwaitingFunding: T_{i,0} < t < T_{i,1}
     * Funding: t = T_{i,1}
     * AwaitingSettlement: T_{i,1} < t < T_{i,2}
     * ValuationAndSettlement: T_{i,2} < t < T_{i,3}
     * Settled: t = T_{i,3}
     */
    enum ProcessState {
        /**
         * @dev The process has not yet started or is terminated
         */
        Idle,
        /*
         * @dev The process is initiated (incepted, but not yet completed confimation). Next: Initiation
         */
        Initiation,
        /*
         * @dev Process State Settled
         */
        Settled,
        /*
         * @dev The Valuation Phase
         */
        Valuation,
        /*
         * @dev The settlement phase is initiated.
         */
        SettlementPhase
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
        require(processState == ProcessState.Settled, "Process state is not 'Settled'."); _;
    }
    modifier onlyWhenValuation() {
        require(processState == ProcessState.Valuation, "Process state is not 'Valuation'."); _;
    }
    modifier onlyWhenSettlementPhase() {
        require(processState == ProcessState.SettlementPhase, "Process state is not 'SettlementPhase'."); _;
    }

    TradeState internal tradeState;
    ProcessState internal processState;

    modifier onlyCounterparty() {
        require(msg.sender == party1 || msg.sender == party2, "You are not a counterparty."); _;
    }

    address internal party1;
    address internal party2;
    address internal receivingParty; // Determine the receiver: Positive values are consider to be received by receivingPartyAddress. Negative values are received by the other counterparty.

    string internal tradeID;
    string internal tradeData;


    /*
     * liquidityToken holds:
     * - funding account of party1
     * - funding account of party2
     * - account for SDC (sum - this is split among parties by sdcBalances)
     */
    IERC20 internal settlementToken;

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

    function getProcessState() public view returns (ProcessState) {
        return processState;
    }

    /**
     * Other party
     */
    function otherParty(address party) internal view returns (address) {
        return (party == party1 ? party2 : party1);
    }

    /*
     * Setters/Getters
     * TODO: Check modifiers!
     */

    function getReceivingParty() public view onlyCounterparty returns (address) {
        return receivingParty;
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