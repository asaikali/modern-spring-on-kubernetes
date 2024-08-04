package com.example.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    var userDetailsService = new InMemoryUserDetailsManager();

    var user =
        User.withUsername("user")
            .password("{noop}password") // {noop} is used for plain text passwords
            .roles("USER")
            .build();

    var admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();

    userDetailsService.createUser(user);
    userDetailsService.createUser(admin);

    return userDetailsService;
  }
}
