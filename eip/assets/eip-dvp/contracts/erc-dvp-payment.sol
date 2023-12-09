// SPDX-License-Identifier: CC0-1.0
pragma solidity >=0.7.0;

/*------------------------------------------- DESCRIPTION ---------------------------------------------------------------------------------------*/

/**
 * @title ERC-DVP PAYMENT Locking of assets and conditional decryption of release keys upon payment to allow secure stateless delivery-versus-payment.
 * @dev Interface specification for a smart contract that enables secure stateless delivery-versus-payment.
 *
 * The specification consists of two interface, one
 * is implemented by a smart contract on the "asset chain"
 * (the asset contract), the other is implemented by
 * a smart contract on the "payment chain" (the payment contract).
 *
 * This is the payment contracts interface.
 *
 * The rationale is that the payment in setup with
 * two encrypted keys, the encryptedSuccessKey and the encryptedFailureKey.
 * Upon payment transfer a conditional decryption of one the encrypted keys
 * is performed.
 */
interface IDvPPayment {

    /*------------------------------------------- EVENTS ---------------------------------------------------------------------------------------*/

    /**
     * @dev Emitted  when the transfer for the payment is incepted.
     * @param initiator is the address from which payment transfer was incepted
     * @param id the trade ID.
     * @param the amount to be transfered.
     */
    event PaymentTransferIncepted(address initiator, uint id, int amount);

    /**
     * @dev Emitted when a the transfer has been performed with a success or failure.
     * @param id the trade ID.
     * @param encryptedKey The encrypted key associated with the transaction status.
     */
    event TransferKeyRequested(uint id, string encryptedKey);

    /**
     * @dev Emitted when a the decrypted key has been obtained.
     * @param id the trade ID.
     * @param success a boolean indicating the status. True: success. False: failure.
     * @param key the descrypted key.
     */
    event TransferKeyReleased(uint id, bool success, string key);

    /*------------------------------------------- FUNCTIONALITY ---------------------------------------------------------------------------------------*/

    /**
     * @notice Called from the payer of the payment to initiate payment transfer.
     * @dev emits a {PaymentTransferIncepted}
     * @param id the trade identifier of the trade.
     * @param amount the amount to be transfered.
     * @param from The address of the seller (the address of the buyer is message.sender).
     * @param keyEncryptedSuccess Encryption of the key that is emitted upon success.
     * @param keyEncryptedFailure Encryption of the key that is emitted upon failure.
     */
    function inceptTransfer(uint id, int amount, address from, string keyEncryptedSuccess, string keyEncryptedFailure) external;

    /**
     * @notice Called from the receiver of the payment to complete payment transfer.
     * @dev emits a {PaymentTransferIncepted}
     * @param id the trade identifier of the trade.
     * @param from The address of the seller (the address of the buyer is message.sender).
     * @param to The address of the seller (the address of the buyer is message.sender).
     * @param keyEncryptedSuccess Encryption of the key that is emitted upon success.
     * @param keyEncryptedFailure Encryption of the key that is emitted upon failure.
     */
    function transferAndDecrypt(uint id, address from, address to, keyEncryptedSuccess, string keyEncryptedFailure) external;

    /**
     * @notice Called from the payer of the payment to initiate payment transfer.
     * @dev emits a {PaymentTransferIncepted}
     * @param id the trade identifier of the trade.
     * @param amount the amount to be transfered.
     * @param from The address of the seller (the address of the buyer is message.sender).
     * @param keyEncryptedSuccess Encryption of the key that is emitted upon success.
     * @param keyEncryptedFailure Encryption of the key that is emitted upon failure.
     */
    function cancelAndDecrypt(uint id, address from, address to, keyEncryptedSuccess, string keyEncryptedFailure) external;
}
