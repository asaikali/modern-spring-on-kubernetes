package com.example.demo;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ObservationBasicsApplication {

  @Bean
  ObservationRegistry observationRegistry() {
    return ObservationRegistry.create();
  }

  public static void main(String[] args) {
    SpringApplication.run(ObservationBasicsApplication.class, args);
  }
}
