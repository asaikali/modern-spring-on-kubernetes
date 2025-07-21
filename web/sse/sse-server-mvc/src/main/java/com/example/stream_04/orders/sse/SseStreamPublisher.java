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

public class SseStreamPublisher implements MessageHandler {

  private Logger logger = LoggerFactory.getLogger(SseStreamPublisher.class);
  private final SseEmitter sseEmitter;
  private final EventId lastEventId;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
  private final String finalEventType;

  public SseStreamPublisher(EventId lastEventId, String finalEventType) {
    this.sseEmitter = new SseEmitter();
    this.lastEventId = lastEventId;
    this.finalEventType = finalEventType;

    sseEmitter.onCompletion(() -> logger.info("Stream {} completed", lastEventId.streamId()));
    sseEmitter.onTimeout(() -> logger.info("Stream {} timed out", lastEventId.streamId()));
    sseEmitter.onError(e -> logger.error("Stream {} error", lastEventId.streamId(), e));
  }

  public SseEmitter getSseEmitter() {
    return sseEmitter;
  }

  @Override
  public void handle(Context context, Message message) {
    long messageId = message.getProperties().getMessageIdAsLong();
    if (messageId <= lastEventId.index()) return;

    final String body = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);
    final String type = (String) message.getApplicationProperties().get("type");
    final long index = message.getProperties().getMessageIdAsLong();
    final String sseEventId = lastEventId.withIndex(index).toString();

    final SseEventBuilder eventBuilder = SseEmitter.event().id(sseEventId).name(type).data(body);

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
