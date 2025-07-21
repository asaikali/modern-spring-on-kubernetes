package com.example.stream_04.orders.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import com.rabbitmq.stream.Producer;
import org.springframework.stereotype.Service;

@Service
public class SseRabbitStreamManager {

  private final Environment environment;
  private final ObjectMapper objectMapper;

  public SseRabbitStreamManager(Environment environment, ObjectMapper objectMapper) {
    this.environment = environment;
    this.objectMapper = objectMapper;
  }

  private void createStream(StreamId streamId) {
    this.environment.streamCreator().stream(streamId.fullName()).create();
  }

  public SseStreamPublisher createSsePublisher(SseEventId lastSseEventId, String finalEventType) {
    this.createStream(lastSseEventId.streamId());
    SseStreamPublisher sseStreamPublisher = new SseStreamPublisher(lastSseEventId, finalEventType);
    this.environment.consumerBuilder().stream(lastSseEventId.streamId().fullName())
        .offset(OffsetSpecification.first())
        .messageHandler(sseStreamPublisher)
        .build();

    return sseStreamPublisher;
  }

  public RabbitStreamPublisher createStreamPublisher(StreamId streamId) {
    this.createStream(streamId);
    Producer producer = this.environment.producerBuilder().stream(streamId.fullName()).build();
    return new RabbitStreamPublisher(streamId, producer, objectMapper, 0);
  }
}
