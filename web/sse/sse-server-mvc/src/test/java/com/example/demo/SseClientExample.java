package com.example.demo;

import com.example.stream_04.orders.sse.client.SseClient;
import org.springframework.web.reactive.function.client.WebClient;

public class SseClientExample {

  public static void main(String[] args) {
    WebClient.Builder builder = WebClient.builder().baseUrl("http://localhost:8080/watchlist");

    SseClient sseClient =
        new SseClient(builder, "/sse/stream")
            .onConnect(() -> System.out.println("Connected to SSE stream"))
            .onEvent(
                event -> {
                  System.out.println("Event ID: " + event.id());
                  System.out.println("Event Type: " + event.event());
                  System.out.println("Data: " + event.data());
                  System.out.println("Retry: " + event.retry());
                  System.out.println("----------------------------");
                  return true; // Continue processing
                })
            .onError(error -> System.err.println("Error: " + error.getMessage()));

    sseClient.start();

    // Keep running
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // consumer.close();
  }
}
