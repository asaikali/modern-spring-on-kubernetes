package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${services.hello}/time")
public class HelloController {

  // maps to /hello/time and /hello/time/ b
  @GetMapping({ "", "/" })
  String get() {
    return "Time is " + LocalDateTime.now();
  }

  // maps to /hello/time/date
  @GetMapping("/date")
  String date() {
    return "date is " + LocalDate.now();
  }
}
