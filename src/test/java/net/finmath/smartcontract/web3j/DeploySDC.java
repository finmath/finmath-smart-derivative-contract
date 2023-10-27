package net.finmath.smartcontract.web3j;

import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class DeploySDC {

    private static final Logger logger = LoggerFactory.getLogger(DeploySDC.class);
    public static void main(String[] args) throws Exception{

            UserAccount.generateNew("xyz",new File("Q:\\"));

            /* GET fpml and descriptor*/
            final String fpml = new String(DeploySDC.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
            SmartDerivativeContractDescriptor descriptor = SDCXMLParser.parse(fpml);
            String party1ID = descriptor.getCounterparties().get(0).getId();
            String party2ID = descriptor.getCounterparties().get(1).getId();

            /* READ blockchain specific stuff */
            String pass = System.getProperty("password", "password");
            String geth_url = System.getProperty("geth_url", "<put your node hier>");

            /* Build Accounts and connections */
            final UserAccount accountAlice = new UserAccount(null, pass);
            final UserAccount accountBob = new UserAccount(null, pass);
            accountAlice.getCredentials();
            accountBob.getCredentials();
            final Connector connector = new Connector(geth_url);
            final Web3j connection = connector.getWeb3Connection();
            System.out.println("Connected to Ethereum client version: " + connection.web3ClientVersion().send().getWeb3ClientVersion());
           // Greeter token = Greeter.deploy(connection,accountBob.getCredentials(), connector.getStaticGasProvider()).send();
           // String greeting = token.greet().send();
           // System.out.println();

    }
}
