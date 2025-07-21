package com.example.stream_04.orders.sse.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import com.rabbitmq.stream.Producer;
import org.springframework.stereotype.Service;

/**
 * Manages RabbitMQ streams for Server-Sent Events (SSE) communication.
 *
 * <p>This service provides functionality to create and manage RabbitMQ streams, as well as create
 * publishers for both producing messages to streams and consuming messages from streams for SSE
 * delivery to clients.
 *
 * <p>It acts as a central point for stream creation and management, ensuring that streams are
 * properly created before publishers or consumers are instantiated.
 */
@Service
public class SseRabbitStreamManager {

  private final Environment environment;
  private final ObjectMapper objectMapper;

  /**
   * Creates a new SseRabbitStreamManager with the specified RabbitMQ environment and ObjectMapper.
   *
   * @param environment The RabbitMQ stream environment used to create streams, producers, and
   *     consumers
   * @param objectMapper The Jackson ObjectMapper used for JSON serialization/deserialization
   */
  public SseRabbitStreamManager(Environment environment, ObjectMapper objectMapper) {
    this.environment = environment;
    this.objectMapper = objectMapper;
  }

  /**
   * Creates a RabbitMQ stream with the name derived from the provided SseStreamId.
   *
   * <p>If the stream already exists, this operation is idempotent and will not create a duplicate
   * stream.
   *
   * @param sseStreamId The identifier for the stream to be created
   */
  private void createStream(SseStreamId sseStreamId) {
    this.environment.streamCreator().stream(sseStreamId.fullName()).create();
  }

  /**
   * Creates an SSE publisher that consumes messages from a RabbitMQ stream and publishes them as
   * Server-Sent Events.
   *
   * <p>This method ensures the stream exists, creates a new RabbitSseBridge, and sets up a RabbitMQ
   * stream consumer that starts reading from the beginning of the stream and forwards messages to
   * the publisher.
   *
   * @param lastSseEventId The ID of the last event that was processed, used to filter out
   *     already-processed messages from the stream
   * @param finalEventType The event type that, when received, will trigger the completion of the
   *     SSE stream
   * @return A new RabbitSseBridge that can be used to stream events to clients
   */
  public RabbitSseBridge createSsePublisher(SseEventId lastSseEventId, String finalEventType) {
    this.createStream(lastSseEventId.sseStreamId());
    RabbitSseBridge rabbitSseBridge = new RabbitSseBridge(lastSseEventId, finalEventType);
    this.environment.consumerBuilder().stream(lastSseEventId.sseStreamId().fullName())
        .offset(OffsetSpecification.first())
        .messageHandler(rabbitSseBridge)
        .build();

    return rabbitSseBridge;
  }

  /**
   * Creates a publisher for sending messages to a RabbitMQ stream.
   *
   * <p>This method ensures the stream exists, creates a RabbitMQ producer for the stream, and
   * returns a RabbitStreamPublisher that can be used to publish messages to the stream. The
   * publisher starts with an initial message index of 0.
   *
   * @param sseStreamId The identifier for the stream to publish to
   * @return A new RabbitStreamPublisher that can be used to publish messages to the stream
   */
  public RabbitStreamPublisher createRabbitStreamPublisher(SseStreamId sseStreamId) {
    this.createStream(sseStreamId);
    Producer producer = this.environment.producerBuilder().stream(sseStreamId.fullName()).build();
    return new RabbitStreamPublisher(sseStreamId, producer, objectMapper, 0);
  }
}
