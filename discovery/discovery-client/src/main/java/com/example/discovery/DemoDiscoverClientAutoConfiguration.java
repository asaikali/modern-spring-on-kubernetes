package com.example.discovery;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class DemoDiscoverClientAutoConfiguration {

  @Bean
  public DemoDiscoveryClient discoveryClient() {
    return new DemoDiscoveryClient();
  }
}
