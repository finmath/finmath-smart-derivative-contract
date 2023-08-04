// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./ISDC.sol";
import "./SettlementToken.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/introspection/IERC165.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract SmartBondContract is ISDC  {
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

    modifier onlyIssuer() {
        require(msg.sender == issuerAddress , "You are not issuer"); _;
    }

    struct TransactionSpec {
        address buyer;
        address seller;
        uint lotNumber;
        uint paymentAmount;
        uint timestamp;
    }

    address issuerAddress;
    SettlementToken internal settlementToken;

    mapping(uint256 => TradeState) tradeStates;
    mapping(uint256 => TransactionSpec) transactionSpecs;
    mapping(address => uint256) bondHolderBalances;
    uint256 bondIssuerBalance;

    string private securityData;
    string private securityID;
    uint lotSize;

    mapping(uint256 => address) private pendingInceptions;

    constructor(
        address         _issuerAddress,
        string memory   _securityData,
        string memory   _securityID,
        uint            _initialLotBalance,
        uint            _lotSize,
        address         _settlementToken
    ) {
        issuerAddress = _issuerAddress;
        securityData = _securityData;
        securityID = _securityID;
        bondHolderBalances[issuerAddress] = _initialLotBalance;
        lotSize = _lotSize;
        settlementToken = SettlementToken(_settlementToken);
     }

    function supportsInterface(bytes4 interfaceId) external view returns (bool){
        return interfaceId == bytes4(keccak256(bytes("ISDB")));
    }

    /*
     * generates a hash from tradeData and generates a map entry in openRequests
     * emits a TradeIncepted
     * can be called only when TradeState = Incepted
     */
    function inceptTrade(address _withParty, string memory _tradeData, int _position, uint256 _units, uint256 _paymentAmountPerUnit, string memory _initialSettlementData) external  override {
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        // @TODO Check eligiblity of the msg.sender
        uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,_withParty,_tradeData, _position, _units, _paymentAmountPerUnit)));
        require(keccak256(abi.encode(_tradeData)) == keccak256(abi.encode(_tradeData)), "Trade Inception request does not meet contract's underlying security specification");
        require(pendingInceptions[transactionHash] != msg.sender, "There exists already an identical pending inception for this trade");
        pendingInceptions[transactionHash] = msg.sender;
        tradeStates[transactionHash] = TradeState.Incepted;
        emit TradeIncepted(msg.sender, Strings.toString(transactionHash), "");
    }

    /*
     * generates a hash from tradeData and checks whether an open request can be found by the opposite party
     * if so, data are stored and open request is deleted
     * emits a TradeConfirmed
     * can be called only when TradeState = Incepted
     */
    function confirmTrade(address _withParty, string memory _tradeData, int _position, uint256 _units, uint256 _paymentAmountPerUnit, string memory _initialSettlementData) external override{
        require(msg.sender != _withParty, "Calling party cannot be the same as Trade Party");
        uint256 transactionHash = uint256(keccak256(abi.encode(_withParty,msg.sender,_tradeData,-_position, _units, _paymentAmountPerUnit)));
        require(pendingInceptions[transactionHash] == _withParty, "No pending inception available to be confirmed for this trade specification");
        delete pendingInceptions[transactionHash];
        address buyer = _position > 0 ? msg.sender : _withParty;  // payer buys the bonds and pays cash
        address seller = _position > 0 ? _withParty : msg.sender; // seller sells the bonds and receives cash
        require(bondHolderBalances[seller] >= _units, "Lot balance of selling party not sufficient");
        uint256 paymentAmount = uint256(_paymentAmountPerUnit) * _units;
        transactionSpecs[transactionHash] = TransactionSpec(buyer,seller,_units,_paymentAmountPerUnit*_units,block.timestamp);
        /*Transfer Bond to internal balance and trigger transfer of the paymentAmount*/
        bondHolderBalances[seller]         -= _units;
        bondHolderBalances[address(this)]  += _units;
        tradeStates[transactionHash] = TradeState.InTransfer;
        settlementToken.checkedTransferFromAndCallSender(buyer,seller,_paymentAmountPerUnit*_units,transactionHash); // trigger transfer upfrontPayment
        emit TradeConfirmed(msg.sender, Strings.toString(transactionHash));
    }

    function afterTransfer(uint256 transactionHash, bool success) external   {
        require(tradeStates[transactionHash] == TradeState.InTransfer, "No existing Transfer phase for Transaction Hash");
        if (success == true){ /* Transfer units to buyer */
            address addressBuyer = transactionSpecs[transactionHash].buyer;
            bondHolderBalances[address(this)]  -= transactionSpecs[transactionHash].lotNumber;
            bondHolderBalances[addressBuyer]   += transactionSpecs[transactionHash].lotNumber;
            if (transactionSpecs[transactionHash].seller == issuerAddress)  // if Seller is Issuer than update issuer balance
                bondIssuerBalance += transactionSpecs[transactionHash].lotNumber;
            tradeStates[transactionHash] = TradeState.Settled;
        }
        else{
            tradeStates[transactionHash] == TradeState.Terminated;
            address adressSeller         = transactionSpecs[transactionHash].seller;
            bondHolderBalances[adressSeller]   += transactionSpecs[transactionHash].lotNumber; /* transfer back the units */
            bondHolderBalances[address(this)]  -= transactionSpecs[transactionHash].lotNumber;
            delete transactionSpecs[transactionHash];
            emit TradeTerminated("Settlement Transfer failed - Trade ist terminated");
        }
    }

    /*
     * Settlement can be initiated when margin accounts are locked, a valuation request event is emitted containing tradeData and valuationViewParty
     * Changes Process State to Valuation&Settlement
     * can be called only when ProcessState = Rebalanced and TradeState = Active
     */
    function initiateSettlement() external onlyIssuer  {
        uint hash = 0;
        tradeStates[hash] = TradeState.Valuation;
    }

    /*
     * Performs a settelement only when processState is ValuationAndSettlement
     * Puts process state to "inTransfer"
     * Checks Settlement amount according to valuationViewParty: If SettlementAmount is > 0, valuationViewParty receives
     * can be called only when ProcessState = ValuationAndSettlement
     */

    function performSettlement(int256 settlementAmount, string memory _settlementData) onlyIssuer external  {
        /*split settlementAmount and transfer to all bond holders*/

        /* Question: Keep in mind that balances might be locked at that point since a transfer might be running - who is getting the payment ? */

        /* settlementAmount is defined in cents per mimimumLotSize - e.g. 1% of mimimumLotSize 1000 EUR => 10EUr = 1000 ct
        So for each address pay = settlementAmount * lotBalances[address] */

        /* Handle Case: Failure to pay if issuerAdress has less Balance then settlementAmount * sum (lotBalances) */

    }

    /*
     * Can be called by a party for mutual termination
     * Hash is generated an entry is put into pendingRequests
     * TerminationRequest is emitted
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function requestTradeTermination(string memory _tradeId, int256 _terminationPayment) external  onlyIssuer {
        /*require(keccak256(abi.encodePacked(tradeId)) == keccak256(abi.encodePacked(_tradeId)), "Trade ID mismatch");
        uint256 hash = uint256(keccak256(abi.encode(_tradeId, "terminate", terminationPayment)));
        pendingRequests[hash] = msg.sender;
        terminationPayment = _terminationPayment;
        emit TradeTerminationRequest(msg.sender, _tradeId);*/
    }

    /*

     * Same pattern as for initiation
     * confirming party generates same hash, looks into pendingRequests, if entry is found with correct address, tradeState is put to terminated
     * can be called only when ProcessState = Funded and TradeState = Active
     */
    function confirmTradeTermination(string memory _tradeId, int256 _terminationPayment) external  onlyIssuer {
        /*address pendingRequestParty = msg.sender == party1 ? party2 : party1;
        uint256 hashConfirm = uint256(keccak256(abi.encode(_tradeId, "terminate", terminationPayment)));
        require(pendingRequests[hashConfirm] == pendingRequestParty, "Confirmation of termination failed due to wrong party or missing request");
        delete pendingRequests[hashConfirm];
        mutuallyTerminated = true;
        emit TradeTerminationConfirmed(msg.sender, _tradeId);
        _emitSettlementRequest();*/
    }



    function getTradeId() public view returns (string memory) {
        return securityID;
    }


    function getTradeData() public view returns (string memory) {
        return securityData;
    }

    /**
     * Absolute value of an integer
     */
    function abs(int x) internal pure returns (int256) {
        return x >= 0 ? x : -x;
    }


}
