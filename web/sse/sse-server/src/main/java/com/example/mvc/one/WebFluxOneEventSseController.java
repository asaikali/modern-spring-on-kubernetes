package com.example.mvc.one;

import static org.springframework.http.MediaType.APPLICATION_JSON;

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
import reactor.core.publisher.Mono;

/**
 * Educational Spring WebFlux controller demonstrating Server-Sent Events (SSE).
 *
 * <p>Shows how to create and emit SSE events using WebFlux's reactive ServerSentEvent with
 * all available SSE specification fields.
 *
 * <p>Test with: {@code curl -N -H "Accept: text/event-stream" http://localhost:8080/webflux/stream/one}
 */
@RestController
public class WebFluxOneEventSseController {

  private final Logger logger = LoggerFactory.getLogger(WebFluxOneEventSseController.class);

  /**
   * Creates a single SSE event demonstrating all available SSE fields using WebFlux reactive streams.
   *
   * <p>This version uses WebFlux's ServerSentEvent builder to create a comprehensive event
   * that showcases all SSE specification fields in a reactive manner.
   *
   * @return Flux of ServerSentEvent containing one complete SSE event
   */
  @GetMapping(path = "/webflux/stream/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> streamOneFullSpecEvent() {

    logger.info("Creating WebFlux SSE event on thread: {}", Thread.currentThread().getName());

    return Mono.fromCallable(() -> {
      try {
        // ==================================================================================
        // STEP 1: Create sample data for demonstration
        // ==================================================================================

        // Create sample user data as a Map for demonstration purposes
        var userMap = Map.of("firstName", "John", "lastName", "Doe");

        // Convert the user data to a pretty-printed JSON string using Jackson ObjectMapper
        var userJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userMap);

        // ==================================================================================
        // STEP 2: Build comprehensive SSE event data
        // ==================================================================================

        // In WebFlux, we build the multi-line data payload as a single string
        // Each line will become a separate 'data:' line in the SSE stream
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("Line 1 of data\n");
        dataBuilder.append("   Line 2 of data indentation is preserved\n");
        dataBuilder.append("   all lines in this event are treated as part of the payload\n");
        dataBuilder.append("\n"); // Empty line
        dataBuilder.append(userMap.toString()).append("\n"); // Map as string
        dataBuilder.append("\n"); // Empty line
        
        // Note: WebFlux ServerSentEvent doesn't support multiple MediaType conversions
        // like Spring MVC's SseEmitter, so we manually serialize to JSON
        dataBuilder.append(userJson).append("\n");
        dataBuilder.append("if you see this the whole event made it");

        // ==================================================================================
        // STEP 3: Build ServerSentEvent with all SSE fields
        // ==================================================================================

        // Build a comprehensive SSE event using WebFlux's ServerSentEvent builder
        // This demonstrates all the standard SSE event fields
        ServerSentEvent<String> event = ServerSentEvent.<String>builder()
            // COMMENTS: Lines starting with ':' in SSE stream
            // Note: WebFlux ServerSentEvent doesn't support comments directly
            // Comments would need to be handled at a lower level or omitted
            
            // EVENT ID: 'id: event-1' in SSE stream
            .id("event-1")
            
            // EVENT NAME/TYPE: 'event: custom-event-type' in SSE stream
            .event("custom-event-type")
            
            // RETRY/RECONNECT TIME: 'retry: 5000' in SSE stream
            .retry(Duration.ofSeconds(5))
            
            // DATA: The actual payload sent to the client
            .data(dataBuilder.toString())
            
            .build();

        logger.info("WebFlux SSE event created successfully");
        return event;

      } catch (Exception e) {
        logger.error("Error creating WebFlux SSE event", e);
        throw new RuntimeException("Failed to create SSE event", e);
      }
    })
    .delayElement(Duration.ofSeconds(1)) // 1-second delay like the MVC version
    .doOnNext(event -> logger.info("Emitting WebFlux SSE event"))
    .flux(); // Convert Mono to Flux
  }
}
