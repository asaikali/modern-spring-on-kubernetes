package com.example;

import io.micrometer.tracing.SpanName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BillboardController {

  private final Logger logger = LoggerFactory.getLogger(BillboardController.class);
  private final RestTemplate restTemplate;

  public BillboardController(RestTemplateBuilder builder) {

    // TLDR: Always use RestTemplateBuilder
    //
    // If ou create the rest template using new RestTemplate() instead of using
    // a RestTemplateBuilder the created template will not be configured with
    // the correct instrumentation setting you will not see any trace ids
    // propagated to the backend message-service
    this.restTemplate = builder.build();
  }

  @GetMapping("/message")
  @SpanName("get()")
  public String get() {

    logger.info("Calling message-service");
    Quote quote = restTemplate.getForObject("http://localhost:8081/", Quote.class);
    logger.info("Got quote object",quote);
    return quote.getQuote() + " -- " + quote.getAuthor();
  }
}
