package com.example.demo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

public class SSEClientExample {

  public static void main(String[] args) {
    WebClient webClient = WebClient.builder().baseUrl("https://postman-echo.com").build();

    System.out.println("Starting SSE client...");

    webClient
        .get()
        .uri("/server-events/5") // Stream 5 events for demo
        .accept(MediaType.TEXT_EVENT_STREAM)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
        .doOnNext(
            event -> {
              System.out.println("Received SSE Event:");
              System.out.println("  ID: " + event.id());
              System.out.println("  Event: " + event.event());
              System.out.println("  Data: " + event.data());
              System.out.println("  Retry: " + event.retry());
              System.out.println("---");
            })
        .doOnComplete(() -> System.out.println("SSE stream completed"))
        .doOnError(error -> System.err.println("SSE error: " + error.getMessage()))
        .blockLast(); // Block to keep main thread alive until stream completes

    System.out.println("SSE client finished");
  }
}
