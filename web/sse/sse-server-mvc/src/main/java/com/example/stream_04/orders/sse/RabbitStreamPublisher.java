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
 * Publishes events to a RabbitMQ stream and waits for each event to be acknowledged by RabbitMQ.
 * Order and in-order delivery is very important for this use case.
 *
 * <p>In this example scenario, we are dealing with an upstream service that produces a set of
 * events that we want to capture. We want those events to be published in sequence to the stream
 * and not out of order. This is why we need confirmation for every send operation.
 */
public class RabbitStreamPublisher implements AutoCloseable {

  private final SseStreamId sseStreamId;
  private final Producer producer;
  private final AtomicLong index;
  private final ObjectMapper objectMapper;

  /**
   * Creates a new RabbitStreamPublisher for publishing events to a specific stream.
   *
   * @param sseStreamId The unique identifier for the stream to publish to
   * @param producer The RabbitMQ stream producer used to send messages
   * @param objectMapper The Jackson ObjectMapper used to serialize objects to JSON
   * @param startIndex The initial index value for message sequencing
   * @throws NullPointerException if any of the required parameters are null
   */
  public RabbitStreamPublisher(
      SseStreamId sseStreamId, Producer producer, ObjectMapper objectMapper, long startIndex) {
    this.sseStreamId = Objects.requireNonNull(sseStreamId);
    this.producer = Objects.requireNonNull(producer);
    this.objectMapper = Objects.requireNonNull(objectMapper);
    this.index = new AtomicLong(startIndex);
  }

  /**
   * Publishes an object as a JSON message to the RabbitMQ stream.
   *
   * <p>This method serializes the provided object to JSON, creates a message with appropriate
   * properties, sends it to the stream, and waits for confirmation from RabbitMQ before returning.
   *
   * @param object The object to be serialized and published
   * @param type The type identifier for the message, stored in application properties
   * @return {@code true} if the message was successfully confirmed by RabbitMQ, {@code false}
   *     otherwise
   * @throws RuntimeException if serialization of the object to JSON fails
   */
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

  /**
   * Closes the RabbitMQ producer associated with this publisher.
   *
   * <p>This method should be called when the publisher is no longer needed to release resources and
   * properly clean up the connection to RabbitMQ.
   *
   * @throws Exception if an error occurs while closing the producer
   */
  @Override
  public void close() throws Exception {
    this.producer.close();
  }
}
