package com.example.messageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GatewayMessageServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayMessageServiceApplication.class, args);
  }
}
