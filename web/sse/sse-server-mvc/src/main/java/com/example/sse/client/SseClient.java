package com.example.sse.client;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

/** Native WebFlux SSE client with automatic resumption */
public class SseClient implements AutoCloseable {

  private final WebClient webClient;
  private final String endpoint;
  private final AtomicReference<String> lastEventId = new AtomicReference<>();
  private volatile boolean running = true;

  // Event handlers
  private Function<ServerSentEvent<String>, Boolean> onEvent;
  private Consumer<Throwable> onError;
  private Runnable onConnect;

  public SseClient(WebClient.Builder webClientBuilder, String endpoint) {
    this.webClient = webClientBuilder.build();
    this.endpoint = endpoint;
  }

  /** Set event handler - return true to continue, false to terminate stream */
  public SseClient onEvent(Function<ServerSentEvent<String>, Boolean> handler) {
    this.onEvent = handler;
    return this;
  }

  /** Set error handler */
  public SseClient onError(Consumer<Throwable> handler) {
    this.onError = handler;
    return this;
  }

  /** Set connection handler */
  public SseClient onConnect(Runnable handler) {
    this.onConnect = handler;
    return this;
  }

  /** Start consuming SSE stream with automatic resumption */
  public void start() {

    webClient
        .post()
        .uri(endpoint)
        .accept(MediaType.TEXT_EVENT_STREAM)
        .headers(
            headers -> {
              // Resume from last event ID if available
              String eventId = lastEventId.get();
              if (eventId != null) {
                headers.set("Last-Event-ID", eventId);
              }
            })
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
        .doOnNext(event -> lastEventId.set(event.id()))
        .takeWhile(
            event -> {
              // Store event ID for resumption
              if (event.id() != null) {
                lastEventId.set(event.id());
              }

              // Call handler and check if we should continue
              if (onEvent != null) {
                return onEvent.apply(event);
              }
              return true; // Continue if no handler
            })
        .retryWhen(
            Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(30))
                .filter(throwable -> running))
        .subscribe(
            event -> {}, // Event handling done in takeWhile
            this::handleError);
  }

  /** Handle errors */
  private void handleError(Throwable error) {
    if (onError != null) {
      onError.accept(error);
    }
  }

  /** Get the last received event ID */
  public String getLastEventId() {
    return lastEventId.get();
  }

  /** Stop consuming and close */
  @Override
  public void close() {
    running = false;
  }
}
