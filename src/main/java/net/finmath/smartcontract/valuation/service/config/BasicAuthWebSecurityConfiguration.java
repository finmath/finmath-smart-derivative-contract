package net.finmath.smartcontract.valuation.service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.valuation.service.utils.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
//@EnableConfigurationProperties(value = ApplicationProperties.class)
public class BasicAuthWebSecurityConfiguration {

	Logger logger = LoggerFactory.getLogger(BasicAuthWebSecurityConfiguration.class);
	@Value("${serviceUrl}")
	String serviceUrl;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, InMemoryUserDetailsManager userDetailsManager) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authz -> {
					try {
						authz.anyRequest().authenticated();
					} catch (Exception e) {
						throw new SDCException(ExceptionId.SDC_AUTH_ERROR, e.getMessage());
					}
				})
				.httpBasic(Customizer.withDefaults())
				.userDetailsService(userDetailsManager)
				.cors(Customizer.withDefaults())
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(((request, response, authException) -> {
							logger.warn("401 Unauthorized: {}, {}", extractBasicAuthUsername(request), getOriginalUri(request), authException);
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "SDC: Unauthorized access request");
						}))
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							logger.warn("403 Forbidden: {}, {}", extractBasicAuthUsername(request), getOriginalUri(request), accessDeniedException);
							response.sendError(HttpServletResponse.SC_FORBIDDEN, "SDC: Forbidden");
						}));
		return http.build();
	}

	private static String extractBasicAuthUsername(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Basic ")) {
			String base64Credentials = authHeader.substring("Basic ".length());
			String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
			int idx = credentials.indexOf(':');
			if (idx > 0) {
				return credentials.substring(0, idx);
			}
		}
		return "unknown";
	}

	private static String getOriginalUri(HttpServletRequest request) {
		String originalUri = (String) request.getAttribute("jakarta.servlet.forward.request_uri");
		if (originalUri == null) {
			originalUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
		}
		if (originalUri == null) {
			originalUri = request.getRequestURI();
		}
		return originalUri;
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
