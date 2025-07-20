package com.example.stream_04.orders.sse;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import org.springframework.stereotype.Service;

@Service
public class SseRabbitStream {

  private final Environment environment;

  public SseRabbitStream(Environment environment) {
    this.environment = environment;
  }

  public void createStream(StreamId streamId) {
    this.environment.streamCreator().stream(streamId.fullName()).create();
  }

  public Producer createProducer(StreamId streamId) {
    return this.environment.producerBuilder().stream(streamId.fullName()).build();
  }
}
