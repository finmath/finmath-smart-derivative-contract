package net.finmath.smartcontract.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class WebFluxConfig {

    @Autowired
    private WebFluxWebSocketHandler handler;

    @Bean
    public HandlerMapping handlerMapping(){
        Map<String, WebFluxWebSocketHandler> handlerMap = Map.of(
                "/uppercase", handler
        );
        return new SimpleUrlHandlerMapping(handlerMap, 1);
    }

    @EnableWebSecurity
    public class SecurityConfig {

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .inMemoryAuthentication()
                    .withUser("user").password("password").roles("USER");
        }
    }

}