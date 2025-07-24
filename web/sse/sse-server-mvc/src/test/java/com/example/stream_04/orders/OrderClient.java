package com.example.stream_04.orders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

import com.example.stream_04.orders.sse.ApiResponse;
import com.example.stream_04.orders.sse.ApiResponse.Immediate;
import com.example.stream_04.orders.sse.ApiResponse.Stream;
import com.example.stream_04.orders.sse.server.SseEventId;
import com.example.stream_04.orders.sse.server.SseStreamId;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class OrderClient {

  private static final Logger log = LoggerFactory.getLogger(OrderClient.class);
  private WebClient webClient;

  public OrderClient() {
    this.webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
  }

  public ApiResponse makeOrder(LimitOrderRequest order, boolean allowImmediate) {

    log.info("makeOrder( {}, allowImmediate {} ) ", order, allowImmediate);

    final CompletableFuture<ApiResponse> result = new CompletableFuture<>();
    this.webClient
        .post()
        .uri("/orders?allowImmediate={allowImmediate}", allowImmediate)
        .contentType(APPLICATION_JSON)
        .bodyValue(order)
        .accept(APPLICATION_JSON, TEXT_EVENT_STREAM)
        .exchangeToMono(
            response -> {
              log.info("Starting processing of server response");
              var contentType = response.headers().contentType().orElse(APPLICATION_OCTET_STREAM);

              if (APPLICATION_JSON.isCompatibleWith(contentType)) {
                log.info(" Processing application/json so returning ApiResponse.Immediate()");
                response
                    .bodyToMono(String.class)
                    .subscribe(
                        s -> {
                          log.info(" Response: {}", s);
                          result.complete(new Immediate(s));
                        });

                log.info(" Returning Mono.empty()");
                return Mono.empty();
              }

              if (TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
                log.info(" Processing text/evet-stream");
                final SseStreamId streamId = SseStreamId.generate("client");

                response
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                    .takeUntil(
                        sseEvent -> {
                          log.info(" takeUntil/event-type:  {}", sseEvent.event().toUpperCase());
                          return "order-executed".equals(sseEvent.event());
                        })
                    .index()
                    .subscribe(
                        tuple -> {
                          long index = tuple.getT1();
                          ServerSentEvent<String> sseEvent = tuple.getT2();

                          log.info(
                              " subscribe/index,event-type: {},{}",
                              index,
                              sseEvent.event().toUpperCase());
                          if (index == 0) {
                            if ("order-executed".equals(sseEvent.event())) {
                              log.info(" converted stream response to Immediate response");
                              ApiResponse apiResponse = new Immediate(sseEvent);
                              result.complete(apiResponse);
                            } else {
                              // blocking operation to create stream and publish first event
                              log.info(" text/event stream returned");
                              var lastEventId = SseEventId.firstEvent(streamId);
                              ApiResponse apiResponse = new Stream(lastEventId);
                              result.complete(apiResponse);
                              log.info(" publish first stream event stream {}", sseEvent);
                            }
                          } else {
                            log.info(" publishing event to stream {}", sseEvent);
                            // block operation to publish events
                          }
                        });
                return Mono.empty();
              }

              response.releaseBody();
              return Mono.error(
                  new IllegalStateException(" Unsupported content type: " + contentType));
            })
        .subscribe();

    return result.join();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    OrderClient client = new OrderClient();
    // try price of 111 to get an immediate fill
    // try a price of 101 or 100.5 to get a stream
    // even if order can be filled right away set allow immediate to false to force a stream to be
    // returned
    var streamOrder = new LimitOrderRequest("APPL", 100, BigDecimal.valueOf(101.5));
    ApiResponse response = client.makeOrder(streamOrder, true);
    switch (response) {
      case Immediate immediate:
        log.info("immediate response payload: {}", immediate.payload());
        break;
      case Stream stream:
        log.info("stream response streamId: {}", stream.lastEventId());
    }

    System.out.println("Press Enter to exit...");
    System.in.read();
  }
}
