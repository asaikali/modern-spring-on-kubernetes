package com.example.billboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class BillboardController {

  @Value("${message:value was not read from config server}")
  private String message;

  @GetMapping("/")
  public String greet() {
    return message;
  }
}
