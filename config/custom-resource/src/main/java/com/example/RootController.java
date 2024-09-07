package com.example;

import com.example.greeter.Greeting;
import com.example.greeter.GreetingService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  private GreetingService greetingService;

  public RootController(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @GetMapping("/")
  public Map<String, Object> get(
      @RequestParam(value = "language", required = false, defaultValue = "en") String language) {
    String message = greetingService.getGreetingMessage(language);
    return Map.of("date", LocalDateTime.now(), "message", message);
  }

  @RequestMapping("/greetings")
  List<Greeting> getGreetings() {
    return this.greetingService.getAllGreetings();
  }
}
