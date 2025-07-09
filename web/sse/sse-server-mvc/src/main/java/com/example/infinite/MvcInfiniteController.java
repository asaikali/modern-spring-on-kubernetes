package com.example.infinite;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@RestController
public class MvcInfiniteController {

  private final Logger logger = LoggerFactory.getLogger(MvcInfiniteController.class);

  private final TaskScheduler scheduler;

  public MvcInfiniteController(TaskScheduler scheduler) {
    this.scheduler = scheduler;
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
    emitter.onError(
        (e) -> logger.error("Infinite stream emitter has errored out with exception", e));
    AtomicInteger count = new AtomicInteger();

    this.scheduler.scheduleAtFixedRate(
        () -> {
          try {
            SseEventBuilder builder =
                SseEmitter.event()
                    .data(LocalDateTime.now())
                    .id(String.valueOf(count.getAndIncrement()))
                    .comment("Emitted from '" + Thread.currentThread().getName() + "' thread")
                    .reconnectTime(1000)
                    .name("mvc/stream/infinite");

            emitter.send(builder);
          } catch (Exception e) {
            emitter.completeWithError(e);
          }
        },
        Duration.ofSeconds(1));

    return emitter;
  }
}
