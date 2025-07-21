package com.example.stream_04.orders;

import com.example.stream_04.orders.sse.ApiResponse;
import com.example.stream_04.orders.sse.ApiResponse.Immediate;
import com.example.stream_04.orders.sse.ApiResponse.Stream;
import com.example.stream_04.orders.sse.server.SseEventId;
import com.example.stream_04.orders.sse.server.SseStreamId;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class OrdersClient {

  private final Logger log = LoggerFactory.getLogger(OrdersClient.class);
  private WebClient webClient;

  public OrdersClient() {
    this.webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
  }

  public ApiResponse makeOrder(LimitOrderRequest order, boolean allowImediate) {

    final CompletableFuture<ApiResponse> result = new CompletableFuture<>();

    this.webClient
        .post()
        .uri("/orders?allowImmediate={allowImmediate}", allowImediate)
        .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_EVENT_STREAM)
        .exchangeToMono(
            response -> {
              MediaType contentType =
                  response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

              if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                 response.bodyToMono(String.class)
                    .doOnNext(s -> {
                      result.complete(new Immediate(s));
                    })
                    .then(); // Return Mono<Void>

                return Mono.empty();
              }

              if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {

                final SseStreamId streamId = SseStreamId.generate("client");

                response
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                    .index()
                    .takeUntil(
                        tuple -> {
                          long index = tuple.getT1();
                          ServerSentEvent<String> sseEvent = tuple.getT2();

                          if (index == 0) {
                            if ("order-executed".equals(sseEvent.event())) {
                              return false;
                            }
                          }
                          return true;
                        })
                    .subscribe(
                        tuple -> {
                          long index = tuple.getT1();
                          ServerSentEvent<String> sseEvent = tuple.getT2();
                          if (index == 0) {
                            if ("order-executed".equals(sseEvent.event())) {
                              ApiResponse apiResponse = new Immediate(sseEvent);
                              result.complete(apiResponse);
                            } else {
                              // blocking operation to create stream and publish first event

                              var lastEventId = SseEventId.firstEvent(streamId);
                              ApiResponse apiResponse = new Stream(lastEventId);
                              result.complete(apiResponse);
                              log.info("publish first stream event stream {}", sseEvent);
                            }
                          } else {
                            log.info("publishing event to stream {}", sseEvent);
                            // block operation to publish events
                          }
                        });
                return Mono.empty();
              }

              response.releaseBody();
              return Mono.error(
                  new IllegalStateException("Unsupported content type: " + contentType));
            })
        .subscribe();

    return result.join();
  }

  public static void main(String[] args) {
    OrdersClient client = new OrdersClient();
    var streamOrder = new LimitOrderRequest("APPL", 100, BigDecimal.valueOf(101));
    ApiResponse response = client.makeOrder( streamOrder, true);
    System.out.println(response);
  }
}
