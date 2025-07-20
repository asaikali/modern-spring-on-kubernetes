package com.example.stream_04.orders;

import com.example.stream_02.prices.StockPrice;
import com.example.stream_02.prices.StockPriceService;
import com.example.stream_04.orders.sse.EventId;
import com.example.stream_04.orders.sse.SseRabbitStream;
import com.example.stream_04.orders.sse.StreamId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.ConfirmationStatus;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.OffsetSpecification;
import com.rabbitmq.stream.Producer;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
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
  private final ObjectMapper objectMapper;
  private final SseRabbitStream sseRabbitStream;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

  public OrderService(
      StockPriceService stockPriceService,
      ObjectMapper objectMapper,
      Environment environment,
      SseRabbitStream sseRabbitStream) {
    this.stockPriceService = stockPriceService;
    this.objectMapper = objectMapper;
    this.sseRabbitStream = sseRabbitStream;
  }

  public SseEmitter resume(String lastEventId) {

    EventId eventId = EventId.fromString(lastEventId);
    final SseEmitter emitter = new SseEmitter(0L);

    emitter.onCompletion(() -> logger.info("Stream {} completed", eventId.streamId()));
    emitter.onTimeout(() -> logger.info("Stream {} timed out", eventId.streamId()));
    emitter.onError(e -> logger.error("Stream {} error", eventId.streamId(), e));

    this.sseRabbitStream.createConsumer(eventId.streamId())
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
    this.sseRabbitStream.createStream(streamId);

    logger.info("Created new rabbitmq stream {} ", streamId.fullName());

    this.executor.execute(
        () -> {
          try {
            try (Producer producer = this.sseRabbitStream.createProducer(streamId)) {
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
            logger.error("Error in price polling loop for stream {}", streamId.fullName(), e);
          }
        });

    var lastEventId = EventId.firstEvent(streamId);
    return new EventualResponse(lastEventId.toString());
  }
}
