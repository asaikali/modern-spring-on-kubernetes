package com.example.stream_04.orders.sse;

import com.rabbitmq.stream.Environment;
import org.springframework.stereotype.Service;

@Service
public class RabbitStream {

  private final Environment environment;

  public RabbitStream(Environment environment) {
    this.environment = environment;
  }

  public void createStream(String name) {
    this.environment.streamCreator().stream(name);
  }

  public void createStream() {}
}
