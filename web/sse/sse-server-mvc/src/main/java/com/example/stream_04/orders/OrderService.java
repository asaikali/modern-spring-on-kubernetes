package com.example.stream_04.orders;

import com.example.stream_02.prices.StockPrice;
import com.example.stream_02.prices.StockPriceService;
import com.example.stream_03.watchlist.EventStreamRepository;
import com.example.stream_03.watchlist.InMemoryEventStreamRepository;
import com.example.stream_03.watchlist.StreamId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.ConfirmationStatus;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.OffsetSpecification;
import com.rabbitmq.stream.Producer;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
  private final EventStreamRepository repository;
  private final ObjectMapper objectMapper;
  private final Environment environment;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

  public OrderService(
      StockPriceService stockPriceService, ObjectMapper objectMapper, Environment environment) {
    this.stockPriceService = stockPriceService;
    this.repository = new InMemoryEventStreamRepository();
    this.objectMapper = objectMapper;
    this.environment = environment;
  }

  record LastEventId(String symbol, UUID uuid, long counter) {

    public static LastEventId parse(String input) {
      String[] parts = input.split(":");
      if (parts.length != 3) {
        throw new IllegalArgumentException("Invalid format: expected <symbol>:<uuid>:<counter>");
      }

      String symbol = parts[0];
      UUID uuid = UUID.fromString(parts[1]);
      long counter;

      try {
        counter = Long.parseLong(parts[2]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid counter value: must be a long", e);
      }

      return new LastEventId(symbol, uuid, counter);
    }

    public String streamName() {
      return symbol +  ":" + uuid.toString();
    }
  }

  public SseEmitter resume(String lastEventId) {

    LastEventId eventId = LastEventId.parse(lastEventId);
    final SseEmitter emitter = new SseEmitter(0L);

    emitter.onCompletion(() -> logger.info("Stream {} completed", eventId.streamName()));
    emitter.onTimeout(() -> logger.info("Stream {} timed out", eventId.streamName()));
    emitter.onError(e -> logger.error("Stream {} error", eventId.streamName(), e));


     this.environment.consumerBuilder()
        .name("sse-stream")
         .offset(OffsetSpecification.first())
        .stream(eventId.streamName())
        .messageHandler( (context, message) -> {

          long messageId = message.getProperties().getMessageIdAsLong();
          if( messageId <= eventId.counter) return;

           final String body = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
           final String type = (String) message.getApplicationProperties().get("type");
           final long index = message.getProperties().getMessageIdAsLong();
           final String sseEventId = eventId.streamName() + ":" + index;

          final SseEventBuilder eventBuilder =
              SseEmitter.event()
                  .id(sseEventId)
                  .name(type)
                  .data(body);

          this.executor.execute(() -> {
              try {
                emitter.send(eventBuilder);
                if("order-completed".equals(type)){
                  context.consumer().close();
                  emitter.complete();
                }
              } catch (IOException e) {
                emitter.completeWithError(e);
                throw new RuntimeException(e);
              }
            });
        }).build();

    return  emitter;
  }

  public Response placeOrder(BuyOrder order) {

    StockPrice initialPrice = this.stockPriceService.getCurrentPrice(order.symbol());
    if (initialPrice.price().compareTo(order.maxPrice()) <= 0) {
      var orderCompleted = new OrderCompleted(order, initialPrice.price(), Instant.now());
      return new ImmediateResponse(orderCompleted);
    }

    // create a rabbitmq stream to back the response
    final String streamName = order.symbol() + ":" + UUID.randomUUID().toString();
    this.environment.streamCreator().stream(streamName).create();

    logger.info("Created new rabbitmq stream {} ", streamName);

    this.executor.execute(
        () -> {
          try {
            try (Producer producer =
                this.environment.producerBuilder().stream(streamName).build()) {
              long counter = 0;
              while (true) {
                // Poll current price
                StockPrice price = stockPriceService.getCurrentPrice(order.symbol());
                BigDecimal current = price.price();
                // Check if we should complete the order
                if (current.compareTo(order.maxPrice()) <= 0) {
                  logger.info("Order completed for {} at price {}", order.symbol(), current);
                  var result = new OrderCompleted(order, current, Instant.now());
                  var data = this.objectMapper.writeValueAsString(result);

                  // create a Message to put on the stream
                  Message message =
                      producer
                          .messageBuilder()
                          .addData(data.getBytes(StandardCharsets.UTF_8))
                          .properties()
                          .messageId(counter++)
                          .contentType("application/json")
                          .messageBuilder()
                          .applicationProperties()
                          .entry("type", "order-completed")
                          .messageBuilder()
                          .build();

                  // send the message to the stream and wait for confirmation
                  CompletableFuture<ConfirmationStatus> confirmationStatusFuture =
                      new CompletableFuture<>();
                  producer.send(
                      message,
                      confirmationStatus -> {
                        confirmationStatusFuture.complete(confirmationStatus);
                      });

                  ConfirmationStatus status = confirmationStatusFuture.join();
                  if (status.isConfirmed()) {
                    logger.info("Order completed for {} at price {}", order.symbol(), current);
                  } else {
                    // TODO retry the send to the RabbitMQ
                  }

                  break;
                }

                // emit order pending event
                var data = this.objectMapper.writeValueAsString(price);

                Message message =
                    producer
                        .messageBuilder()
                        .addData(data.getBytes(StandardCharsets.UTF_8))
                        .properties()
                        .messageId(counter++)
                        .contentType("application/json")
                        .messageBuilder()
                        .applicationProperties()
                        .entry("type", "order-pending")
                        .messageBuilder()
                        .build();

                // send the message to the stream and wait for confirmation
                CompletableFuture<ConfirmationStatus> confirmationStatusFuture =
                    new CompletableFuture<>();
                producer.send(
                    message,
                    confirmationStatus -> {
                      confirmationStatusFuture.complete(confirmationStatus);
                    });

                ConfirmationStatus status = confirmationStatusFuture.join();
                if (status.isConfirmed()) {
                  logger.info("Order completed for {} at price {}", order.symbol(), current);
                } else {
                  // TODO retry the send to the RabbitMQ
                }

                // Wait before polling again
                Thread.sleep(Duration.ofSeconds(1));
              }
            }
          } catch (Exception e) {
            logger.error("Error in price polling loop for stream {}", streamName, e);
          }
        });

    String lastEventId = streamName + ":0";
    return new EventualResponse(lastEventId);
  }
}
