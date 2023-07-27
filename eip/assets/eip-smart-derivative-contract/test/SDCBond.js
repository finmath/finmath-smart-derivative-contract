const { ethers } = require("hardhat");
const { expect } = require("chai");
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;

describe("Livecycle Unit-Tests for Smart Bond Contract", () => {

    // Define objects for TradeState enum, since solidity enums cannot provide their member names...
  const TradeState = {
      Inactive: 0,
      Incepted: 1,
      Confirmed: 2,
      Active: 3,
      Terminated: 4,
  };

  const abiCoder = new AbiCoder();
  let sdc_bond;
  let token;
  let issuer;
  let tokenManager;
  let subscriber;
  let buyer;
  let trade_id;
  let initialLiquidityBalance = 1000000;
  let bondData = "<bondData>";

  before(async () => {
    const [_tokenManager,_issuer, _subscriber, _buyer] = await ethers.getSigners();
    tokenManager = _tokenManager;
    issuer = _issuer;
    subscriber = _subscriber;
    buyer = _buyer;
    const ERC20Factory = await ethers.getContractFactory("SettlementToken");
    const SDCFactory = await ethers.getContractFactory("SDCBond");
    token = await ERC20Factory.deploy();

    /*
     address         _issuerAddress,
            string memory   _securityData,
            string memory   _securityID,
            uint            _initialLotBalance,
            uint            _minimumLotSize,
            address         _settlementToken
    */
    sdc_bond = await SDCFactory.deploy(issuer.address, bondData, "SEC_ID_123", 10000, 1000, token.address);
        await token.deployed();
        await sdc_bond.deployed();
    console.log("Bond Address: %s", sdc_bond.address);
  });

   it("Initial minting and approvals", async () => {
      await token.connect(tokenManager).setSDCAddress(sdc_bond.address);
      await token.connect(tokenManager).mint(issuer.address,initialLiquidityBalance);
      await token.connect(tokenManager).mint(subscriber.address,initialLiquidityBalance);
      await token.connect(tokenManager).mint(buyer.address,initialLiquidityBalance);
      await token.connect(buyer).approve(sdc_bond.address,initialLiquidityBalance);
      await token.connect(issuer).approve(sdc_bond.address,initialLiquidityBalance);
      await token.connect(subscriber).approve(sdc_bond.address,initialLiquidityBalance);
    });

   it("Subscriber subscribes to buy 1000 lots with price of 999 each", async () => {
        const incept_call = await sdc_bond.connect(subscriber).inceptTrade(issuer.address,bondData,1,1000,999);
        await expect(incept_call).to.emit(sdc_bond, "TradeIncepted");
   });
   it("Issuer confirms to sell 1000 lots to subscriber with price of 999 each", async () => {
        const confirm_call = await sdc_bond.connect(issuer).confirmTrade(subscriber.address,bondData,-1,1000,999);
        await expect(confirm_call).to.emit(sdc_bond, "TradeConfirmed");
        let balanceIssuer = await token.connect(issuer).balanceOf(issuer.address);
        let balanceSubscriber = await token.connect(subscriber).balanceOf(subscriber.address);
        await expect(balanceIssuer).equal(initialLiquidityBalance + 1000*999);
   });



});