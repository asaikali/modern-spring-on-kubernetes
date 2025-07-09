package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

@SpringBootApplication
public class SseServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SseServerApplication.class, args);
  }
}
