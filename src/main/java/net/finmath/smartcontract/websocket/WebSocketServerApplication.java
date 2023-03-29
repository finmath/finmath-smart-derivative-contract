package net.finmath.smartcontract.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Collections;

@SpringBootApplication
@EnableWebSocket
public class WebSocketServerApplication implements WebSocketConfigurer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebSocketServerApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "443"));
        app.run(args);
        //SpringApplication.run(WebSocketServerApplication.class, args);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/feed");
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new FeedWebSocketHandler();
    }
}