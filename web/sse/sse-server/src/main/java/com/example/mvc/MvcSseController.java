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
   * Educational example demonstrating Server-Sent Events (SSE) with Spring MVC. This method creates
   * and emits a single comprehensive SSE event that showcases all available fields in the SSE
   * specification.
   *
   * @return SseEmitter configured to send one complete SSE event
   * @throws IOException if there's an error during JSON serialization
   */
  @GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamOneFullSpecEvent() throws IOException {

    //
    // create a single event that uses all the fields allowed by SSE specification
    //

    // Create sample user data as a Map for demonstration purposes
    var userMap = Map.of("firstName", "John", "lastName", "Doe");

    // Convert the user data to a pretty-printed JSON string using Jackson ObjectMapper
    // This demonstrates how to include formatted JSON data in SSE events
    var userJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

    // Build a comprehensive SSE event using the SseEventBuilder
    // This demonstrates all the standard SSE event fields
    SseEmitter.SseEventBuilder event =
        SseEmitter.event()
            // Comments: Optional field for adding explanatory text (not sent to client as data)
            .comment("This event demonstrates all the fields allowed by SSE events")
            .comment("payload is multi line notice how an SSE event can preserve formatting")
            .comment("Check the README.md file in the see folder for an explanation of SSE events")
            .comment("Event generated from a spring MVC controller /mvc/stream/one")
            .comment("Emitted from '" + Thread.currentThread().getName() + "' thread")

            // Reconnect time: Tells client how long to wait (in milliseconds) before reconnecting
            // if the connection is lost. Here set to 5 seconds.
            .reconnectTime(5000L)

            // Event ID: Unique identifier for this event, useful for client-side event tracking
            // and replay functionality
            .id("event-1")

            // Event name/type: Allows clients to listen for specific event types
            // Clients can use addEventListener('custom-event-type', handler)
            .name("custom-event-type")

            // Data fields: The actual payload sent to the client
            // Multiple .data() calls create multiple lines in the SSE event
            .data("Line 1 of data")
            .data("   Line 2 of data indentation is preserved") // Whitespace is preserved
            .data("   all lines in this event are treated as part of the paylod")
            .data("") // Empty lines are valid and preserved in SSE
            .data(userMap) // Objects are automatically serialized
            .data("") // Another empty line for formatting
            .data(userJson); // Pre-formatted JSON string

    //
    // Typically, an SSE stream emits multiple events from a background thread.
    // However, for educational purposes, this example focuses on the structure of a single event.
    // Therefore, we emit just one event directly from the Tomcat thread handling the request,
    // using Spring MVC's imperative SseEmitter.
    //

    // Create the SseEmitter instance that will handle the SSE connection
    final SseEmitter emitter = new SseEmitter();

    // Set up event handlers for different lifecycle events

    // onCompletion: Called when the emitter is properly closed/completed
    emitter.onCompletion(() -> logger.info("Emitter has been closed"));

    // onTimeout: Called when the emitter times out (default timeout or custom timeout)
    emitter.onTimeout(() -> logger.info("Emitter has timed out"));

    // onError: Called when an exception occurs during event emission
    emitter.onError((e) -> logger.error("Emitter has errored out with exception", e));

    // Send the constructed event to the client
    // This writes the event to the HTTP response stream in SSE format
    emitter.send(event);

    // Mark the emitter as complete, which closes the SSE connection
    // In a real streaming scenario, you wouldn't call complete() immediately
    emitter.complete();

    // Return the emitter to Spring MVC for response handling
    // Spring will manage the HTTP response and connection lifecycle
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
