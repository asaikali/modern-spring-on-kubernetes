package com.example.stream_02.prices;

import com.example.stocks.StockPrice;
import com.example.stocks.StockPriceService;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * Educational Spring MVC controller demonstrating infinite SSE streams with strongly typed objects.
 *
 * <p>Streams real-time stock price updates using Jackson for JSON serialization. Focuses on the
 * core SSE fields: event name, id, and data.
 *
 * <p>/**
 *
 * <p>Test with: {@code curl -N -H "Accept: text/event-stream"
 * http://localhost:8080/mvc/stream/infinite} or {@code curl -N -H "Accept: text/event-stream"
 * http://localhost:8080/mvc/stream/infinite?symbol=GOOGL}
 */
@RestController
class MvcInfiniteController {

  private final Logger logger = LoggerFactory.getLogger(MvcInfiniteController.class);
  private final TaskScheduler scheduler;
  private final StockPriceService stockPriceService;

  public MvcInfiniteController(TaskScheduler scheduler, StockPriceService stockPriceService) {
    this.scheduler = scheduler;
    this.stockPriceService = stockPriceService;
  }

  /**
   * Infinite SSE stream endpoint that emits stock price updates every second.
   *
   * <p>Demonstrates real-world usage with strongly typed Java objects serialized to JSON. Uses only
   * the essential SSE fields: event name, id, and data.
   *
   * @return SseEmitter that streams stock price updates indefinitely
   */
  @GetMapping(path = "/mvc/stream/infinite", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter infinite(@RequestParam(defaultValue = "AAPL") String symbol) {
    // Set timeout to 0 for infinite stream (no timeout)
    // per the spec https://jakarta.ee/specifications/servlet/6.1/jakarta-servlet-spec-6.1
    //  If the timeout is not specified via the call to setTimeout, 30000 is used as the default. A
    // value of 0 or less indicates that the asynchronous operation will never time out.
    final SseEmitter emitter = new SseEmitter(0l);

    // Set up lifecycle event handlers
    emitter.onCompletion(() -> logger.info("Stock price stream completed"));
    emitter.onTimeout(() -> logger.info("Stock price stream timed out"));
    emitter.onError(e -> logger.error("Stock price stream error", e));

    AtomicInteger eventId = new AtomicInteger(1);

    // Schedule stock price updates every second
    scheduler.scheduleAtFixedRate(
        () -> {
          try {
            // Generate random stock price update
            StockPrice stockPrice = stockPriceService.getCurrentPrice(symbol);

            // Build SSE event with strongly typed object
            SseEventBuilder event =
                SseEmitter.event()
                    .id(String.valueOf(eventId.getAndIncrement())) // Sequential event ID
                    .name("stock-price") // Event type for client filtering
                    .data(stockPrice); // Jackson will serialize StockPrice to JSON

            emitter.send(event);
          } catch (Exception e) {
            logger.error("Error sending stock price update", e);
            emitter.completeWithError(e);
          }
        },
        Duration.ofSeconds(1));

    return emitter;
  }
}
