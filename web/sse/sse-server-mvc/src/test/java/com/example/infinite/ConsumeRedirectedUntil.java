package com.example.infinite;

import com.example.stocks.StockPrice;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

public class ConsumeRedirectedUntil {

  public static void main(String[] args) throws InterruptedException {
    HttpClient httpClient = HttpClient.create()
        .followRedirect(true); // enables automatic redirect following

    WebClient client = WebClient.builder()
        .baseUrl("http://localhost:8080")
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();

    BigDecimal targetPrice = new BigDecimal("109");
    client.get()
        .uri("/test/redirect")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<StockPrice>>() {})
        .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
            .maxBackoff(Duration.ofSeconds(30)))
        .takeUntil( event -> targetPrice.compareTo(event.data().price()) <= 0)
        .subscribe(
            event -> {
              System.out.println(event.data());
            },
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Stream completed")
        );


    // Keep application alive for demo
    Thread.sleep(60_000);
  }
}
