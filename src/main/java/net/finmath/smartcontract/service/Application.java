package net.finmath.smartcontract.service;

import net.finmath.smartcontract.service.config.BasicAuthWebSecurityConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;

import java.util.Collections;
import java.util.Arrays;

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
// @SpringBootApplication
// @Import(BasicAuthWebSecurityConfiguration.class)
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@ComponentScan(basePackages = "net.finmath.smartcontract.product.xml")
@ComponentScan(basePackages = "net.finmath.smartcontract.api")
@ComponentScan(basePackages = "net.finmath.smartcontract.valuation.controllers")
public class Application {


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/generatexml").allowedOrigins("http://localhost:4200");
			}
		};
	}

	/**
	 * Application entry point.
	 *
	 * @param args Program arguments (not used).
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
