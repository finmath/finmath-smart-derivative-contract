package net.finmath.smartcontract.web3j;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class UserAccount {

	final private File keySource;
	final private String password;
	private Credentials credentials;


	public static UserAccount generateNew(String password, File destinationDir) throws Exception{
		final String fileName = WalletUtils.generateFullNewWalletFile(password,destinationDir);
		final File file = Path.of(fileName).toFile();
		return new UserAccount(file,password);
	}


	public UserAccount(File keyFile, String password){
		keySource = keyFile;
		this.password = password;
	}

	public Credentials getCredentials()  {
		if(credentials != null) {
			return credentials;
		}

		try {
			credentials = WalletUtils.loadCredentials(password,keySource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CipherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return credentials;
	}



}
