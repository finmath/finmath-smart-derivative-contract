package net.finmath.smartcontract.service.config;

import net.finmath.smartcontract.service.utils.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(value = ApplicationProperties.class)
public class BasicAuthWebSecurityConfiguration
{

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        .anyRequest().authenticated().and().httpBasic();
    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService(ApplicationProperties applicationProperties) {
    return new InMemoryUserDetailsManager(buildUserDetailsList(applicationProperties));
  }

  /**
   * Helper class to generate List of UserDetails from application properties.
   * @param applicationProperties injected properties
   * @return List of UserDetails
   */
  private List<UserDetails> buildUserDetailsList(ApplicationProperties applicationProperties) {
    List<UserDetails> userDetailsList = new ArrayList<>();
    applicationProperties.getUsers().forEach((sdcUser) -> userDetailsList.add(User
            .withUsername(sdcUser.getUsername())
            .password("{noop}"+sdcUser.getPassword())
            .roles(sdcUser.getRole())
            .build()));
    return userDetailsList;
  }
}
