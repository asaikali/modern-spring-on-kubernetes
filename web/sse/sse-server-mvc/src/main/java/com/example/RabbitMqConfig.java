package com.example;

import com.rabbitmq.stream.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

  @Bean(destroyMethod = "close")
  Environment rabbitMqEnvironment() {
    return Environment.builder().build();
  }
}
