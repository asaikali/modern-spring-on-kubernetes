package com.example.mvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@RestController
public class MvcSseController {

  private final Logger logger = LoggerFactory.getLogger(MvcSseController.class);

  /**
   * Endpoint: /mvc/stream/one Sends a single SSE event including all standard fields and a comment
   * with the MDN spec link.
   */
  @GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamOneFullSpecEvent() {
    final String path = "/mvc/stream/one";
    final SseEmitter emitter = new SseEmitter(60_000l);

    emitter.onCompletion(() -> logger.info("Emitter has been closed"));
    emitter.onTimeout(() -> logger.info("Emitter has timed out"));
    emitter.onError((e) -> logger.error("Emitter has errored out with exception", e));

    logger.info("GET " + path + " executing request");
    // Question: What exactly does spring do when the emitter is returend, how does it know to keep
    // the connection open,
    // whats the lifecycle contract between Spring MVC and the emitter

    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            () -> {
              try {
                logger.info(
                    "SseEmitter started on thread "
                        + Thread.currentThread().getName()
                        + " for path "
                        + path);
                SseEmitter.SseEventBuilder event =
                    SseEmitter.event()
                        .comment(
                            "SSE standard fields: https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#event_stream_format")
                        .reconnectTime(5000L)
                        .id("event-1")
                        .name("demo-event-type")
                        .data("This is the event data");

                emitter.send(event);
                emitter.complete();
              } catch (IOException ex) {
                emitter.completeWithError(ex);
              }
            },
            0,
            1,
            TimeUnit.SECONDS);

    return emitter;
  }

  // The infinite stream endpoint can be added here later
  @GetMapping(path = "/mvc/stream/infinite", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter infinite() {
    final String path = "/mvc/stream/infinite";
    logger.info("GET " + path + " executing request");

    // Set a long timeout or use a negative value for no timeout
    // java docs are unclear what value to set the timout value to
    final SseEmitter emitter = new SseEmitter(0L);

    // Add event handlers for debugging
    emitter.onCompletion(() -> logger.info("Infinite stream emitter has been closed"));
    emitter.onTimeout(() -> logger.info("Infinite stream emitter has timed out"));
    emitter.onError((e) -> logger.error("Infinite stream emitter has errored out with exception", e));
    AtomicInteger count = new AtomicInteger();
    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            () -> {
              try {
                SseEventBuilder builder =
                    SseEmitter.event()
                        .data(LocalDateTime.now())
                        .id(String.valueOf(count.getAndIncrement()))
                        .comment("example comment")
                        .reconnectTime(1000)
                        .name("mvc/stream/infinite");

                emitter.send(builder);
              } catch (Exception e) {
                emitter.completeWithError(e);
              }
            },
            0,
            1,
            TimeUnit.SECONDS);

    return emitter;
  }
}
