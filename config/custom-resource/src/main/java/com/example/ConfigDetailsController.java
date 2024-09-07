package com.example;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigDetailsController {

  private String message;

  public ConfigDetailsController(@Value("${message:hardcoded default value}") String message) {
    this.message = message;
  }

  @GetMapping("/")
  public Map<String, Object> get() {
    return Map.of("date", LocalDateTime.now(), "message", message);
  }
}
