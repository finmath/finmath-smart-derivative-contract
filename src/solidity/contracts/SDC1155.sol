// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC1155/IERC1155.sol";

/*  https://github.com/enjin/erc-1155/blob/master/contracts/IERC1155.sol
    https://github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/token/ERC1155/ERC1155.sol */

contract SDC1155 is IERC1155 {

    // const int ids for several token - i.e. account balance - types
    uint public constant TO_DEPOSIT         = 0;
    uint public constant TO_WITHDRAW        = 1;
    uint public constant CASH_BUFFER        = 2;
    uint public constant MARGIN_BUFFER      = 3;
    uint public constant TERMINATIONFEE     = 4;
    uint public constant VALUATIONFEE       = 5;

    enum TradeStatus{ INCEPTED, CONFIRMED, ACTIVE, TERMINATION_REQUESTED, TERMINATION_CONFIRMED, DEAD }

    string private sdc_id;
    // SDC need four addresses as token minting and burning is executed by a central authority and valuation provided by an external service
    address private counterparty1Address;
    address private counterparty2Address;
    address private tokenManagerAddress;
    address private valuationProviderAddress;

    // Specification of reference trades
    struct RefTradeSpec{
        uint fpml_index;
        address addressPayerSwap;       // Convention: PV calculation as seen from fixed rate payer = Fix - Float
        uint256 marginBuffer;           // Currently buffers and termination fee are assumed to be symmetric
        uint256 terminationFee;
        TradeStatus tradeStatus;
        address addressStatusUpdate;    // this is the adress which has initiated a certain trade status update which needs to be confirmed - i.e. INCEPTION or TERMINATION
    }

    // Hold all reference trades data
    mapping(bytes32 => RefTradeSpec) refTradeSpecs;
    string[] private   fpmlData;

    // Multi-Token Balance and operator approval map
    mapping(uint256 => mapping(address => uint256)) private balances;
    mapping(address => mapping(address => bool))    private operatorApprovals;

    // SDC Trade Events
    event TradeIncepted(address fromAddress, bytes32 id);
    event TradeConfirmed(address fromAddress, bytes32 id);
    event TradeActive(bytes32 id);
    event TradeTerminated(bytes32 id, address causingParty);
    event TradeSettlementSuccessful(bytes32 id);
    event ValuationRequest();
    event TerminationRequested(address fromAddress, bytes32 trade_id);
    event TerminationConfirmed(address fromAddress, bytes32 trade_id);

    // Transfer Events
    event DepositRequested();
    event WithdrawalRequested();

    // Modifiers to control access to external functions below
    modifier onlyCounterparty { 
        require(msg.sender == counterparty1Address || msg.sender == counterparty2Address, "Not authorised"); _;
    }

    modifier onlyTokenManager {
        require(msg.sender == tokenManagerAddress,"Not authorised");
        _;
    }

    modifier onlyValuationProvider {
        require(msg.sender == valuationProviderAddress, "Not authorised");
        _;
    }
    
    /*@notice: constructor */
    constructor(string memory _sdc_id, 
                address _counterparty1Adress, 
                address _counterparty2Adress, 
                address _tokenManagerAddress,
                address _valuationProviderAdress) {
      sdc_id = _sdc_id; 
      counterparty1Address  = _counterparty1Adress;
      counterparty2Address  = _counterparty2Adress;  
      tokenManagerAddress   = _tokenManagerAddress;
      valuationProviderAddress = _valuationProviderAdress;
    }
    
    ////// SECTION: EXTERNAL GETTER FUNCTIONS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    function    getTradeRef(bytes32 id)   external view returns (string memory fpml_data, address addressPayerSwap, TradeStatus status){
        uint data_index = refTradeSpecs[id].fpml_index;
        return (fpmlData[data_index], refTradeSpecs[id].addressPayerSwap, refTradeSpecs[id].tradeStatus);
    }

     /*@notice: SDC - Communication with Oracle - External function to return fpml array  */
    function getFPMLData() external onlyValuationProvider view  returns (string[] memory) {
        return fpmlData;
    }

    
    ////// SECTION: EXTERNAL SDC LIVE CYCLE FUNCTIONS (to be called from authorised addresses only) ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*@notice: External Function to Incept a Trade with FPML data and margin and buffer amounts */
    function inceptTrade(string memory fpml_data, address payerSwapAddress, uint256 terminationFee, uint256 marginBuffer) external onlyCounterparty returns(bool){ 
        bytes32 id = keccak256(abi.encode(fpml_data));
        fpmlData.push(fpml_data);
        uint index = fpmlData.length-1;
        refTradeSpecs[id] = RefTradeSpec(index,payerSwapAddress,terminationFee,marginBuffer,TradeStatus.INCEPTED,msg.sender);
        emit TradeIncepted(msg.sender,id);
        return true;
    }

    /*@notice: External Function to Confirm an incepted trade, triggers initial transfer of margin and termination fee */
    function confirmTrade(bytes32 trade_id) external onlyCounterparty  returns(bool isConfirmed){ 
        require(refTradeSpecs[trade_id].addressPayerSwap != address(0x0), "Trade not exists");
        require(refTradeSpecs[trade_id].addressStatusUpdate != msg.sender, "Trade-Inception cannot be confirmed by same address which has requested inception");
        emit TradeConfirmed(msg.sender,trade_id);
        _performTransfer(counterparty1Address, counterparty1Address, CASH_BUFFER , MARGIN_BUFFER, refTradeSpecs[trade_id].marginBuffer); 
        _performTransfer(counterparty1Address, counterparty1Address, CASH_BUFFER , TERMINATIONFEE, refTradeSpecs[trade_id].terminationFee); 
        _performTransfer(counterparty2Address, counterparty2Address, CASH_BUFFER , MARGIN_BUFFER, refTradeSpecs[trade_id].marginBuffer); 
        _performTransfer(counterparty2Address, counterparty2Address, CASH_BUFFER , TERMINATIONFEE, refTradeSpecs[trade_id].terminationFee); 
        refTradeSpecs[trade_id].tradeStatus = TradeStatus.ACTIVE;
        emit TradeActive(trade_id);
        isConfirmed = true;
        return isConfirmed;
    }

    /* @notice: SDC - External Function to trigger a settlement for all active trades only triggered by a counterparty */
    function requestSettlement() external onlyCounterparty { 
        emit ValuationRequest();
    }

    
    /*@notice: SDC - Externalö Function to trigger a settlement with already know settlement amounts called by e.g. an external oracle service */
    function settle(bytes32[] memory trade_ids, int[] memory _marginAmounts ) external onlyValuationProvider returns(bool)  { 
        for (uint i=0; i< trade_ids.length; i++){
            require(refTradeSpecs[trade_ids[i]].addressPayerSwap != address(0x0),"settle: trade id not defined");
            address creditor_address; 
            uint transferAmount = 0;
            if (_marginAmounts[i] < 0 ){ // This case Payer Swap has decreased in value
                creditor_address = refTradeSpecs[trade_ids[i]].addressPayerSwap == counterparty1Address ? counterparty2Address : counterparty1Address;
                transferAmount = uint(-1 * _marginAmounts[i]);
            }
            else{
                creditor_address = refTradeSpecs[trade_ids[i]].addressPayerSwap;
                transferAmount = uint( _marginAmounts[i]);
            }
             _performSettlementTransfer(trade_ids[i], transferAmount, creditor_address);
             if (refTradeSpecs[trade_ids[i]].tradeStatus == TradeStatus.TERMINATION_CONFIRMED)  /* Perform Termination for respective flags */
                _performMutualTermination(trade_ids[i]);
        }
        return true;
    }


    /*@notice: SDC - External function to request an early termination */
    function requestTradeTermination(bytes32 trade_id) external onlyCounterparty returns(bool){ 
        emit TerminationRequested(msg.sender,trade_id);
        refTradeSpecs[trade_id].tradeStatus = TradeStatus.TERMINATION_REQUESTED;
        refTradeSpecs[trade_id].addressStatusUpdate = msg.sender;
        return true;
    }

    /*@notice: SDC - External function to confirm an early termination: Termination will be executed after next settlement */
    function confirmTradeTermination(bytes32 trade_id) external onlyCounterparty returns(bool){ 
        require(refTradeSpecs[trade_id].addressStatusUpdate != msg.sender, "Trade-Termination cannot be confirmed by same address which has requested termination");
        refTradeSpecs[trade_id].tradeStatus = TradeStatus.TERMINATION_CONFIRMED;
        refTradeSpecs[trade_id].addressStatusUpdate = msg.sender;
        emit TerminationConfirmed(msg.sender,trade_id);
        return true;
    }

    ////// SECTION: SDC INTERNAL FUNCTIONALITY FOR SETTLEMENT AND TERMINATION /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /*@notice: SDC - Check Margin */
    function _marginCheck(address cpAddress, bytes32 trade_id) internal view returns(bool){
        if ( balances[CASH_BUFFER][cpAddress] >= refTradeSpecs[trade_id].marginBuffer )
            return true;
        else
            return false;
    }

    /*@notice: SDC - Check Settlement */
    function _settlementCheck(bytes32 trade_id, uint256 amount) internal view returns(bool){
        if ( refTradeSpecs[trade_id].marginBuffer >= amount) return true;
        else return false;
    }

     /*@notice: SDC - Internal function to perform a settlement transfer for specific trade id */
    function _performSettlementTransfer(bytes32 trade_id, uint256 settlement_amount, address address_of_creditor ) private returns(bool){
        require(settlement_amount > 0, "Settlement amount should be positive");
        require(address_of_creditor == counterparty1Address || address_of_creditor == counterparty2Address, "Creditor Address should be either CP1 oder CP");
        address address_of_debitor = address_of_creditor == counterparty1Address ? counterparty2Address : counterparty1Address;
        if ( _settlementCheck(trade_id,settlement_amount) ){
            if (_marginCheck(address_of_debitor, trade_id) ){
                _performTransfer(address_of_debitor, address_of_creditor, MARGIN_BUFFER, MARGIN_BUFFER, settlement_amount); // transfer of settlement amount
                _performTransfer(address_of_debitor, address_of_debitor, CASH_BUFFER, MARGIN_BUFFER, settlement_amount);     // autodebit
                _performTransfer(address_of_creditor, address_of_creditor, MARGIN_BUFFER, CASH_BUFFER, settlement_amount); // autocredit
                 emit TradeSettlementSuccessful(trade_id);
                return true;
            }
        }
       
        _performTermination(trade_id,address_of_debitor);
        emit TradeTerminated(trade_id, address_of_debitor);
        return false;
    }

    /*@notice: SDC - Internal function to perform termination */
    function _performTermination(bytes32 trade_id, address causing_party_address) internal returns(bool){
        address address_fee_receiver = causing_party_address == counterparty1Address ? counterparty2Address : counterparty1Address;
        _performTransfer(causing_party_address, address_fee_receiver, MARGIN_BUFFER, CASH_BUFFER,refTradeSpecs[trade_id].marginBuffer); // book max margin amount
        _performTransfer(causing_party_address, address_fee_receiver, TERMINATIONFEE, CASH_BUFFER, refTradeSpecs[trade_id].terminationFee); // book termination fee from causing party to cash of receiving party
        _performTransfer(causing_party_address, address_fee_receiver, TERMINATIONFEE, CASH_BUFFER, refTradeSpecs[trade_id].terminationFee); // transfer locked termination fee of receving party
        _performTransfer(address(this), counterparty1Address, MARGIN_BUFFER, CASH_BUFFER, refTradeSpecs[trade_id].marginBuffer); // release margin amounts to cash
        _performTransfer(address(this), counterparty2Address, MARGIN_BUFFER, CASH_BUFFER, refTradeSpecs[trade_id].marginBuffer); // release margin amounts to cash
        refTradeSpecs[trade_id].tradeStatus = TradeStatus.DEAD;
        return true;
    }

    function _performMutualTermination(bytes32 trade_id) internal returns(bool){
        _performTransfer(counterparty1Address, counterparty1Address, TERMINATIONFEE, CASH_BUFFER,refTradeSpecs[trade_id].terminationFee);
        _performTransfer(counterparty1Address, counterparty1Address, MARGIN_BUFFER, CASH_BUFFER,refTradeSpecs[trade_id].marginBuffer);
        _performTransfer(counterparty2Address, counterparty2Address, TERMINATIONFEE, CASH_BUFFER,refTradeSpecs[trade_id].terminationFee);
        _performTransfer(counterparty2Address, counterparty2Address, MARGIN_BUFFER, CASH_BUFFER,refTradeSpecs[trade_id].marginBuffer);
        refTradeSpecs[trade_id].tradeStatus = TradeStatus.DEAD;
        return true;       
    }

    
    ////// SECTION: TOKEN TRANSFER FUNCTIONALITY /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 

    /* @notice: Counterparty to trigger a deposit request to margin account*/
    function depositRequest(uint256 amount) external virtual onlyCounterparty {
        balances[TO_DEPOSIT][msg.sender] += amount;
        emit DepositRequested();
    }

    /* @notice: Counterparty to trigger a withdraw request to margin account*/
    function withdrawRequest(uint256 amount) external virtual onlyCounterparty {
        require(balances[CASH_BUFFER  ][msg.sender] >= amount, "withdrawRequest: Not sufficient balance!");
        balances[CASH_BUFFER  ][msg.sender] -= amount;
        balances[TO_WITHDRAW][msg.sender] += amount;
        emit WithdrawalRequested();
    }

    /* @notice: Function for token manager to manage deposit and withdrawal requests and allocate according liquidity*/
    function allocateLiquidity() external virtual onlyTokenManager {
        balances[TO_WITHDRAW  ][counterparty1Address] = 0;  // burn
        balances[TO_WITHDRAW  ][counterparty2Address] = 0;  // burn
        uint amountToDepositCP1 = balances[TO_DEPOSIT  ][counterparty1Address];
        uint amountToDepositCP2 = balances[TO_DEPOSIT  ][counterparty2Address];
        _performTransfer(counterparty1Address, counterparty1Address, TO_DEPOSIT, CASH_BUFFER,amountToDepositCP1); // allocate to buffer
        _performTransfer(counterparty2Address, counterparty2Address, TO_DEPOSIT, CASH_BUFFER,amountToDepositCP2); // allocate to buffer
    }

    /*@notice: Internal function to perform a cross token transfer */
    function _performTransfer(address from, address to, uint256 id_from, uint256 id_to, uint256 amount) internal {
        require(to != address(0x0), "_transfer: transfer to the zero address");
        uint256 fromBalance = balances[id_from][from];
        require(fromBalance >= amount, "_transfer: insufficient balance for transfer");
        balances[id_from][from] = fromBalance - amount;
        balances[id_to][to] += amount;
    }

    /*@notice: Internal function to perform a cross token batch transfer */
    function _performBatchTransfer(address from, address to, uint256[] memory ids_from, uint256[] memory ids_to, uint256[] memory amounts) internal {
        require(ids_from.length == amounts.length, "_batchCrossTransfer: ids and amounts length mismatch");
        require(ids_from.length == ids_to.length, "_batchCrossTransfer: ids_to and ids_from length mismatch");
        require(to != address(0), "safeBatchTransferFrom: transfer to the zero address");
        for (uint256 i = 0; i < ids_from.length; ++i) {
            uint256 id_from = ids_from[i];
            uint256 id_to = ids_from[i];
            uint256 amount = amounts[i];
            uint256 fromBalance = balances[id_from][from];
            require(fromBalance >= amount, "_batchTransfer: insufficient balance for transfer");
            unchecked {
                balances[id_from][from] = fromBalance - amount;
            }
            balances[id_to][to] += amount;
        }

    }

    ////// SECTION: IERC1155 INTERFACE IMPLEMENTATIONS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
    /*@notice: IERC1155-Impl - Get the balance of an account's Tokens. */
    function balanceOf(address account, uint _id)  external view override returns (uint256){
        require(_id <= 5, "balanceOf: only five token types defined!");
        require(account != address(0x0), "balanceOf: balance query for the zero address");
        return balances[_id][account];
    }

    /*@notice: IERC1155-Impl - Get the balance of multiple account/token pairs*/
    function balanceOfBatch(address[] memory accounts, uint[] memory ids) external view override returns (uint256[] memory){
        require(accounts.length == ids.length, "balanceOfBatch: accounts and ids length mismatch");
        uint256[] memory batchBalances = new uint256[](accounts.length);
        for (uint256 i = 0; i < accounts.length; ++i) {
            batchBalances[i] = this.balanceOf(accounts[i], ids[i]);
        }
        return batchBalances;
    }

    /*  @notice: IERC1155-Impl - Transfers `_value` amount of an `_id` from the `_from` address to the `_to` address specified (with safety call). */
    function safeTransferFrom(address from, address to, uint256 id, uint256 amount, bytes memory data) external override{
        require(msg.sender==address(this),"Not authorised");  // cannot be called from other external address
        _performTransfer(from,to,id,id,amount);

    }

    /* @notice: IERC1155-Impl - Transfers `_values` amount(s) of `_ids` from the `_from` address to the `_to` address specified (with safety call). */
    function safeBatchTransferFrom(address from, address to, uint256[] calldata ids, uint256[] calldata amounts, bytes memory data) external override{
        require(msg.sender==address(this),"Not authorised");  // cannot be called from other external address
        _performBatchTransfer(from,to,ids,ids,amounts);

    }
    
    /*@notice: IERC1155-Impl - Enable or disable approval for a third party ("operator") to manage all of the caller's tokens.*/
    function setApprovalForAll(address operator, bool isApproved) external override {
        require(operator != address(0x0), "setApprovalForAll: operator is zero address");
        address owner = msg.sender;
        operatorApprovals[owner][operator] = isApproved;
        emit ApprovalForAll(owner, operator, isApproved);
    }

    /*@notice: IERC1155-Impl - Queries the approval status of an operator for a given owner.*/
    function isApprovedForAll(address owner, address operator) external view override returns (bool)  {
        require(owner != address(0x0), "isApprovedForAll: owner is zero address");
        require(operator != address(0x0), "isApprovedForAll: operator is zero address");
        return operatorApprovals[owner][operator];
    }

    
    /*@notice: Some support interface implementation..to be explored further*/
    function supportsInterface(bytes4 interfaceId) public view virtual override returns (bool) {
        return interfaceId == type(IERC1155).interfaceId;
    }
    
}