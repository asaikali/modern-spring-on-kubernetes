package com.example.orders;

import com.example.stream_04.orders.BuyOrder;
import com.example.stream_04.orders.EventualResponse;
import com.example.stream_04.orders.ImmediateResponse;
import com.example.stream_04.orders.OrderCompleted;
import com.example.stream_04.orders.Response;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class OrderClient {

  private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

  public Response placeOrder(BuyOrder order) {
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
                return response
                    .bodyToMono(OrderCompleted.class)
                    .map(ImmediateResponse::new)
                    .cast(Response.class);
              }

              if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
                return response
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                    .doOnNext(sse -> System.out.println("SSE: " + sse))
                    .takeUntil(sse -> sse.event().equals("result"))
                    .last() // Only take the last SSE before stream ends
                    .map(
                        sse -> {
                          String lastId = sse.id(); // Can also parse sse.data() if needed
                          return (Response) new EventualResponse(lastId);
                        });
              }

              response.releaseBody();
              return Mono.error(
                  new IllegalStateException("Unsupported content type: " + contentType));
            })
        .block();
  }

  public static void main(String[] args) {
    var immediate =
        """
        {
          "symbol": "AAPL",
          "quantity": "10",
          "maxPrice": 190.00
        }
        """; // new BuyOrder("APPL",100, BigDecimal.valueOf(200));
    var eventual = new BuyOrder("APPL", 100, BigDecimal.valueOf(101));

    var client = new OrderClient();

    Response response = client.placeOrder(new BuyOrder("APPL", 100, BigDecimal.valueOf(200)));
    if (response instanceof ImmediateResponse immediateResponse) {
      log.info(immediateResponse.getResult().toString());
    }

    response = client.placeOrder(new BuyOrder("APPL", 100, BigDecimal.valueOf(101)));
    if (response instanceof EventualResponse eventualResponse) {
      log.info("Stream last event id {} ", eventualResponse.getLastEvenId());
    }
  }
}
