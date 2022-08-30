const { expect } = require("chai");
const mlog = require('mocha-logger');
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;


/*describe("SDC functionaly as ERC1155 Token", () => {

  const abiCoder = new AbiCoder();
  const fpml_data = "<fpml><trade>test-trade</trade></fpml>";
  let sdc_object;
  let tokenManager;
  let counterparty1;
  let counterparty2;
  let trade_id;


  before(async () => {
    const [_tokenManager, _counterparty1, _counterparty2] = await ethers.getSigners();
    tokenManager = _tokenManager;
    counterparty1 = _counterparty1;
    counterparty2 = _counterparty2;
    const SDCER20Factory = await ethers.getContractFactory("SDCLight");
    sdc_object = await SDCER20Factory.deploy("ID_123",counterparty1.address, counterparty2.address, tokenManager.address);
    await sdc_object.deployed();
    console.log("Token Manager Address: %s", tokenManager.address);
    console.log("SDC Address: %s", sdc_object.address);
  });

  it("Counterparty1 sends a deposit request", async () => {
    const request_call_cp1 =  sdc_object.connect(counterparty1).depositRequest(1000);
    await expect(request_call_cp1).to.emit(sdc_object, "DepositRequested").withArgs(counterparty1.address,1000);
  });

  it("Token Manager deposits 1000 to both cash account", async () => {
    await sdc_object.connect(tokenManager).deposit(counterparty1.address,1000);
    await sdc_object.connect(tokenManager).deposit(counterparty2.address,1000);
    expect(await sdc_object.balanceOf(counterparty1.address,sdc_object.CASH_BUFFER())).to.equal(1000);
    expect(await sdc_object.balanceOf(counterparty2.address,sdc_object.CASH_BUFFER())).to.equal(1000);
  });

  it("Counterparty1 incepts a trade", async () => {
    trade_id =  Keccak256(abiCoder.encode(["string"], [fpml_data]));
    const incept_call = await sdc_object.connect(counterparty1).inceptTrade(fpml_data, 20, 80);
    expect(incept_call).to.emit(sdc_object, "TradeIncepted");

  });

  it("Counterparty2 confirms a trade", async () => {
    const confirm_call = await sdc_object.connect(counterparty2).confirmTrade(trade_id);
    await expect(confirm_call).to.emit(sdc_object, "TradeConfirmed");
    expect(await sdc_object.balanceOf(counterparty1.address,sdc_object.CASH_BUFFER())).to.equal(900);
    expect(await sdc_object.balanceOf(counterparty2.address,sdc_object.CASH_BUFFER())).to.equal(900);
    expect(await sdc_object.balanceOf(counterparty1.address,sdc_object.MARGIN_BUFFER())).to.equal(80);
    expect(await sdc_object.balanceOf(counterparty1.address,sdc_object.TERMINATIONFEE())).to.equal(20);
    expect(await sdc_object.balanceOf(counterparty2.address,sdc_object.MARGIN_BUFFER())).to.equal(80);
    expect(await sdc_object.balanceOf(counterparty2.address,sdc_object.TERMINATIONFEE())).to.equal(20);
  });

  /*it("Settlement with 40 in favor to CP2 is performed", async () => {
    const settle_call = await sdc_object.connect(counterparty2).settle(40,counterparty2.address);
    await expect(settle_call).to.emit(sdc_object, "TradeSettlementSuccessful");
  });

  it("Settlement with 100 in favor to couterparty1 leads to termination", async () => {
    const settle_call2 = await sdc_object.connect(counterparty1).settle(100,counterparty1.address);
    await expect(settle_call2).to.emit(sdc_object, "TradeTerminated").withArgs(counterparty2.address);
  });

/*
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


});*/