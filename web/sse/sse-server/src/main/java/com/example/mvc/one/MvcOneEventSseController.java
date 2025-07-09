package com.example.mvc.one;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * Educational Spring MVC controller demonstrating Server-Sent Events (SSE).
 *
 * <p>Shows how to create and emit SSE events using Spring's SseEmitter with all available SSE
 * specification fields.
 *
 * <p>Test with: {@code curl -N -H "Accept: text/event-stream" http://localhost:8080/mvc/stream/one}
 */
@RestController
public class MvcOneEventSseController {

  private final Logger logger = LoggerFactory.getLogger(MvcOneEventSseController.class);

  /**
   * Creates a single SSE event demonstrating all available SSE fields.
   *
   * <p>In production, SSE streams typically emit multiple events over time from background threads.
   * This example emits one event for educational purposes.
   *
   * @return SseEmitter configured to send one complete SSE event
   */
  @GetMapping(path = "/mvc/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamOneFullSpecEvent() throws IOException {

    // ==================================================================================
    // STEP 1: Create sample data for demonstration
    // ==================================================================================

    // Create sample user data as a Map for demonstration purposes
    var userMap = Map.of("firstName", "John", "lastName", "Doe");

    // Convert the user data to a pretty-printed JSON string using Jackson ObjectMapper
    // This demonstrates how to include formatted JSON data in SSE events
    var userJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

    // ==================================================================================
    // STEP 2: Build a comprehensive SSE event using all available fields
    // ==================================================================================

    // Build a comprehensive SSE event using the SseEventBuilder
    // This demonstrates all the standard SSE event fields
    SseEventBuilder event =
        SseEmitter.event()
            // COMMENTS: Lines starting with ':' in SSE stream
            // - Not sent to client as data, used for debugging/documentation
            // - Multiple comments are allowed and each creates a separate line
            .comment("This event demonstrates all the fields allowed by SSE events")
            .comment("payload is multi line notice how an SSE event can preserve formatting")
            .comment("Check the README.md file in the see folder for an explanation of SSE events")
            .comment("Event generated from a spring MVC controller /mvc/stream/one")
            .comment("Emitted from '" + Thread.currentThread().getName() + "' thread")

            // RETRY/RECONNECT TIME: 'retry: 5000' in SSE stream
            // - Tells client how long to wait (in milliseconds) before reconnecting
            // - Only used when connection is lost, not for normal operation
            // - Here set to 5 seconds (5000ms)
            .reconnectTime(5000L)

            // EVENT ID: 'id: event-1' in SSE stream
            // - Unique identifier for this event, useful for client-side event tracking
            // - Enables replay functionality - client can request events after specific ID
            // - Accessible in JavaScript as event.lastEventId
            .id("event-1")

            // EVENT NAME/TYPE: 'event: custom-event-type' in SSE stream
            // - Allows clients to listen for specific event types
            // - Default type is 'message' if not specified
            // - Client can use: addEventListener('custom-event-type', handler)
            .name("custom-event-type")

            // DATA FIELDS: 'data: ...' lines in SSE stream
            // - The actual payload sent to the client
            // - Multiple .data() calls create multiple 'data:' lines
            // - All data lines are combined into event.data on client side
            .data("Line 1 of data")
            .data("   Line 2 of data indentation is preserved") // Whitespace is preserved
            .data("   all lines in this event are treated as part of the paylod")
            .data("") // Empty lines are valid and preserved in SSE (creates blank data: line)
            .data(userMap) // SpringMVC guesses what media type to serialize to
            .data("")
            .data((Object) userMap, TEXT_EVENT_STREAM) // Help Spring MVC pick the right converter
            .data("") // Another empty line for formatting
            .data(userJson) // Pre-formatted JSON string
            .data("if you see this the whole event made it"); // visual market to show end of event


    // ==================================================================================
    // STEP 3: Create and configure the SseEmitter
    // ==================================================================================

    // IMPORTANT: Production vs Educational Difference
    // Typically, an SSE stream emits multiple events from a background thread over time.
    // However, for educational purposes, this example focuses on the structure of a single event.
    // Therefore, we emit just one event directly from the Tomcat thread handling the request,
    // using Spring MVC's imperative SseEmitter.

    // Create the SseEmitter instance with custom timeout (10 seconds)
    // SseEmitter manages the HTTP response and connection lifecycle
    final SseEmitter emitter = new SseEmitter(10_000L);

    // ==================================================================================
    // STEP 4: Set up lifecycle event handlers (optional but recommended)
    // ==================================================================================

    // onCompletion: Called when the emitter is properly closed/completed
    // - Triggered by emitter.complete() or client disconnection
    // - Good place for cleanup operations
    emitter.onCompletion(() -> logger.info("SSE connection completed successfully"));

    // onTimeout: Called when the emitter times out after 10 seconds
    // - Automatic cleanup happens after timeout
    emitter.onTimeout(() -> logger.info("SSE connection timed out"));

    // onError: Called when an exception occurs during event emission
    // - Important for debugging connection issues
    // - Emitter is automatically completed when error occurs
    emitter.onError((e) -> logger.error("SSE connection error occurred", e));

    // ==================================================================================
    // STEP 5: Send the event and complete the connection
    // ==================================================================================

    // Send the constructed event to the client
    // This writes the event to the HTTP response stream in proper SSE format
    // The event is immediately flushed to the client
    emitter.send(event);


    // Mark the emitter as complete, which closes the SSE connection
    // In a real streaming scenario, you wouldn't call complete() immediately
    // Instead, you'd keep the connection open and send events over time
    // we are not calling it so we don't generate a network error
    // emitter.complete();

    // Return the emitter to Spring MVC for response handling
    // Spring will manage the HTTP response headers and connection lifecycle
    return emitter;
  }
}
