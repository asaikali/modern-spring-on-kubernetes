package com.example.one;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * Educational Spring MVC controller demonstrating Server-Sent Events (SSE) with background threads.
 *
 * <p>Shows how to create and emit SSE events using Spring's SseEmitter with background thread
 * execution.
 *
 * <p>Test with: {@code curl -N -H "Accept: text/event-stream" http://localhost:8080/mvc/stream/one}
 */
@RestController
public class MvcOneEventSseController {

  private final Logger logger = LoggerFactory.getLogger(MvcOneEventSseController.class);

  @Autowired private TaskScheduler taskScheduler;

  /**
   * Creates a single SSE event demonstrating all available SSE fields using a background thread.
   *
   * <p>This version uses a background thread to emit the event with a 1-second delay to demonstrate
   * proper SSE field usage.
   *
   * @return SseEmitter configured to send one complete SSE event from a background thread
   */
  @GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamOneFullSpecEvent() throws IOException {

    // Create the SseEmitter instance with custom timeout (30 seconds)
    // This gives us time to demonstrate the background thread execution
    final SseEmitter emitter = new SseEmitter(30_000L);

    // ==================================================================================
    // Set up lifecycle event handlers (before background processing)
    // ==================================================================================

    // onCompletion: Called when the emitter is properly closed/completed
    emitter.onCompletion(() -> logger.info("SSE connection completed successfully"));

    // onTimeout: Called when the emitter times out after 30 seconds
    emitter.onTimeout(() -> logger.info("SSE connection timed out"));

    // onError: Called when an exception occurs during event emission
    emitter.onError((e) -> logger.error("SSE connection error occurred", e));

    // ==================================================================================
    // Schedule background thread execution with 1-second delay
    // ==================================================================================

    // Use the configured TaskScheduler to execute SSE emission in background thread
    // This is the proper way to handle SSE in production - avoid blocking the request thread
    // The 1-second delay demonstrates scheduled execution and prevents connection issues
    taskScheduler.schedule(
        () -> {
          try {
            logger.info(
                "Starting SSE event emission from background thread: {}",
                Thread.currentThread().getName());

            // ==================================================================================
            // STEP 1: Create sample data for demonstration
            // ==================================================================================

            // Create sample user data as a Map for demonstration purposes
            var userMap = Map.of("firstName", "John", "lastName", "Doe");

            // Convert the user data to a pretty-printed JSON string using Jackson ObjectMapper
            var userJson =
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

            // ==================================================================================
            // STEP 2: Build a comprehensive SSE event using all available fields
            // ==================================================================================

            SseEventBuilder event =
                SseEmitter.event()
                    // COMMENTS: Lines starting with ':' in SSE stream
                    .comment("This event demonstrates all the fields allowed by SSE events")
                    .comment(
                        "payload is multi line notice how an SSE event can preserve formatting")
                    .comment(
                        "Check the README.md file in the see folder for an explanation of SSE events")
                    .comment("Event generated from a spring MVC controller /mvc/stream/one")
                    .comment("Emitted from '" + Thread.currentThread().getName() + "' thread")

                    // RETRY/RECONNECT TIME: 'retry: 5000' in SSE stream
                    .reconnectTime(5000L)

                    // EVENT ID: 'id: event-1' in SSE stream
                    .id("event-1")

                    // EVENT NAME/TYPE: 'event: custom-event-type' in SSE stream
                    .name("custom-event-type")

                    // DATA FIELDS: 'data: ...' lines in SSE stream
                    .data("Line 1 of data")
                    .data("   Line 2 of data indentation is preserved")
                    .data("   all lines in this event are treated as part of the payload")
                    .data("") // Empty lines are valid and preserved in SSE
                    .data(userMap) // SpringMVC picks Jackson converter for Map objects
                    .data("")
                    .data(
                        userMap, APPLICATION_JSON) // Explicitly specify APPLICATION_JSON MediaType
                    .data("")
                    .data(userJson) // Pre-formatted JSON string
                    .data("last data line");

            // ==================================================================================
            // STEP 3: Send the event from background thread
            // ==================================================================================

            // Send the event - this happens from the background thread
            emitter.send(event);
            logger.info("SSE event sent successfully");

            // ==================================================================================
            // STEP 4: Complete the connection
            // ==================================================================================

            // Complete immediately after sending the event
            emitter.complete();
            logger.info("SSE connection completed gracefully from background thread");

          } catch (IOException e) {
            logger.error("Error sending SSE event", e);
            emitter.completeWithError(e);
          } catch (Exception e) {
            logger.error("Unexpected error in SSE background thread", e);
            emitter.completeWithError(e);
          }
        },
        Instant.now().plus(Duration.ofSeconds(1)));

    // Return the emitter immediately to Spring MVC
    // The background thread will handle the actual event emission after 1 second
    logger.info("Returning SseEmitter to client, background thread will handle events in 1 second");
    return emitter;
  }
}
