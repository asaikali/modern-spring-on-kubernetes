package com.example;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TracingMessageServiceApplication {

  @Bean
  ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }

  public static void main(String[] args) {
    SpringApplication.run(TracingMessageServiceApplication.class, args);
  }
}
