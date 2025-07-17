package com.example.sse.sever.rabbit;

import com.rabbitmq.stream.Environment;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RabbitEventStreamManager implements EventStreamManager {

  private final Environment environment;

  public RabbitEventStreamManager(Environment environment) {
    this.environment = environment;
  }

  @Override
  public EventStream create() {
    return new RabbitEventStream(environment, StreamId.newStreamId(), 0);
  }

  @Override
  public Optional<EventStream> get(StreamId streamId) {
    if (this.environment.streamExists(streamId.value().toString())) {
      return Optional.of(new RabbitEventStream(environment, streamId, 0));
    }
    return Optional.empty();
  }

  @Override
  public void delete(StreamId streamId) {
    this.environment.deleteStream(streamId.value().toString());
  }
}
