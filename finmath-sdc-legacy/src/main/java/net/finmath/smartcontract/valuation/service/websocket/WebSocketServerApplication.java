package net.finmath.smartcontract.valuation.service.websocket;

import net.finmath.smartcontract.valuation.service.config.BasicAuthWebSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Collections;


/**
 * Spring Boot WebSocket Server Application using credentials from application.yml.
 *
 * @author Peter Kohl-Landgraf
 */

@SpringBootApplication
@EnableWebSocket
@Import(BasicAuthWebSecurityConfiguration.class)
public class WebSocketServerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WebSocketServerApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "443"));
		app.run(args);
	}


}