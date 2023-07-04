package net.finmath.smartcontract.service;


import net.finmath.smartcontract.service.config.BasicAuthWebSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import java.util.TimeZone;

/**
 * Spring boot entry point.
 */
@SpringBootApplication
@EnableWebSocket
@Import(BasicAuthWebSecurityConfiguration.class)
@ComponentScan(basePackages={"net.finmath.smartcontract.marketdata.database","net.finmath.smartcontract.service"})
public class Application {

	/**
	 * Application entry point.
	 *
	 * @param args Program arguments (not used).
	 */
	public static void main(String[] args) {
		System.out.println("Setting the timezone: "+TimeZone.getTimeZone("UTC").getID());
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(Application.class, args);
	}

}
