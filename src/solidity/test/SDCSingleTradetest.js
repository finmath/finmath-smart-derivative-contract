const { expect } = require("chai");
const mlog = require('mocha-logger');
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;


describe("SDC functionaly as ERC1155 Token", () => {

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
    const SDCER20Factory = await ethers.getContractFactory("SDCSingleTrade");
    sdc_object = await SDCER20Factory.deploy(counterparty1.address, counterparty2.address);
    await sdc_object.deployed();
    console.log("SDC Address: %s", sdc_object.address);
  });

  it("Counterparty1 incepts a trade", async () => {
     const incept_call = await sdc_object.connect(counterparty1).requestTradeInitiation(fpml_data, counterparty1.address, 1000, 100);
     console.log("TradeId: %s", await sdc_object.callStatic.getTradeStatus());
     expect(incept_call).to.emit(sdc_object, "TradeIncepted");
   });

   it("Counterparty1 confirms a trade", async () => {
        const incept_call = await sdc_object.connect(counterparty2).confirmTradeInitiation(fpml_data, counterparty1.address, 1000, 100);
        console.log("TradeId: %s", await sdc_object.callStatic.getTradeStatus());
        expect(incept_call).to.emit(sdc_object, "TradeConfirmed");
      });

});