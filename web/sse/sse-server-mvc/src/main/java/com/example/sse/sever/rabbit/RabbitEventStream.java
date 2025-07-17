package com.example.sse.sever.rabbit;

import com.rabbitmq.stream.ConfirmationStatus;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class RabbitEventStream implements EventStream, AutoCloseable {

  private final Environment environment;
  private final StreamId streamId;
  private final Producer producer;
  private final AtomicLong nextId;
  private final List<com.rabbitmq.stream.Consumer> streamConsumers = new ArrayList<>();

  public RabbitEventStream(Environment environment, StreamId streamId, long nextId) {
    this.environment = environment;
    this.streamId = streamId;
    this.nextId = new AtomicLong(nextId);

    this.producer =
        this.environment.producerBuilder().stream(this.streamId.value().toString()).build();
  }

  @Override
  public boolean append(String value, long index) {
    long messageId = nextId.incrementAndGet();
    Message message =
        this.producer
            .messageBuilder()
            .properties()
            .messageId(messageId)
            .messageBuilder()
            .addData(value.getBytes(StandardCharsets.UTF_8))
            .build();

    CompletableFuture<ConfirmationStatus> status = new CompletableFuture<>();
    this.producer.send(
        message,
        confirmationStatus -> {
          status.complete(confirmationStatus);
        });

    try {
      ConfirmationStatus confirmationStatus = status.get();
      return confirmationStatus.isConfirmed();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public void emmitEvents(Optional<EventId> after, SseEmitter emitter) {
    final long startingIndex;
    if (after.isEmpty()) {
      startingIndex = -1;
    } else {
      startingIndex = after.get().index();
    }

    var streamConsumer =
        this.environment.consumerBuilder().stream(streamId.value().toString())
            .messageHandler(
                (context, message) -> {
                  long messageId = message.getProperties().getMessageIdAsLong();
                  if (messageId > startingIndex) {
                    EventId eventId = new EventId(this.streamId, messageId);
                    String payload = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
                    Event event = new Event(eventId, payload);
                  }
                })
            .build();

    this.streamConsumers.add(streamConsumer);
  }

  @Override
  public void consumeAfter(Optional<EventId> after, Consumer<Event> consumer) {
    final long startingIndex;
    if (after.isEmpty()) {
      startingIndex = -1;
    } else {
      startingIndex = after.get().index();
    }

    var streamConsumer =
        this.environment.consumerBuilder().stream(streamId.value().toString())
            .messageHandler(
                (context, message) -> {
                  long messageId = message.getProperties().getMessageIdAsLong();
                  if (messageId > startingIndex) {
                    EventId eventId = new EventId(this.streamId, messageId);
                    String payload = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
                    Event event = new Event(eventId, payload);
                    consumer.accept(event);
                  }
                })
            .build();

    this.streamConsumers.add(streamConsumer);
  }

  @Override
  public StreamId getStreamId() {
    return this.streamId;
  }

  @Override
  public void close() throws Exception {
    this.producer.close();
    this.streamConsumers.forEach(consumer -> consumer.close());
  }
}
