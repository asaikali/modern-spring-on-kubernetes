package com.example.stream_01.one;

import com.fasterxml.jackson.core.JsonProcessingException;
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
class WebFluxOneEventSseController {

  private final Logger logger = LoggerFactory.getLogger(WebFluxOneEventSseController.class);

  /**
   * Creates a single SSE event demonstrating all available SSE fields using WebFlux reactive
   * streams. However, this is still a SpringMVC servlet-based controller, even though it is using
   * reactive types.
   *
   * @return Flux of ServerSentEvent containing one complete SSE event
   */
  @GetMapping(path = "/webflux/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> streamOneFullSpecEvent() throws JsonProcessingException {

    // Create the event directly - happens immediately when method is called
    ServerSentEvent<String> event = createSseEvent();

    return Flux.just(event)
        .doOnSubscribe(sub -> logger.info("SSE stream subscribed"))
        .doOnNext(e -> logger.info("Emitting SSE event " + Thread.currentThread().getName()))
        .doOnError(e -> logger.error("Error in the SSE stream", e))
        .doOnComplete(() -> logger.info("SSE stream completed"));
  }

  /**
   * Creates a comprehensive SSE event demonstrating all available SSE specification fields.
   *
   * @return ServerSentEvent with all SSE fields populated
   */
  private ServerSentEvent<String> createSseEvent() throws JsonProcessingException {
    // ==================================================================================
    // STEP 1: Create sample data for demonstration
    // ==================================================================================

    var userMap = Map.of("firstName", "John", "lastName", "Doe");
    var userJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

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
          last data line"""
            .formatted(userMap.toString(), userJson);

    // ==================================================================================
    // STEP 3: Build ServerSentEvent with ALL SSE fields including comments
    // ==================================================================================
    String comments =
        """
              This event demonstrates all the fields allowed by SSE events
              payload is multi line notice how an SSE event can preserve formatting
              Check the README.md file in the see folder for an explanation of SSE events
              Event generated from a WebFlux controller /webflux/stream/one
              Emitted from '%s' thread"""
            .formatted(Thread.currentThread().getName());

    return ServerSentEvent.<String>builder()
        .comment(comments)
        .id("event-1")
        .event("custom-event-type")
        .retry(Duration.ofSeconds(5))
        .data(eventData)
        .build();
  }
}
