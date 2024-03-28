package net.finmath.smartcontract.valuation.service.config;

import net.finmath.smartcontract.valuation.service.utils.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(value = ApplicationProperties.class)
public class BasicAuthWebSecurityConfiguration {

	Logger logger = LoggerFactory.getLogger(BasicAuthWebSecurityConfiguration.class);
	@Value("${serviceUrl}")
	String serviceUrl;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authz -> {
					try {
						authz.anyRequest().authenticated().and().httpBasic();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				})
				.cors();
		return http.build();
	}

	// when using OpenAPI/Swagger class-level annotations are ignored, this is the global config to work around that
	@Bean
	public WebMvcConfigurer corsConfigurer() {

		logger.info("CORS filter has been loaded.");
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/editor/**").allowedOrigins("http://localhost:4200", serviceUrl); // localhost:4200 is the angular dev server
			}
		};
	}


	@Bean
	public InMemoryUserDetailsManager userDetailsService(ApplicationProperties applicationProperties) {
		return new InMemoryUserDetailsManager(buildUserDetailsList(applicationProperties));
	}

	/**
	 * Helper class to generate List of UserDetails from application properties.
	 *
	 * @param applicationProperties injected properties
	 * @return List of UserDetails
	 */
	private List<UserDetails> buildUserDetailsList(ApplicationProperties applicationProperties) {
		List<UserDetails> userDetailsList = new ArrayList<>();
		applicationProperties.getUsers().forEach(sdcUser -> userDetailsList.add(User
				.withUsername(sdcUser.getUsername())
				.password("{noop}" + sdcUser.getPassword())
				.roles(sdcUser.getRole())
				.build()));
		return userDetailsList;
	}
}
