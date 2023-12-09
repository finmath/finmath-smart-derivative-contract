---
eip: dvp
title: Conditional-upon-Tranfer-Decryption for Delivery-Versus-Payment
description: A Proposal for a Lean and Functional Delivery versus Payment
author: Christian Fries (@cfries), Peter Kohl-Landgraf (@pekola)
discussions-to: https://ethereum-magicians.org/t/eip-6123-smart-derivative-contract-frictionless-processing-of-financial-derivatives/12134
status: Draft
type: Standards Track
category: ERC
created: 2023-12-05
---

## Abstract


## Motivation


## Specificaiton

### Methods

#### Smart Contract on the Asset Chain

```solidity
interface IAssetContract {

    // events
    event Event();

    // functions
    function inceptTransfer(uint id, int amount, address from, string keyEncryptedSeller);

    function confirmTransfer(uint id, int amount, address to, string keyEncryptedBuyer);

    function transferWithKey(uint id, string key);
}
```
#### Smart Contract on the Payment Chain

```solidity
interface IPaymentContract {

    // events
    event Event();

    // functions
    function inceptTransfer(uint id, int amount, address from, string keyEncryptedBuyer, string keyEncryptedSeller);

    function transferAndDecrypt(uint id, address from, address to, keyEncryptedBuyer, string keyEncryptedSeller);

    function cancelAndDecrypt(uint id, address from, address to, keyEncryptedBuyer, string keyEncryptedSeller);
}
```

## Rationale

The interface design is based on the following considerations:

-...



### Sequence diagram of delivery versus payment

![image info](../assets/eip-dvp/doc/DvP-Seq-Diag.png)

## Test Cases


## Reference Implementation

## Security Considerations

No known security issues up to now.

## Copyright

Copyright and related rights waived via [CC0](../LICENSE.md).


