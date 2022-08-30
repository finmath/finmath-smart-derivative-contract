// SPDX-License-Identifier: MIT
pragma solidity >=0.7.0 <0.9.0;

/*------------------------------------------- DESCRIPTION ---------------------------------------------------------------------------------------*/

/**
 * @dev Interface specification for - a Smart Derivative Contract - to either handle trade inception and post-trade processing of
 * one or M trades between two or N counterparties.
 *
 * The concept of a Smart Derivative Contract was introduced in the following paper: https://papers.ssrn.com/sol3/papers.cfm?abstract_id=3163074
 * A Smart Derivative Contract is a deterministic settlement protocol which has economically the same behaviour as a collateralized OTC
 * Derivative. In contrast to a collateralized derivative contract based and collateral flows are netted such that the smart derivative
 * introduces a high frequent - e.g. daily - settlement flow schedule. With each settlement flow  net present value of the underlying contract is
 * exchanged and the value of the contract is reset to zero.
 * To automatically process the settlement counterparties need to provide sufficient prefunded margin amounts and termination fees at the
 * beginning of each settlement cycle. Through a settlement cycle the margin amounts are locked.
 * A SDC contract automatically terminates the derivatives contract if there is insufficient prefunding or if the settlement amount exceeds a
 * prefunded margin balance. Beyond mutual termination is also intended by the function specification.
 *
 * Events include: TradeIncepted, TradeConfirmed, TradeActive, TradeTerminated, ValuationRequest, TerminationRequest, TerminationConfirmed, MarginLocked, MarginUpdated,
 * Functionality includes: Trade-Inception, Trade-Confirmation, Settlement-Processing, and Margin-Locks

 */
interface ISDC {

}
