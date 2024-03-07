package net.finmath.smartcontract.demo.web3;



import net.finmath.smartcontract.web3.ERC20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.File;
import java.math.BigInteger;

public class Launcher {

	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) throws Exception {
		//String keyfile1 = System.getenv().getOrDefault("web3/key.json","na");
		//File keySource = ResourceUtils.getFile("net/finmath/smartcontract/web3/key.json");
		WalletUtils.loadCredentials("password","");
		ECKeyPair ecKeyPair = Keys.createEcKeyPair();
		String privateKeyString = ecKeyPair.getPrivateKey().toString(16);
		Credentials credentials = Credentials.create(privateKeyString);
		System.out.println("Private key: " + credentials.getEcKeyPair().getPrivateKey().toString(16));
		System.out.println("Public key: " + credentials.getEcKeyPair().getPublicKey().toString(16));
		System.out.println("Wallet Address: " + credentials.getAddress());

		Web3j web3j = Web3j.build(new HttpService("http://besu.apps.digital-campus.io/bootnode"));
		logger.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

		StaticGasProvider staticGasProvider;
		StaticGasProvider provider = new StaticGasProvider(BigInteger.valueOf(1L), BigInteger.valueOf(10_000_000_000L));
		ERC20 token = ERC20.deploy(web3j, credentials, provider,"testToken","").send();
		logger.info("Token deployed at address: " + token.getContractAddress());

	}
}
