package com.example.stream_04.orders.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.ConsumerBuilder;
import com.rabbitmq.stream.Environment;
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

  public void createStream(StreamId streamId) {
    this.environment.streamCreator().stream(streamId.fullName()).create();
  }

  public ConsumerBuilder createConsumer(StreamId streamId) {
    return this.environment.consumerBuilder().stream(streamId.fullName());
  }

  public StreamPublisher createStreamPublisher(StreamId streamId) {
    Producer producer = this.environment.producerBuilder().stream(streamId.fullName()).build();
    return new StreamPublisher(streamId, producer, objectMapper, 0);
  }
}
