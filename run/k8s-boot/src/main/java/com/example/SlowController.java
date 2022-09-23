package com.example;

import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SlowController {

  @GetMapping("/slow")
  String get() throws InterruptedException {
    Thread.sleep(10_000);
    return "hello the time is " + LocalDateTime.now();
  }
}
