const { ethers } = require("hardhat");
const { expect } = require("chai");
const AbiCoder = ethers.utils.AbiCoder;
const Keccak256 = ethers.utils.keccak256;
const crypto = require('crypto');
const EthCrypto = require('eth-crypto')

describe("Livecycle Unit-Tests for Delivery-vs-Payment", () => {
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

  before(async () => {
  //https://www.geeksforgeeks.org/node-js-crypto-publicencrypt-method/

    console.log("PublicKey of DvP Oracle: %s",dvpOracle.publicKey);

    const [_buyer, _seller] = await ethers.getSigners();
    buyer = _buyer;
    seller = _seller;

    const deliveryContractFactory = await ethers.getContractFactory("AssetContract");
    const paymentContractFactory = await ethers.getContractFactory("PaymentContract");
    deliveryContract = await deliveryContractFactory.deploy();
    paymentContract = await paymentContractFactory.deploy(buyer.address,seller.address);
    await deliveryContract.deployed();
    await paymentContract.deployed();
    console.log("DeliveryContract Address: %s", deliveryContract.address);
    console.log("PaymentContract Address: %s", paymentContract.address);
  });

  it("Incept Trade from Seller", async () => {
     let hashedKeySeller = ethers.utils.solidityKeccak256(["string"],["keySeller"]);
     console.log("KeyHashedSeller: %s",hashedKeySeller);
     const call = await deliveryContract.connect(seller).inceptTrade(buyer.address, trade_data,-1000, 998, hashedKeySeller);
     const res = await call.wait();

     id = res.events[0].args[0];
     console.log("TransactionHash:", id);
     await expect(call).to.emit(deliveryContract, "TradeIncepted").withArgs(id,buyer.address);

  });

  it("Confirm Trade from Buyer", async () => {
       let hashedKeyBuyer = ethers.utils.solidityKeccak256(["string"],["keyBuyer"]);
       console.log("KeyHashedBuyer: %s",hashedKeyBuyer);
       const call = await deliveryContract.connect(buyer).confirmTrade(seller.address, trade_data, 1000, 998, hashedKeyBuyer);

       await expect(call).to.emit(deliveryContract, "TradeConfirmed");
  });


 it("Incept Transfer from Buyer", async () => {
     let keyEncryptedBuyer = await EthCrypto.encryptWithPublicKey(dvpOracle.publicKey,"keyBuyer");
     let keyEncrypedAsString = await EthCrypto.cipher.stringify(keyEncryptedBuyer);
     console.log("KeyEncryptedSeller: %s",keyEncrypedAsString);
     const call = await deliveryContract.connect(buyer).inceptTransfer(id, assetAmount, seller.address, keyEncrypedAsString) ;
     await expect(call).to.emit(deliveryContract, "AssetTransferIncepted");
  });


});