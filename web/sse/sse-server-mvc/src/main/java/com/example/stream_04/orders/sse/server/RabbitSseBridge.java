package com.example.stream_04.orders.sse.server;

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
 * Consumes messages from a RabbitMQ stream and publishes them as Server-Sent Events (SSE).
 *
 * <p>This class implements the MessageHandler interface to process messages from a RabbitMQ stream.
 * It starts reading from the beginning of the stream and emits via SSE any messages that occurred
 * after the specified last event ID. It uses virtual threads to avoid blocking the RabbitMQ stream
 * client thread pool when sending SSE events.
 *
 * <p>The publisher can be configured to automatically complete the SSE stream when a message with a
 * specific event type (finalEventType) is received.
 */
public class RabbitSseBridge implements MessageHandler {

  private Logger logger = LoggerFactory.getLogger(RabbitSseBridge.class);
  private final SseEmitter sseEmitter;
  private final SseEventId lastSseEventId;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
  private final String finalEventType;

  /**
   * Creates a new RabbitSseBridge with the specified last event ID and final event type.
   *
   * <p>This constructor initializes a new SseEmitter and sets up completion, timeout, and error
   * callbacks with appropriate logging.
   *
   * @param lastSseEventId The ID of the last event that was processed, used to filter out
   *     already-processed messages from the stream
   * @param finalEventType The event type that, when received, will trigger the completion of the
   *     SSE stream
   */
  public RabbitSseBridge(SseEventId lastSseEventId, String finalEventType) {
    this.sseEmitter = new SseEmitter(0L);
    this.lastSseEventId = lastSseEventId;
    this.finalEventType = finalEventType;

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

  /**
   * Handles messages received from the RabbitMQ stream.
   *
   * <p>This method is called by the RabbitMQ stream consumer for each message in the stream. It
   * filters out messages that have already been processed (based on the lastSseEventId), converts
   * the message to an SSE event, and sends it to the client using a virtual thread to avoid
   * blocking the RabbitMQ stream client thread pool.
   *
   * <p>If a message with the configured finalEventType is received, the consumer is closed and the
   * SSE stream is completed.
   *
   * @param context The message context provided by the RabbitMQ stream consumer
   * @param message The message received from the RabbitMQ stream
   */
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
