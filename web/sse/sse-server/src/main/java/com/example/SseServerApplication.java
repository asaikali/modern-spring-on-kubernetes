package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

@SpringBootApplication
public class SseServerApplication {

  public static void main(String[] args) {

    String comment = """
              This event demonstrates all the fields allowed by SSE events
              payload is multi line notice how an SSE event can preserve formatting
              Check the README.md file in the see folder for an explanation of SSE events
              Event generated from a WebFlux controller /webflux/stream/one
              Emitted from '%s' thread""".formatted(Thread.currentThread().getName());

    System.out.println(StringUtils.replace(comment, "\n", "\n:"));

    SpringApplication.run(SseServerApplication.class, args);
  }
}
