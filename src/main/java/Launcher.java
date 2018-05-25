import java.util.UUID;

import net.finmath.smartcontract.specifications.*;

public class Launcher {
	public static void main(String[] args) {
		String address = UUID.randomUUID().toString();
		Wallet wallet = new Wallet(address);
		System.out.println(address);
	}
}
