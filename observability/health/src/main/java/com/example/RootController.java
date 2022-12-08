package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  private final ExampleHealthIndicator exampleHealthIndicator;

  public RootController(ExampleHealthIndicator exampleHealthIndicator) {
    this.exampleHealthIndicator = exampleHealthIndicator;
  }

  @GetMapping
  public String get() {
    return "Health Checks Will Pass: " + exampleHealthIndicator.getState();
  }

  @GetMapping("/error")
  public String generateErorr() {
    throw new RuntimeException("oops something went wrong");
  }
  @GetMapping("/fail")
  public String fail() {
    exampleHealthIndicator.setState(false);
    return "Health Checks Will Pass: " + exampleHealthIndicator.getState();
  }

  @GetMapping("/pass")
  public String pass() {
    exampleHealthIndicator.setState(true);
    return "Health Checks Will Pass: " + exampleHealthIndicator.getState();
  }
}
