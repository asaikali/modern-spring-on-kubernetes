package com.example.eventsource;

import org.springframework.web.reactive.function.client.WebClient;

public class SseClient {

  private final WebClient webClient;

  public SseClient(WebClient webClient) {
    this.webClient = webClient;
  }


}
