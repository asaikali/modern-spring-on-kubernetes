package com.example.stream_01.one;

import java.io.IOException;
import java.time.Duration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

public class ConsumeWebfluxOne {

  public static void main(String[] args) throws InterruptedException, IOException {
    WebClient client = WebClient.create("http://localhost:8080");

    client
        .get()
        .uri("/webflux/stream/one")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
        .retryWhen(
            Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(30)))
        .subscribe(
            event -> {
              System.out.println("<SSEEvent>");

              System.out.println("  <id>" + (event.id() != null ? event.id() : "") + "</id>");
              System.out.println(
                  "  <event>" + (event.event() != null ? event.event() : "") + "</event>");

              String data = event.data();
              System.out.println("  <data>");
              if (data != null) {
                System.out.println(data);
              }
              System.out.println("  </data>");

              System.out.println(
                  "  <retry>" + (event.retry() != null ? event.retry() : "") + "</retry>");

              String comment = event.comment();
              System.out.println("  <comment>");
              if (comment != null) {
                System.out.println(comment);
              }
              System.out.println("  </comment>");

              System.out.println("</SSEEvent>\n");
            },
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Stream completed"));

    System.out.println("Press Enter to exit...");
    System.in.read();
  }
}
