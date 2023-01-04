package net.finmath.smartcontract.service;

import net.finmath.smartcontract.service.config.BasicAuthWebSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Spring boot entry point.
 */
/*
@OpenAPIDefinition(
	servers = {
		@Server(url = "/", description = "Default Server URL")
	}
)
*/
@SpringBootApplication
@Import(BasicAuthWebSecurityConfiguration.class)
public class Application {

	/**
	 * Application entry point.
	 *
	 * @param args Program arguments (not used).
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
