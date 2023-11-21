package net.finmath.smartcontract.web3j;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

public class Connector {

	final private String url;
	private Web3j web3j;

	private StaticGasProvider staticGasProvider;

	public Connector(String url) {
		this.url = url;
	}

	public Web3j getWeb3Connection() {
		if (web3j != null) {
			return web3j;
		}

		web3j = Web3j.build(new HttpService(url));
		return web3j;
	}

	public StaticGasProvider getStaticGasProvider() {
		if (staticGasProvider != null) {
			return staticGasProvider;
		}

		try {
			staticGasProvider = new StaticGasProvider(BigInteger.valueOf(1L), BigInteger.valueOf(10_000_000_000L)); // standard ethereum
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return staticGasProvider;
	}
}
