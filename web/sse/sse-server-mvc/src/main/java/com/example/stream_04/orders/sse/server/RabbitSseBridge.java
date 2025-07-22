package com.example.stream_04.orders.sse.server;

import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.MessageHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
public class RabbitSseBridge implements MessageHandler, AutoCloseable {

  private Logger logger = LoggerFactory.getLogger(RabbitSseBridge.class);
  private final SseEventId lastSseEventId;
  private final String finalEventType;
  private final ServerSentEventPublisher serverSentEventPublisher;
  private final AtomicBoolean isClosed = new AtomicBoolean(false);
  private final AtomicReference<Consumer> consumerRef = new AtomicReference<>();

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
  public RabbitSseBridge(SseEventId lastSseEventId, String finalEventType, ServerSentEventPublisher serverSentEventPublisher) {
    this.lastSseEventId = lastSseEventId;
    this.finalEventType = finalEventType;
    this.serverSentEventPublisher = serverSentEventPublisher;
  }

  public SseEmitter getSseEmitter() {
    return this.serverSentEventPublisher.getSseEmitter();
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

    consumerRef.compareAndSet(null, context.consumer());
    this.serverSentEventPublisher.publish(message);
    final String type = (String) message.getApplicationProperties().get("type");
    if(finalEventType.equals(type)) {
      try {
        this.close();
      } catch (Exception e) {
        logger.error("Error closing sse stream ",e);
        throw new RuntimeException(e);
      }
    };
  }

  @Override
  public void close() throws Exception {
    if(isClosed.compareAndSet(false, true)) {
      this.consumerRef.get().close();
      this.serverSentEventPublisher.close();
    }
  }
}
