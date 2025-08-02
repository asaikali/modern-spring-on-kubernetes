package com.example.demo;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MyConfiguration {

  @LoadBalanced
  @Bean
  RestClient.Builder restClientBuilder(RestClientBuilderConfigurer configurer) {
    return configurer.configure(RestClient.builder());
  }
}
