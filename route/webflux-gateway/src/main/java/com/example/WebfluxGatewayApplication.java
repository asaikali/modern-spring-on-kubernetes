package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableDiscoveryClient no longer required keeping this comment to point that it is no longer
// needed
@SpringBootApplication
public class WebfluxGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebfluxGatewayApplication.class, args);
  }
}
