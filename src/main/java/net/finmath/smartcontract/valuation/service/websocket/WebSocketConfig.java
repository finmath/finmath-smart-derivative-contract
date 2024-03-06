package net.finmath.smartcontract.valuation.service.websocket;

import net.finmath.smartcontract.valuation.service.websocket.handler.ValuationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


/**
 * Websocket config
 * <p>
 * setting message size to 1MB and adding currently one handler and its endpoint to the Handler registry
 *
 * @author Peter Kohl-Landgraf
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(new ValuationHandler(), "/valuationfeed")
				.setAllowedOrigins("*");

	}


	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(1024 * 1024);
		container.setMaxBinaryMessageBufferSize(1024 * 1024);
		return container;
	}


}

