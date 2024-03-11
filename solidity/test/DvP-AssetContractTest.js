const { ethers } = require("hardhat");
const { expect } = require("chai");
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;
const crypto = require('crypto');
const EthCrypto = require('eth-crypto')

describe("Test of DvP Asset Contract", () => {
    const abiCoder = new AbiCoder();
    const trade_data = "<xml>here are the trade specification</xml";
    const dvpOracle = EthCrypto.createIdentity();

    let sdc;
    let deliveryContract;
    let paymentContract;
    let buyer;
    let seller;
    let id;
    let assetAmount = 10000;
    let paymentAmount = 9000;
    let secWait = 4000;

    before(async () => {
        //https://www.geeksforgeeks.org/node-js-crypto-publicencrypt-method
        const [_buyer, _seller] = await ethers.getSigners();
        buyer = _buyer;
        seller = _seller;

        const deliveryContractFactory = await ethers.getContractFactory("AssetContract");
        const paymentContractFactory = await ethers.getContractFactory("PaymentContract");
        deliveryContract = await deliveryContractFactory.deploy();
        paymentContract = await paymentContractFactory.deploy(buyer.address,seller.address);
        await deliveryContract.deployed();
        await paymentContract.deployed();
    });

    it("should fail if trade spec are inconsistent", async () => {
        let hashedKeySeller = ethers.utils.solidityKeccak256(["string"],["keySeller"]);
        const call = await deliveryContract.connect(seller).inceptTrade(buyer.address, trade_data,-1000, 998, hashedKeySeller);
        const res = await call.wait();
        id = res.events[0].args[0];
        await expect(call).to.emit(deliveryContract, "TradeIncepted").withArgs(id,buyer.address);
        let hashedKeyBuyer = ethers.utils.solidityKeccak256(["string"],["keyBuyer"]);
        const call2 = deliveryContract.connect(buyer).confirmTrade(seller.address, trade_data, 21000, 998, hashedKeyBuyer);
        await expect(call2).to.be.revertedWith("No pending inception available to be confirmed for this trade specification");
    });
});