const { ethers } = require("hardhat");
const { expect } = require("chai");
const mlog = require('mocha-logger');
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;

describe("Livecycle Unit-Tests for Smart Derivative Contract", () => {

    // Define objects for TradeState enum, since solidity enums cannot provide their member names...
  const TradeState = {
      Inactive: 0,
      Incepted: 1,
      Confirmed: 2,
      Active: 3,
      Terminated: 4,
  };

  const abiCoder = new AbiCoder();
  const trade_data = "<xml>here are the trade specification</xml";
  let sdc;
  let token;
  let tokenManager;
  let counterparty1;
  let counterparty2;
  let trade_id;
  let initialLiquidityBalance = 5000;
  let terminationFee = 100;
  let marginBufferAmount = 900;


  before(async () => {
    const [_tokenManager, _counterparty1, _counterparty2] = await ethers.getSigners();
    tokenManager = _tokenManager;
    counterparty1 = _counterparty1;
    counterparty2 = _counterparty2;
    const ERC20Factory = await ethers.getContractFactory("SDCToken");
    const SDCFactory = await ethers.getContractFactory("SDC");
    token = await ERC20Factory.deploy();
    await token.deployed();
    sdc = await SDCFactory.deploy(counterparty1.address, counterparty2.address,counterparty1.address, token.address,marginBufferAmount,terminationFee);
    await sdc.deployed();
    console.log("SDC Address: %s", sdc.address);
  });

  it("Initial minting and approvals for SDC", async () => {
    await token.connect(counterparty1).mint(counterparty1.address,initialLiquidityBalance);
    await token.connect(counterparty2).mint(counterparty2.address,initialLiquidityBalance);
    await token.connect(counterparty1).approve(sdc.address,terminationFee+marginBufferAmount);
    await token.connect(counterparty2).approve(sdc.address,terminationFee+marginBufferAmount);
    let allowanceSDCParty1 = await token.connect(counterparty1).allowance(counterparty1.address, sdc.address);
    let allowanceSDCParty2 = await token.connect(counterparty2).allowance(counterparty2.address, sdc.address);
    await expect(allowanceSDCParty1).equal(terminationFee+marginBufferAmount);
  });

  it("Counterparty1 incepts a trade", async () => {
     const incept_call = await sdc.connect(counterparty1).inceptTrade(trade_data,"initialMarketData");
     let tradeid =  await sdc.connect(counterparty1).getTradeID();
     //console.log("TradeId: %s", tradeid);
     await expect(incept_call).to.emit(sdc, "TradeIncepted").withArgs(counterparty1.address,tradeid,trade_data);
     let trade_state =  await sdc.connect(counterparty1).getTradeState();
     await expect(trade_state).equal(TradeState.Incepted);
   });


  it("Counterparty2 confirms a trade", async () => {
     const confirm_call = await sdc.connect(counterparty2).confirmTrade(trade_data,"initialMarketData");
     //console.log("TradeId: %s", await sdc.callStatic.getTradeState());
     let balanceSDC = await token.connect(counterparty2).balanceOf(sdc.address);
     await expect(confirm_call).to.emit(sdc, "TradeConfirmed");
     await expect(balanceSDC).equal(2*terminationFee);
     let trade_state =  await sdc.connect(counterparty1).getTradeState();
     await expect(trade_state).equal(TradeState.Active);
   });

   it("Processing prefunding phase", async () => {
     const call = await sdc.connect(counterparty2).initiatePrefunding();
     let balanceSDC = await token.connect(counterparty2).balanceOf(sdc.address);
     let balanceCP2 = await token.connect(counterparty2).balanceOf(counterparty2.address);
     await expect(balanceSDC).equal(2*terminationFee+2*marginBufferAmount);
     await expect(balanceCP2).equal(initialLiquidityBalance-(terminationFee+marginBufferAmount));
     await expect(call).to.emit(sdc, "ProcessFunded");
   });

});

/**


  it("Counterparty1 and 2 send deposit request", async () => {
    const request_call_cp1 =  sdc1155.connect(counterparty1).depositRequest(amount1);
    const request_call_cp2 =  sdc1155.connect(counterparty2).depositRequest(amount2);
    await expect(request_call_cp1).to.emit(sdc1155, "DepositRequested").withArgs(counterparty1.address,amount1);
    await expect(request_call_cp2).to.emit(sdc1155, "DepositRequested").withArgs(counterparty2.address,amount2);
  });
  it("Token Manager deposits to cash account", async () => {
    await sdc1155.connect(tokenManager).deposit(counterparty1.address,amount1);
    await sdc1155.connect(tokenManager).deposit(counterparty2.address,amount2);
    expect(await sdc1155.balanceOf(counterparty1.address,sdc1155.CASH_BUFFER())).to.equal(amount1);
    expect(await sdc1155.balanceOf(counterparty2.address,sdc1155.CASH_BUFFER())).to.equal(amount2);
  });
  it("Counterparty1 incepts a trade with payer party Counterparty2", async () => {
    trade_id =  Keccak256(abiCoder.encode(["string"], [fpml_data]));
    const incept_call = await sdc1155.connect(counterparty1).inceptTrade(fpml_data, counterparty2.address, 200, 50);
    expect(incept_call).to.emit(sdc1155, "TradeIncepted");
    const {0: fpml_ret, 1: address_ret, 2: status}  = await sdc1155.getTradeRef(trade_id);
    expect(status).to.equal(0);
    expect(fpml_ret).to.equal(fpml_data);
    expect(address_ret).to.equal(counterparty2.address);
  });
  it("Counterparty1 confirms a trade", async () => {
    const confirm_call = await sdc1155.connect(counterparty2).confirmTrade(trade_id);
    await expect(confirm_call).to.emit(sdc1155, "TradeConfirmed");
    const {0: fpml_ret, 1: address_ret, 2: status}  = await sdc1155.getTradeRef(trade_id);
    expect(fpml_ret).to.equal(fpml_data);
    expect(status).to.equal(2);
    expect(await sdc1155.balanceOf(counterparty1.address,sdc1155.CASH_BUFFER())).to.equal(amount1-250);
    expect(await sdc1155.balanceOf(counterparty2.address,sdc1155.CASH_BUFFER())).to.equal(amount2-250);
    expect(await sdc1155.balanceOf(counterparty1.address,sdc1155.MARGIN_BUFFER())).to.equal(200);
    expect(await sdc1155.balanceOf(counterparty1.address,sdc1155.TERMINATIONFEE())).to.equal(50);
    expect(await sdc1155.balanceOf(counterparty2.address,sdc1155.MARGIN_BUFFER())).to.equal(200);
    expect(await sdc1155.balanceOf(counterparty2.address,sdc1155.TERMINATIONFEE())).to.equal(50);
  });
  it ("Valuation Request is emitted", async () => {
    const settle_call = await sdc1155.connect(counterparty1).requestSettlement();
    await expect(settle_call).to.emit(sdc1155, "ValuationRequested");
  });
  it ("Valuation Oracle Node calls settlement", async () => {
    let id_array = [];
    id_array.push(trade_id);
    let margin_array = [];
    margin_array.push(marginAmount);
    const settle_call = await sdc1155.connect(valuationProvider).settle(id_array,margin_array);
    await expect(settle_call).to.emit(sdc1155, "TradeSettlementSuccessful");
    expect(await sdc1155.balanceOf(counterparty1.address,sdc1155.CASH_BUFFER())).to.equal(amount1-250-marginAmount);
    expect(await sdc1155.balanceOf(counterparty2.address,sdc1155.CASH_BUFFER())).to.equal(amount2-250+marginAmount);
  });
*/