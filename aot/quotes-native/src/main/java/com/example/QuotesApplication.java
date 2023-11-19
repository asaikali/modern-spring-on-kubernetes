package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuotesApplication {

  public static void main(String[] args) {
    SpringApplication.run(QuotesApplication.class, args);
  }

  private static boolean isRunningInNativeImage() {
    return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
  }
}
