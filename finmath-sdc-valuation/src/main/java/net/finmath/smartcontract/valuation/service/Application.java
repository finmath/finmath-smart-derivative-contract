package net.finmath.smartcontract.valuation.service;


import net.finmath.smartcontract.valuation.service.config.BasicAuthWebSecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@ComponentScan(basePackages = {"net.finmath.smartcontract.valuation.marketdata.database", "net.finmath.smartcontract.valuation.service"})
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	/**
	 * Application entry point.
	 *
	 * @param args Program arguments (not used).
	 */
	public static void main(String[] args) {
		logger.info("Setting the timezone: {}", TimeZone.getTimeZone("UTC").getID());
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(Application.class, args);
	}

}
