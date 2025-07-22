package com.example.stream_04.orders.sse.server;

import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler.Context;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

public class ServerSentEventPublisher implements AutoCloseable {

  private Logger logger = LoggerFactory.getLogger(RabbitSseBridge.class);
  private final SseEmitter sseEmitter;
  private final SseEventId lastSseEventId;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();


  public ServerSentEventPublisher(SseEventId lastEventId) {
    this.sseEmitter = new SseEmitter(0L);
    this.lastSseEventId = lastEventId;

    sseEmitter.onCompletion(
        () -> logger.info("Stream {} completed", lastSseEventId.createRabbitSseBridge()));
    sseEmitter.onTimeout(
        () -> logger.info("Stream {} timed out", lastSseEventId.createRabbitSseBridge()));
    sseEmitter.onError(
        e -> logger.error("Stream {} error", lastSseEventId.createRabbitSseBridge(), e));
  }

  /**
   * Returns the SseEmitter associated with this publisher.
   *
   * <p>This emitter can be used by Spring MVC controllers to stream Server-Sent Events to clients.
   *
   * @return The SseEmitter instance that will emit events from the RabbitMQ stream
   */
  public SseEmitter getSseEmitter() {
    return sseEmitter;
  }


  public void publish(Message message) {

    final String body = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
    final String type = (String) message.getApplicationProperties().get("type");
    final long index = message.getProperties().getMessageIdAsLong();
    final String sseEventId = lastSseEventId.withIndex(index).toString();

    final SseEventBuilder eventBuilder = SseEmitter.event().id(sseEventId).name(type).data(body);

    // We execute the sseEnd because this method we are is passed to the
    // RabbitMQ stream consumer on a callback. RabbitMQ streams assume non-
    // blocking behaviour this is why we need to create a virtual thread
    // to send the request on the outbound SSE stream. we don't want to
    // block the thread pool used by RabbitMQ streams client library
    this.executor.execute(
        () -> {
          try {
            sseEmitter.send(eventBuilder);
          } catch (IOException e) {
            sseEmitter.completeWithError(e);
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public void close() throws Exception {
    sseEmitter.complete();
  }
}
