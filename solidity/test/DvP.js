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
  let secWait = 4000;

  before(async () => {
  //https://www.geeksforgeeks.org/node-js-crypto-publicencrypt-method
    console.log("====================================================================================================");
    console.log("PublicKey of DvP Oracle is: %s",dvpOracle.publicKey);
    console.log("====================================================================================================");
    await new Promise((resolve) => setTimeout(resolve, secWait));
    const [_buyer, _seller] = await ethers.getSigners();
    buyer = _buyer;
    seller = _seller;

    const deliveryContractFactory = await ethers.getContractFactory("AssetContract");
    const paymentContractFactory = await ethers.getContractFactory("PaymentContract");
    deliveryContract = await deliveryContractFactory.deploy();
    paymentContract = await paymentContractFactory.deploy(buyer.address,seller.address);
    await deliveryContract.deployed();
    await paymentContract.deployed();
    console.log("AssetContract is now deployed at Address: %s", deliveryContract.address);
    console.log("PaymentContract is now deployed at Address: %s", paymentContract.address);
    console.log("====================================================================================================");
    await new Promise((resolve) => setTimeout(resolve, secWait));
  });

  it("Incept Trade from Seller", async () => {
     console.log("1. Incept a Trade by Security Seller");
     await new Promise((resolve) => setTimeout(resolve, secWait));
     console.log("====================================================================================================");
     console.log("- At first Seller generates a hashed key for the Buyer with solidity keccak256");
     await new Promise((resolve) => setTimeout(resolve, secWait));
     let hashedKeySeller = ethers.utils.solidityKeccak256(["string"],["keySeller"]);
     console.log("- Hashed Key for Buyer is: %s", hashedKeySeller);
     await new Promise((resolve) => setTimeout(resolve, secWait));
     console.log("- Seller now calls 'inceptTrade' against the AssetContract providing trade data and hashed key for seller");
     await new Promise((resolve) => setTimeout(resolve, secWait));
     const call = await deliveryContract.connect(seller).inceptTrade(buyer.address, trade_data,-1000, 998, hashedKeySeller);
     const res = await call.wait();
     id = res.events[0].args[0];
     console.log("- The Contract emits a unique Transaction ID:", id);
     await expect(call).to.emit(deliveryContract, "TradeIncepted").withArgs(id,buyer.address);

     await new Promise((resolve) => setTimeout(resolve, secWait));
     console.log("- Trade is now 'incepted' and hashed key for buyer is stored.");
     console.log("====================================================================================================");
  });

  it("Confirm Trade from Buyer", async () => {
      await new Promise((resolve) => setTimeout(resolve, secWait));
      console.log("====================================================================================================");
      console.log("2. Confirm the Trade by Security Buyer");
      console.log("====================================================================================================");
      await new Promise((resolve) => setTimeout(resolve, secWait));
      console.log("- Buyer now generates a hashed key for the Seller with solidity keccak256");
      await new Promise((resolve) => setTimeout(resolve, secWait));
      let hashedKeyBuyer = ethers.utils.solidityKeccak256(["string"],["keyBuyer"]);
      console.log("- Hashed Key for Seller is: %s",hashedKeyBuyer);

      console.log("- Buyer now calls 'confirmTrade' against the AssetContract providing trade details and hashed key for seller");
       const call = await deliveryContract.connect(buyer).confirmTrade(seller.address, trade_data, 1000, 998, hashedKeyBuyer);

       await expect(call).to.emit(deliveryContract, "TradeConfirmed");
      await new Promise((resolve) => setTimeout(resolve, secWait));
      console.log("- Contract performs trade data matching. Trade is now 'confirmed' and hashed key for seller is stored.");
      console.log("====================================================================================================");
  });


 it("Incept Asset-Transfer from Buyer", async () => {
     await new Promise((resolve) => setTimeout(resolve, secWait));
     console.log("====================================================================================================");
     console.log("3. Buyer incepts the DvP-Transfer against the Asset Contract by providing the Seller's encrypted key");
     console.log("====================================================================================================");
     await new Promise((resolve) => setTimeout(resolve, secWait));
     let keyEncryptedForSeller = await EthCrypto.encryptWithPublicKey(dvpOracle.publicKey,"keyForSeller");
     let keyEncrypedAsString = await EthCrypto.cipher.stringify(keyEncryptedForSeller);
     console.log("- Buyer generates encrypted key for the seller by using public key of DvP Oracle: %s",keyEncrypedAsString);
     await new Promise((resolve) => setTimeout(resolve, secWait));
     console.log("- Buyer calls 'inceptTransfer' against AssetContract providing the encrypted key for Seller");
     const call = await deliveryContract.connect(buyer).inceptTransfer(id, assetAmount, seller.address, keyEncrypedAsString) ;
     await expect(call).to.emit(deliveryContract, "AssetTransferIncepted");
     await new Promise((resolve) => setTimeout(resolve, secWait));
  });

  it("Incept Payment-Transfer from Seller", async () => {
      await new Promise((resolve) => setTimeout(resolve, secWait));
      console.log("====================================================================================================");
      console.log("3. Seller incepts the DvP-Transfer against the Payment Contract by providing the Buyer's and Seller's encrypted key");
      console.log("====================================================================================================");
      await new Promise((resolve) => setTimeout(resolve, secWait));
      let keyEncryptedBuyer = await EthCrypto.encryptWithPublicKey(dvpOracle.publicKey, "keyFoBuyer");
      let keyEncrypedBuyerAsString = await EthCrypto.cipher.stringify(keyEncryptedBuyer);
      console.log("- Seller generates encrypted key for the Buyer by using public key of DvP Oracle: %s",keyEncrypedBuyerAsString);
      await new Promise((resolve) => setTimeout(resolve, secWait));
      console.log("- Seller retrieves its encrypted key stored in the Asset Contract:", 0);
      await new Promise((resolve) => setTimeout(resolve, secWait));
      let keyEncrypedSellerAsString = "";
      console.log("- Buyer calls 'inceptTransfer' against AssetContract providing the encrypted key for Seller");
      // inceptTransfer(uint id, int amount, address from, string memory keyEncryptedSuccess, string memory keyEncryptedFailure)
      const call = await paymentContract.connect(buyer).inceptTransfer(id, paymentAmount, buyer.address, keyEncrypedBuyerAsString, keyEncrypedSellerAsString) ;
      await expect(call).to.emit(paymentContract, "PaymentTransferIncepted");
      await new Promise((resolve) => setTimeout(resolve, secWait));
  });


});