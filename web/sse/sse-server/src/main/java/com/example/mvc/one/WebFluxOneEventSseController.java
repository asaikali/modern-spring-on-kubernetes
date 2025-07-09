package com.example.mvc.one;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Educational Spring WebFlux controller demonstrating Server-Sent Events (SSE).
 *
 * <p>Shows how to create and emit SSE events using WebFlux's reactive ServerSentEvent with all
 * available SSE specification fields.
 *
 * <p>Test with: {@code curl -N -H "Accept: text/event-stream"
 * http://localhost:8080/webflux/stream/one}
 */
@RestController
public class WebFluxOneEventSseController {

  private final Logger logger = LoggerFactory.getLogger(WebFluxOneEventSseController.class);

  /**
   * Creates a single SSE event demonstrating all available SSE fields using WebFlux reactive
   * streams.
   *
   * <p>This version creates the event directly and uses reactive operators for timing and logging.
   * Demonstrates all SSE specification fields including comments, id, event type, retry, and data.
   *
   * @return Flux of ServerSentEvent containing one complete SSE event
   */
  @GetMapping(path = "/webflux/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> streamOneFullSpecEvent() {

    logger.info("Creating WebFlux SSE event on thread: {}", Thread.currentThread().getName());

    // Create the event directly - happens immediately when method is called
    ServerSentEvent<String> event = createSseEvent();

    return Flux.just(event)
        .doOnNext(e -> logger.info("WebFlux SSE event created successfully"))
        .delayElements(Duration.ofSeconds(1))
        .doOnNext(e -> logger.info("Emitting WebFlux SSE event"));
  }

  /**
   * Creates a comprehensive SSE event demonstrating all available SSE specification fields.
   *
   * @return ServerSentEvent with all SSE fields populated
   */
  private ServerSentEvent<String> createSseEvent() {
    try {
      // ==================================================================================
      // STEP 1: Create sample data for demonstration
      // ==================================================================================

      var userMap = Map.of("firstName", "John", "lastName", "Doe");
      var userJson =
          new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

      // ==================================================================================
      // STEP 2: Build comprehensive SSE event data with multi-line content
      // ==================================================================================

      String eventData =
          """
          Line 1 of data
             Line 2 of data indentation is preserved
             all lines in this event are treated as part of the payload

          %s

          %s
          if you see this the whole event made it"""
              .formatted(userMap.toString(), userJson);

      // ==================================================================================
      // STEP 3: Build ServerSentEvent with ALL SSE fields including comments
      // ==================================================================================

      // COMMENTS: Lines starting with ':' in SSE stream
      // Spring's auto-formatting isn't working, so we'll bypass it entirely
      // and build the raw SSE string manually
      String comments = """
              This event demonstrates all the fields allowed by SSE events
              payload is multi line notice how an SSE event can preserve formatting
              Check the README.md file in the see folder for an explanation of SSE events
              Event generated from a WebFlux controller /webflux/stream/one
              Emitted from '%s' thread""".formatted(Thread.currentThread().getName());


      // Don't use .comment() since it's not working properly
      // We'll need to include comments in the raw response or find another approach

      var event = ServerSentEvent.<String>builder()
          // Skip comments for now - they're not working as expected

          // EVENT ID: 'id: event-1' in SSE stream
          .id("event-1")

          // EVENT NAME/TYPE: 'event: custom-event-type' in SSE stream
          .event("custom-event-type")

          // RETRY/RECONNECT TIME: 'retry: 5000' in SSE stream
          .retry(Duration.ofSeconds(5))
          .comment(comments)

          // DATA: The actual payload sent to the client
          // Multi-line data is automatically split into multiple 'data:' lines
          .data(eventData)
          .build();

      System.out.println(event.format());
      return event;
    } catch (Exception e) {
      logger.error("Error creating WebFlux SSE event", e);
      throw new RuntimeException("Failed to create SSE event", e);
    }
  }
}
