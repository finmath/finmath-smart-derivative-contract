// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.8.0 <0.9.0;

import "./SDC.sol";
import "./SettlementToken.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/introspection/IERC165.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

contract SDCBond is IERC165  {


    event TradeIncepted(address initiator, string tradeId, string tradeData);

    event TradeConfirmed(address confirmer, string tradeId);

    event TradeActivated(string tradeId);

    event TradeTerminated(string cause);

    event ProcessSettlementPhase();

    event ProcessSettled();

    event ProcessSettlementRequest(string tradeData, string lastSettlementData);

    event TradeTerminationRequest(address cpAddress, string tradeId);

    event TradeTerminationConfirmed(address cpAddress, string tradeId);

    event ProcessHalted(string message);

    modifier onlyIssuer() {
        require(msg.sender == issuerAddress , "You are not issuer"); _;
    }


    /*
     * Trade States
     */
    enum TradeState {
        /*
         * Incepted: Trade data submitted by one party. Market data for initial valuation is set.
         */
        Incepted,
        /*
         * Confirmed: Trade data accepted by other party.
         */
        Confirmed,
        /*
         * Trade is settled
         */
        Settled,
        /*
         * @dev The Valuation Phase
         */
        Valuation,
        /*
         * @dev The settlement phase is initiated.
         */
        inTransfer,
        /*
         * Terminated.
         */
        Terminated
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
    uint bondIssuerBalance;

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
    function inceptTrade(address withParty, string memory tradeData, int position, uint256 lotNumber, uint256 paymentAmountPerLot) external   {
        // @TODO require(msg.sender != withParty,)
        // @TODO Check eligiblity of the msg.sender
        uint256 transactionHash = uint256(keccak256(abi.encode(msg.sender,withParty,tradeData, position, lotNumber, paymentAmountPerLot)));
        require(keccak256(abi.encode(tradeData)) == keccak256(abi.encode(securityData)), "Trade Inception request does not meet contract's underlying security specification");
        require(pendingInceptions[transactionHash] != msg.sender, "Same inception request cannot be openend twice");
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
    function confirmTrade(address withParty, string memory tradeData, int position, uint256 lotNumber, uint256 paymentAmountPerLot) external  {
        //require(msg.sender != withParty,)
        // @TODO Check eligiblity of the msg.sender
        uint256 transactionHash = uint256(keccak256(abi.encode(withParty,msg.sender,tradeData,-position, lotNumber, paymentAmountPerLot)));
        require(pendingInceptions[transactionHash] == withParty, "No open request available to be confirmed");
        delete pendingInceptions[transactionHash];
        address buyer = position > 0 ? msg.sender : withParty;  // payer buys the bonds and pays cash
        address seller = position > 0 ? withParty : msg.sender; // seller sells the bonds and receives cash
        require(bondHolderBalances[seller] >= lotNumber, "Lot balance of selling party not sufficient");
        uint256 paymentAmount = uint256(paymentAmountPerLot) * lotNumber;
        transactionSpecs[transactionHash] = TransactionSpec(buyer,seller,lotNumber,paymentAmount,block.timestamp);
        /*Transfer Bond to internal balance and trigger transfer of the paymentAmount*/
        bondHolderBalances[seller]         -= lotNumber;
        bondHolderBalances[address(this)]  += lotNumber;
        emit TradeConfirmed(msg.sender, Strings.toString(transactionHash));
        tradeStates[transactionHash] = TradeState.inTransfer;
        settlementToken.checkedTransferFromAndCallSender(buyer,seller,paymentAmount,transactionHash); // trigger transfer upfrontPayment
    }

    function afterSettlement(uint256 transactionHash, bool success) external   {
        require(tradeStates[transactionHash] == TradeState.inTransfer, "No existing Transfer phase for Transaction Hash");
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
