package com.example.stream_04.orders;

import com.example.sse.server.*;
import com.example.stream_02.prices.StockPrice;
import com.example.stream_02.prices.StockPriceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Service
public class OrderService {

  private final Logger logger = LoggerFactory.getLogger(OrderService.class);
  private final StockPriceService stockPriceService;
  private final EventStreamRepository repository;
  private final ObjectMapper objectMapper;

  public OrderService(StockPriceService stockPriceService, ObjectMapper objectMapper) {
    this.stockPriceService = stockPriceService;
    this.repository = new InMemoryEventStreamRepository();
    this.objectMapper = objectMapper;
  }

  public Object placeOrder(BuyOrder order) {

    StockPrice initialPrice = this.stockPriceService.getCurrentPrice(order.symbol());
    if (initialPrice.price().compareTo(order.maxPrice()) <= 0) {
      return new OrderCompleted(order, initialPrice.price(), Instant.now());
    }

    EventStream stream = repository.create();
    StreamId streamId = stream.getStreamId();
    logger.info("Created new watchlist stream {} for symbol {}", streamId, order.symbol());

    SseEmitter emitter = new SseEmitter(0L);

    emitter.onCompletion(() -> logger.info("Stream {} completed", streamId));
    emitter.onTimeout(() -> logger.info("Stream {} timed out", streamId));
    emitter.onError(e -> logger.error("Stream {} error", streamId, e));

    Thread.startVirtualThread(
        () -> {
          try {
            while (true) {
              // Poll current price
              StockPrice price = stockPriceService.getCurrentPrice(order.symbol());
              BigDecimal current = price.price();
              // Check if we should complete the order
              if (current.compareTo(order.maxPrice()) <= 0) {
                logger.info("Order completed for {} at price {}", order.symbol(), current);
                var result = new OrderCompleted(order, current, Instant.now());
                var data = this.objectMapper.writeValueAsString(result);

                Event event = stream.append(data);

                SseEventBuilder builder =
                    SseEmitter.event().id(event.id().toString()).name("order-completed").data(data);

                emitter.send(builder);

                emitter.complete();
                break;
              }

              // emit order pending event
              var data = this.objectMapper.writeValueAsString(price);
              Event event = stream.append(data);

              SseEventBuilder builder =
                  SseEmitter.event().id(event.id().toString()).name("order-pending").data(data);

              emitter.send(builder);

              // Wait before polling again
              Thread.sleep(Duration.ofSeconds(1));
            }
          } catch (Exception e) {
            logger.error("Error in price polling loop for stream {}", streamId, e);
            emitter.completeWithError(e);
          }
        });

    return emitter;
  }
}
