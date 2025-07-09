package com.example.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
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
public class MvcSseController {

  private final Logger logger = LoggerFactory.getLogger(MvcSseController.class);

  private final TaskScheduler scheduler;

  public MvcSseController(TaskScheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Endpoint: /mvc/stream/one Sends a single SSE event including all standard fields and a comment
   * with the MDN spec link.
   */
  @GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamOneFullSpecEvent() throws IOException {

    //
    // create a single event that uses all the fields allowed by SSE specification
    //
    var userMap = Map.of("firstName", "John", "lastName", "Doe");
    var userJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

    SseEmitter.SseEventBuilder event =
        SseEmitter.event()
            .comment("This event demonstrates all the fields allowed by SSE events")
            .comment("payload is multi line notice how an SSE event can preserve formatting")
            .comment("Check the README.md file in the see folder for an explanation of SSE events")
            .comment("Event generated from a spring MVC controller /mvc/stream/one")
            .comment("Emitted from '" + Thread.currentThread().getName() + "' thread")
            .reconnectTime(5000L)
            .id("event-1")
            .name("custom-event-type")
            .data("Line 1 of data")
            .data("   Line 2 of data indentation is preserved")
            .data("   all lines in this event are treated as part of the paylod")
            .data("")
            .data(userMap)
            .data("")
            .data(userJson);

    //
    // Typically, an SSE stream emits multiple events from a background thread.
    // However, for educational purposes, this example focuses on the structure of a single event.
    // Therefore, we emit just one event directly from the Tomcat thread handling the request,
    // using Spring MVC's imperative SseEmitter.
    //
    final SseEmitter emitter = new SseEmitter();

    emitter.onCompletion(() -> logger.info("Emitter has been closed"));
    emitter.onTimeout(() -> logger.info("Emitter has timed out"));
    emitter.onError((e) -> logger.error("Emitter has errored out with exception", e));

    emitter.send(event);
    emitter.complete();

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
