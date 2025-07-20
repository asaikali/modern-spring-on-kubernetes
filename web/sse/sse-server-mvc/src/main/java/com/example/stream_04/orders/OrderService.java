package com.example.stream_04.orders;

import com.example.stream_02.prices.StockPrice;
import com.example.stream_02.prices.StockPriceService;
import com.example.stream_04.orders.sse.EventId;
import com.example.stream_04.orders.sse.SseRabbitStreamManager;
import com.example.stream_04.orders.sse.StreamId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Service
public class OrderService {

  private final Logger logger = LoggerFactory.getLogger(OrderService.class);
  private final StockPriceService stockPriceService;
  private final ObjectMapper objectMapper;
  private final SseRabbitStreamManager sseRabbitStreamManager;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

  public OrderService(
      StockPriceService stockPriceService,
      ObjectMapper objectMapper,
      Environment environment,
      SseRabbitStreamManager sseRabbitStreamManager) {
    this.stockPriceService = stockPriceService;
    this.objectMapper = objectMapper;
    this.sseRabbitStreamManager = sseRabbitStreamManager;
  }

  public SseEmitter resume(String lastEventId) {

    EventId eventId = EventId.fromString(lastEventId);
    final SseEmitter emitter = new SseEmitter(0L);

    emitter.onCompletion(() -> logger.info("Stream {} completed", eventId.streamId()));
    emitter.onTimeout(() -> logger.info("Stream {} timed out", eventId.streamId()));
    emitter.onError(e -> logger.error("Stream {} error", eventId.streamId(), e));

    this.sseRabbitStreamManager
        .createConsumer(eventId.streamId())
        .offset(OffsetSpecification.first())
        .stream(eventId.streamId().fullName())
        .messageHandler(
            (context, message) -> {
              long messageId = message.getProperties().getMessageIdAsLong();
              if (messageId <= eventId.index()) return;

              final String body = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
              final String type = (String) message.getApplicationProperties().get("type");
              final long index = message.getProperties().getMessageIdAsLong();
              final String sseEventId = eventId.withIndex(index).toString();

              final SseEventBuilder eventBuilder =
                  SseEmitter.event().id(sseEventId).name(type).data(body);

              this.executor.execute(
                  () -> {
                    try {
                      emitter.send(eventBuilder);
                      if ("order-completed".equals(type)) {
                        context.consumer().close();
                        emitter.complete();
                      }
                    } catch (IOException e) {
                      emitter.completeWithError(e);
                      throw new RuntimeException(e);
                    }
                  });
            })
        .build();

    return emitter;
  }

  public Response placeOrder(BuyOrder order) {

    StockPrice initialPrice = this.stockPriceService.getCurrentPrice(order.symbol());
    if (initialPrice.price().compareTo(order.maxPrice()) <= 0) {
      var orderCompleted = new OrderCompleted(order, initialPrice.price(), Instant.now());
      return new ImmediateResponse(orderCompleted);
    }

    // create a rabbitmq stream to back the response
    StreamId streamId = StreamId.generate(order.symbol().toLowerCase());
    this.sseRabbitStreamManager.createStream(streamId);

    logger.info("Created new rabbitmq stream {} ", streamId.fullName());

    this.executor.execute(
        () -> {
          try {

            try (var streamPublisher =
                this.sseRabbitStreamManager.createStreamPublisher(streamId)) {
              long counter = 0;
              while (true) {
                // Poll current price
                StockPrice price = stockPriceService.getCurrentPrice(order.symbol());
                BigDecimal current = price.price();
                // Check if we should complete the order
                if (current.compareTo(order.maxPrice()) <= 0) {
                  logger.info("Order completed for {} at price {}", order.symbol(), current);
                  var orderCompleted = new OrderCompleted(order, current, Instant.now());
                  boolean published = streamPublisher.publish(orderCompleted, "order-completed");
                  if (published) {
                    logger.info("Order completed for {} at price {}", order.symbol(), current);
                  } else {
                    // TODO retry the send to the RabbitMQ
                  }
                  break;
                }

                boolean published = streamPublisher.publish(price, "order-pending");
                if (published) {
                  logger.info("Order completed for {} at price {}", order.symbol(), current);
                } else {
                  // TODO retry the send to the RabbitMQ
                }

                // Wait before polling again
                Thread.sleep(Duration.ofSeconds(1));
              }
            }
          } catch (Exception e) {
            logger.error("Error in price polling loop for stream {}", streamId.fullName(), e);
          }
        });

    var lastEventId = EventId.firstEvent(streamId);
    return new EventualResponse(lastEventId.toString());
  }
}
