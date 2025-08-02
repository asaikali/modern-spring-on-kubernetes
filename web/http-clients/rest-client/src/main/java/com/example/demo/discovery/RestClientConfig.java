package com.example.demo.discovery;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

/**
 * This example shows how to have multiple RestClient builders that can have different
 * configurations, some load balanced and some not.
 */
@Configuration
public class RestClientConfig {

  @Bean
  @CustomDiscovery
  RestClient.Builder discoveryRestClient(RestClientBuilderConfigurer configurer) {
    return configurer
        .configure(RestClient.builder())
        .requestInterceptor(new DiscoveryRewriteInterceptor());
  }

  @Bean
  @Primary
  public RestClient.Builder plainRestClientBuilder(RestClientBuilderConfigurer configurer) {
    return configurer.configure(RestClient.builder());
  }
}
