package com.example.test;

import com.example.stocks.StockPrice;
import com.example.stocks.StockPriceService;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@RestController
public class RedirectStreamController {

  private final Logger logger = LoggerFactory.getLogger(RedirectStreamController.class);
  private final TaskScheduler scheduler;
  private final StockPriceService stockPriceService;

  public RedirectStreamController(TaskScheduler scheduler, StockPriceService stockPriceService) {
    this.scheduler = scheduler;
    this.stockPriceService = stockPriceService;
  }

  @GetMapping(path = "/test/redirect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<Void> redirect(@RequestParam(defaultValue = "AAPL") String symbol) {
    String targetUrl = "/test/stream/stocks/stream?symbol=" + symbol;
    return ResponseEntity.status(302).header("Location", targetUrl).build();
  }

  @GetMapping(path = "/test/stream/stocks", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter stockPrices(@RequestParam(defaultValue = "AAPL") String symbol) {
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
