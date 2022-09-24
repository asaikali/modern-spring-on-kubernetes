package com.example;

import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  private final Logger logger = LoggerFactory.getLogger(HelloController.class);

  @GetMapping("/")
  public String hello() {
    logger.info(
        "Hello called {} -> {}",
        StructuredArguments.keyValue("foo", 123),
        StructuredArguments.value("bar", "world"));
    return "hello";
  }
}
