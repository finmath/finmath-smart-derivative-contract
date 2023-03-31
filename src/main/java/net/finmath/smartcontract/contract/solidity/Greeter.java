package net.finmath.smartcontract.contract.solidity;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.9.4.
 */
@SuppressWarnings("rawtypes")
public class Greeter extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5060408051808201909152600b8082526a12195b1b1bc815dbdc9b1960aa1b602090920191825261004391600091610049565b5061011d565b828054610055906100e2565b90600052602060002090601f01602090048101928261007757600085556100bd565b82601f1061009057805160ff19168380011785556100bd565b828001600101855582156100bd579182015b828111156100bd5782518255916020019190600101906100a2565b506100c99291506100cd565b5090565b5b808211156100c957600081556001016100ce565b600181811c908216806100f657607f821691505b6020821081141561011757634e487b7160e01b600052602260045260246000fd5b50919050565b6102e28061012c6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063131a06801461003b578063cfae321714610050575b600080fd5b61004e6100493660046101aa565b61006e565b005b61005861007f565b604051610065919061021c565b60405180910390f35b61007a60008383610111565b505050565b60606000805461008e90610271565b80601f01602080910402602001604051908101604052809291908181526020018280546100ba90610271565b80156101075780601f106100dc57610100808354040283529160200191610107565b820191906000526020600020905b8154815290600101906020018083116100ea57829003601f168201915b5050505050905090565b82805461011d90610271565b90600052602060002090601f01602090048101928261013f5760008555610185565b82601f106101585782800160ff19823516178555610185565b82800160010185558215610185579182015b8281111561018557823582559160200191906001019061016a565b50610191929150610195565b5090565b5b808211156101915760008155600101610196565b600080602083850312156101bd57600080fd5b823567ffffffffffffffff808211156101d557600080fd5b818501915085601f8301126101e957600080fd5b8135818111156101f857600080fd5b86602082850101111561020a57600080fd5b60209290920196919550909350505050565b600060208083528351808285015260005b818110156102495785810183015185820160400152820161022d565b8181111561025b576000604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061028557607f821691505b602082108114156102a657634e487b7160e01b600052602260045260246000fd5b5091905056fea26469706673582212207c4f5cdae8d9814ca81bc91c4f386312898befaa2e0485640085bd0f345a9ac764736f6c63430008070033";

    public static final String FUNC_GREET = "greet";

    public static final String FUNC_STORE = "store";

    @Deprecated
    protected Greeter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Greeter(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Greeter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Greeter(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> greet() {
        final Function function = new Function(FUNC_GREET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> store(String str) {
        final Function function = new Function(
                FUNC_STORE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(str)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Greeter load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Greeter(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Greeter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Greeter(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Greeter load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Greeter(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Greeter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Greeter(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Greeter> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Greeter.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Greeter> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Greeter.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Greeter> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Greeter.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Greeter> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Greeter.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
