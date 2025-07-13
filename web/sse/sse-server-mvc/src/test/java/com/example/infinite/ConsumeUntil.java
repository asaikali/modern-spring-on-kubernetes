package com.example.infinite;

import com.example.stream_02.prices.StockPrice;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

public class ConsumeUntil {

  public static void main(String[] args) throws InterruptedException {
    WebClient client = WebClient.create("http://localhost:8080");

    BigDecimal targetPrice = new BigDecimal("109");
    client
        .get()
        .uri("/mvc/stream/infinite")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<StockPrice>>() {})
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(30)))
        .takeUntil(event -> targetPrice.compareTo(event.data().price()) <= 0)
        .subscribe(
            event -> {
              System.out.println(event.data());
            },
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Stream completed"));

    // Keep application alive for demo
    Thread.sleep(60_000);
  }
}
