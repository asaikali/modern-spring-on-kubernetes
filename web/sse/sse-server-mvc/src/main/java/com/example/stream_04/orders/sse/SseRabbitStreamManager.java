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

  public Producer createProducer(StreamId streamId) {
    return this.environment.producerBuilder().stream(streamId.fullName()).build();
  }

  public ConsumerBuilder createConsumer(StreamId streamId) {
    return this.environment.consumerBuilder().stream(streamId.fullName());

  }
}
