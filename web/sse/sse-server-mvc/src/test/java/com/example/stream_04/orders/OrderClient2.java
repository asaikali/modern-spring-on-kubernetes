package com.example.stream_04.orders;

import java.math.BigDecimal;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class OrderClient2 {

  private static final Logger log = LoggerFactory.getLogger(OrderClient2.class);

  public Mono<String> placeOrder(LimitOrderRequest order) {
    WebClient client = WebClient.create("http://localhost:8080/orders");

    return client
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
        .bodyValue(order)
        .exchangeToMono(
            response -> {
              MediaType contentType =
                  response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

              if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                System.out.println("Processing JSON response");
                // Return JSON directly
                return response
                    .bodyToMono(String.class)
                    .doOnNext(json -> System.out.println("JSON: " + json));
              }

              if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
                System.out.println("Processing SSE stream");
                // Process SSE events immediately and return final result
                return response
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                    .doOnSubscribe(sub -> System.out.println("Subscribed to SSE stream"))
                    .doOnNext(
                        sse -> {
                          System.out.println("Received SSE event:");
                          System.out.println("  Event: " + sse.event());
                          System.out.println("  Data: " + sse.data());
                          System.out.println("  ID: " + sse.id());
                          System.out.println("---");
                        })
                    .doOnComplete(() -> System.out.println("SSE stream completed"))
                    .doOnError(error -> System.err.println("SSE stream error: " + error))
                    .takeUntil(sse -> "result".equals(sse.event()))
                    .last() // Get the last event before "result"
                    .map(lastEvent -> "SSE processing completed. Last event: " + lastEvent.data())
                    .onErrorReturn("SSE stream completed without final event")
                    .timeout(Duration.ofSeconds(30));
              }

              response.releaseBody();
              return Mono.error(
                  new IllegalStateException("Unsupported content type: " + contentType));
            });
  }

  public static void main(String[] args) {
    var client = new OrderClient2();

    try {
      // Much simpler - just get the final result
      String result =
          client.placeOrder(new LimitOrderRequest("APPL", 100, BigDecimal.valueOf(101))).block();

      System.out.println("\nFinal result: " + result);

    } catch (Exception e) {
      System.err.println("Error processing order: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
