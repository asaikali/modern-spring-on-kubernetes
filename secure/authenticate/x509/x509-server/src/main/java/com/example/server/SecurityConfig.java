package com.example.server;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .x509(
            x509 ->
                x509.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                    .userDetailsService(userDetailsService()));

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      // Implement your logic to load user details by username (CN)
      // For example, you can load from a database or any other source
      if ("client".equals(username)) {
        return new User(username, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
      } else {
        throw new UsernameNotFoundException("User not found: " + username);
      }
    };
  }
}
