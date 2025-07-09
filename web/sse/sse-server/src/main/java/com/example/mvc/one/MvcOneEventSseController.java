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
 * <p>This controller shows how to create and emit SSE events using Spring's SseEmitter.
 * SSE is a web standard that allows a server to push data to a client over a single
 * HTTP connection in real-time.
 *
 * <p><strong>To test this endpoint:</strong>
 * <ul>
 *   <li>Start the application and navigate to: GET /mvc/stream/one</li>
 *   <li>Use curl: {@code curl -N -H "Accept: text/event-stream" http://localhost:8080/mvc/stream/one}</li>
 *   <li>Open browser dev tools Network tab to see the SSE response format</li>
 * </ul>
 *
 * <p><strong>Expected SSE output format:</strong>
 * <pre>
 * : This event demonstrates all the fields allowed by SSE events
 * retry: 5000
 * id: event-1
 * event: custom-event-type
 * data: Line 1 of data
 * data:    Line 2 of data indentation is preserved
 * data:
 * data: {firstName=John, lastName=Doe}
 * data:
 * data: {
 * data:   "firstName" : "John",
 * data:   "lastName" : "Doe"
 * data: }
 * </pre>
 *
 * @author Educational Example
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events">MDN SSE Documentation</a>
 */
@RestController
public class MvcOneEventSseController {

  private final Logger logger = LoggerFactory.getLogger(MvcOneEventSseController.class);

  /**
   * Educational example demonstrating Server-Sent Events (SSE) with Spring MVC.
   *
   * <p>This method creates and emits a single comprehensive SSE event that showcases
   * all available fields in the SSE specification. In production, SSE streams typically
   * emit multiple events over time from background threads.
   *
   * <p><strong>SSE Event Fields Demonstrated:</strong>
   * <ul>
   *   <li><strong>Comments:</strong> Lines starting with ':' - not sent to client as data</li>
   *   <li><strong>Retry:</strong> Reconnection time in milliseconds</li>
   *   <li><strong>ID:</strong> Unique event identifier for client-side tracking</li>
   *   <li><strong>Event:</strong> Custom event type name</li>
   *   <li><strong>Data:</strong> The actual payload (can be multi-line)</li>
   * </ul>
   *
   * <p><strong>Client-side JavaScript example:</strong>
   * <pre>{@code
   * const eventSource = new EventSource('/mvc/stream/one');
   * eventSource.addEventListener('custom-event-type', function(event) {
   *   console.log('Received event:', event.data);
   *   console.log('Event ID:', event.lastEventId);
   * });
   * }</pre>
   *
   * @return SseEmitter configured to send one complete SSE event
   * @throws IOException if there's an error during JSON serialization
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
            .data(userMap) // SpringMVC guesses what media type to serialze too
            .data("")
            .data((Object)userMap, TEXT_EVENT_STREAM) // help spring MVC pick the right convertor
            .data("") // Another empty line for formatting
            .data(userJson); // Pre-formatted JSON string

    // ==================================================================================
    // STEP 3: Create and configure the SseEmitter
    // ==================================================================================

    // IMPORTANT: Production vs Educational Difference
    // Typically, an SSE stream emits multiple events from a background thread over time.
    // However, for educational purposes, this example focuses on the structure of a single event.
    // Therefore, we emit just one event directly from the Tomcat thread handling the request,
    // using Spring MVC's imperative SseEmitter.

    // Create the SseEmitter instance that will handle the SSE connection
    // SseEmitter manages the HTTP response and connection lifecycle
    final SseEmitter emitter = new SseEmitter(10_000L);

    // ==================================================================================
    // STEP 4: Set up lifecycle event handlers (optional but recommended)
    // ==================================================================================

    // onCompletion: Called when the emitter is properly closed/completed
    // - Triggered by emitter.complete() or client disconnection
    // - Good place for cleanup operations
    emitter.onCompletion(() -> logger.info("SSE connection completed successfully"));

    // onTimeout: Called when the emitter times out
    // - Default timeout is 30 seconds, can be customized: new SseEmitter(60000L)
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
    emitter.complete();

    // Return the emitter to Spring MVC for response handling
    // Spring will manage the HTTP response headers and connection lifecycle
    // Required headers: Content-Type: text/event-stream, Cache-Control: no-cache
    return emitter;
  }
}
