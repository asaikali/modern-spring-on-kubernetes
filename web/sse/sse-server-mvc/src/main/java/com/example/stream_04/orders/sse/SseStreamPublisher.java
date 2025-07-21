package com.example.stream_04.orders.sse;

import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * Start from the begging a rabbitMQ stream and emmits via SSE any messages
 * that occurred after the lastEventId
 */
public class SseStreamPublisher implements MessageHandler {

  private Logger logger = LoggerFactory.getLogger(SseStreamPublisher.class);
  private final SseEmitter sseEmitter;
  private final SseEventId lastSseEventId;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
  private final String finalEventType;

  public SseStreamPublisher(SseEventId lastSseEventId, String finalEventType) {
    this.sseEmitter = new SseEmitter();
    this.lastSseEventId = lastSseEventId;
    this.finalEventType = finalEventType;

    sseEmitter.onCompletion(() -> logger.info("Stream {} completed", lastSseEventId.sseStreamId()));
    sseEmitter.onTimeout(() -> logger.info("Stream {} timed out", lastSseEventId.sseStreamId()));
    sseEmitter.onError(e -> logger.error("Stream {} error", lastSseEventId.sseStreamId(), e));
  }

  public SseEmitter getSseEmitter() {
    return sseEmitter;
  }

  @Override
  public void handle(Context context, Message message) {
    long messageId = message.getProperties().getMessageIdAsLong();
    if (messageId <= lastSseEventId.index()) return;

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
            if (finalEventType.equals(type)) {
              context.consumer().close();
              sseEmitter.complete();
            }
          } catch (IOException e) {
            sseEmitter.completeWithError(e);
            throw new RuntimeException(e);
          }
        });
  }
}
