// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;


contract SDC {

    ////// 1. Section: INVOLVED PARTIES: SDC need threee addresses as token minting and burning is executed by a central authority
    address private counterparty1Address;
    address private counterparty2Address;
    address private tokenManagerAddress;

    // Modifiers to control access to external functions below
    modifier onlyCounterparty { 
        require(msg.sender == counterparty1Address || msg.sender == counterparty2Address, "Not authorised"); _;
    }

    modifier onlyTokenManager {
        require(msg.sender == tokenManagerAddress,"Not authorised");
        _;
    }

    
    constructor(string memory _sdc_id, 
                address _counterparty1Adress, 
                address _counterparty2Adress, 
                address _tokenManagerAddress
                ) {
      sdc_id = _sdc_id; 
      counterparty1Address  = _counterparty1Adress;
      counterparty2Address  = _counterparty2Adress;  
      tokenManagerAddress   = _tokenManagerAddress;
    }

    ////// 2. SECTION: TOKEN TRANSFER FUNCTIONALITY /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Multi-Token Balance and operator approval map
    mapping(uint256 => mapping(address => uint256)) private balances;
   
    uint public constant CASH_BUFFER        = 1;
    uint public constant MARGIN_BUFFER      = 2;
    uint public constant TERMINATIONFEE     = 3;
    uint public constant VALUATIONFEE       = 4;

     // Transfer Events
    event DepositRequested(address cp, uint amount);
    event WithdrawalRequested(address cp, uint amount);
    
    function balanceOf(address account, uint _id)  external view returns (uint256){    /*@notice: IERC1155-Impl - Get the balance of an account's Tokens. */
        require(_id <= 5, "balanceOf: only five token types defined!");
        require(account != address(0x0), "balanceOf: balance query for the zero address");
        return balances[_id][account];
    }

    function performTransfer(address from, address to, uint256 id_from, uint256 id_to, uint256 amount) internal {    /*@notice: Internal function to perform a cross token transfer */
        require(to != address(0x0), "_transfer: transfer to the zero address");
        uint256 fromBalance = balances[id_from][from];
        require(fromBalance >= amount, "_transfer: insufficient balance for transfer");
        balances[id_from][from] = fromBalance - amount;
        balances[id_to][to] += amount;
    }

    function depositRequest(uint256 amount) external virtual onlyCounterparty { /* @notice: Counterparty to trigger a deposit request to margin account*/
        emit DepositRequested(msg.sender, amount);
    }

    function withdrawRequest(uint256 amount) external virtual onlyCounterparty {     /* @notice: Counterparty to trigger a withdraw request to margin account*/
        require(balances[CASH_BUFFER  ][msg.sender] >= amount, "withdrawRequest: Not sufficient balance!");
        emit WithdrawalRequested(msg.sender, amount);
    }

    function deposit(address cpAdress, uint256 amount) external virtual onlyTokenManager{
        require(cpAdress == counterparty1Address || cpAdress == counterparty2Address, "Address not known");
        balances[CASH_BUFFER][cpAdress] += amount;
    }

    function withdrawal(address cpAdress, uint256 amount) external virtual onlyTokenManager{
        require(cpAdress == counterparty1Address || cpAdress == counterparty2Address, "Address not known");
        require(balances[CASH_BUFFER  ][cpAdress] >= amount, "withdrawRequest: Not sufficient balance!");
        balances[CASH_BUFFER][cpAdress] -= amount;
    }


    ////// 3. SECTION: TRADE LIVE CYCLE FUNCTIONALITY /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum TradeStatus{ INCEPTED, CONFIRMED, ACTIVE, TERMINATION_REQUESTED, TERMINATION_CONFIRMED, DEAD }
    
    // SDC Live Cycle Events
    event TradeIncepted(address fromAddress);
    event TradeConfirmed(address fromAddress);
    event TradeActive();
    event TradeTerminated(address causingParty);
    event TradeSettlementSuccessful();
    event ValuationRequested(string fpml_data );
    event TerminationRequested(address fromAddress);
    event TerminationConfirmed(address fromAddress);

    // Data
    TradeStatus tradeStatus;
    string private sdc_id;
    uint private marginBuffer;
    uint private terminationFee;
    string fpmlData;
    
    function inceptTrade(string memory _fpml_data, uint256 _terminationFee, uint256 _marginBuffer) external onlyCounterparty returns(bool){   /*@notice: External Function to Incept a Trade with FPML data and margin and buffer amounts */
        fpmlData = _fpml_data;
        terminationFee = _terminationFee;
        marginBuffer = _marginBuffer;
        emit TradeIncepted(msg.sender);
        return true;
    }

    function confirmTrade(bytes32 trade_id) external onlyCounterparty  returns(bool isConfirmed){   /*@notice: External Function to Confirm an incepted trade, triggers initial transfer of margin and termination fee */
       emit TradeConfirmed(msg.sender);
        performTransfer(counterparty1Address, counterparty1Address, CASH_BUFFER , MARGIN_BUFFER, marginBuffer); 
        performTransfer(counterparty1Address, counterparty1Address, CASH_BUFFER , TERMINATIONFEE, terminationFee); 
        performTransfer(counterparty2Address, counterparty2Address, CASH_BUFFER , MARGIN_BUFFER, marginBuffer); 
        performTransfer(counterparty2Address, counterparty2Address, CASH_BUFFER , TERMINATIONFEE, terminationFee); 
        tradeStatus = TradeStatus.ACTIVE;
        emit TradeActive();
        isConfirmed = true;
        return isConfirmed;
    }

    function marginCheck(address cpAddress) internal view returns(bool){  /*@notice: SDC - Check Margin */
        return (balances[CASH_BUFFER][cpAddress] >= marginBuffer);
    }

    function settlementCheck(uint256 amount) internal view returns(bool){  /*@notice: SDC - Check Settlement */
        return (marginBuffer >= amount);
    }

    function requestSettlement() external  {     /* @notice: SDC - External Function to trigger a settlement for all active trades */
        emit ValuationRequested(fpmlData);
    }
   
    function settle(uint256 settlement_amount, address address_of_creditor) external returns(bool)  {        /*@notice: SDC - External Function to trigger a settlement with already know settlement amounts called by e.g. an external oracle service */
        require(settlement_amount > 0, "Settlement amount should be positive");
        require(address_of_creditor == counterparty1Address || address_of_creditor == counterparty2Address, "Creditor Address should be either CP1 oder CP");
        address address_of_debitor = address_of_creditor == counterparty1Address ? counterparty2Address : counterparty1Address;
        if ( settlementCheck(settlement_amount) ){
            if (marginCheck(address_of_debitor) ){
                performTransfer(address_of_debitor, address_of_creditor, MARGIN_BUFFER, MARGIN_BUFFER, settlement_amount); // transfer of settlement amount
                performTransfer(address_of_debitor, address_of_debitor, CASH_BUFFER, MARGIN_BUFFER, settlement_amount);     // autodebit
                performTransfer(address_of_creditor, address_of_creditor, MARGIN_BUFFER, CASH_BUFFER, settlement_amount); // autocredit
                 emit TradeSettlementSuccessful();
                return true;
            }
        }
        performTermination(address_of_debitor);
        emit TradeTerminated(address_of_debitor);
    }
    
    function performTermination( address causing_party_address) internal returns(bool){     /*@notice: SDC - Internal function to perform termination */
        address address_fee_receiver = causing_party_address == counterparty1Address ? counterparty2Address : counterparty1Address;
        performTransfer(causing_party_address, address_fee_receiver, MARGIN_BUFFER, CASH_BUFFER,marginBuffer); // book max margin amount
        performTransfer(causing_party_address, address_fee_receiver, TERMINATIONFEE, CASH_BUFFER,terminationFee); // book termination fee from causing party to cash of receiving party
        performTransfer(causing_party_address, address_fee_receiver, TERMINATIONFEE, CASH_BUFFER, terminationFee); // transfer locked termination fee of receving party
        performTransfer(address(this), counterparty1Address, MARGIN_BUFFER, CASH_BUFFER, marginBuffer); // release margin amounts to cash
        performTransfer(address(this), counterparty2Address, MARGIN_BUFFER, CASH_BUFFER, marginBuffer); // release margin amounts to cash
        tradeStatus = TradeStatus.DEAD;
        return true;
    }
 
}