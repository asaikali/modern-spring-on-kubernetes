package com.example.stream_04.orders.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.ConfirmationStatus;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Publishes events to a RabbitMQ stream and waits for each event to be
 * acknowdbelegd by rabbit because order and in-order deliver is very important
 * for this use case. In this example secanrio in this package we are
 * dealining with a an upstream service that produces a set of evevnts that
 * we want to capture, we want those events to be published in sequence to the
 * stream and not out of orde. this wyh we need for every send confirumation.
 */
public class RabbitStreamPublisher implements AutoCloseable {

  private final SseStreamId sseStreamId;
  private final Producer producer;
  private final AtomicLong index;
  private final ObjectMapper objectMapper;

  public RabbitStreamPublisher(
      SseStreamId sseStreamId, Producer producer, ObjectMapper objectMapper, long startIndex) {
    this.sseStreamId = Objects.requireNonNull(sseStreamId);
    this.producer = Objects.requireNonNull(producer);
    this.objectMapper = Objects.requireNonNull(objectMapper);
    this.index = new AtomicLong(startIndex);
  }

  public boolean publish(Object object, String type) {
    try {
      var bodyJson = this.objectMapper.writeValueAsString(object);

      // create a Message to put on the stream
      Message message =
          producer
              .messageBuilder()
              .addData(bodyJson.getBytes(StandardCharsets.UTF_8))
              .properties()
              .messageId(index.incrementAndGet())
              .contentType("application/json")
              .messageBuilder()
              .applicationProperties()
              .entry("type", type)
              .messageBuilder()
              .build();

      // send the message to the stream and wait for confirmation
      CompletableFuture<ConfirmationStatus> confirmationStatusFuture = new CompletableFuture<>();
      producer.send(
          message,
          confirmationStatus -> {
            confirmationStatusFuture.complete(confirmationStatus);
          });

      ConfirmationStatus status = confirmationStatusFuture.join();
      return status.isConfirmed();

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws Exception {
    this.producer.close();
  }
}
