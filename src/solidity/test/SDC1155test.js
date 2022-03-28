const { expect } = require("chai");
const mlog = require('mocha-logger');
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;

/*describe("SDC functionaly as ERC1155 Token", () => {

  const abiCoder = new AbiCoder();
  const fpml_data = "<fpml><trade>test-trade</trade></fpml>";
  let sdc1155;
  let tokenManager;
  let counterparty1;
  let counterparty2;
  let valuationProvider;
  let trade_id;
  let amount1 = 1000;
  let amount2 = 2000;
  let marginAmount = 10;

  before(async () => {
    const [_tokenManager, _counterparty1, _counterparty2, _valuationProvider] = await ethers.getSigners();
    tokenManager = _tokenManager;
    counterparty1 = _counterparty1;
    counterparty2 = _counterparty2;
    valuationProvider = _valuationProvider;
    const SDCER20Factory = await ethers.getContractFactory("SDCLight");
    sdc1155 = await SDCER20Factory.deploy("SDCToken",counterparty1.address, counterparty2.address, tokenManager.address, valuationProvider.address);
    await sdc1155.deployed();
  });

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


});*/