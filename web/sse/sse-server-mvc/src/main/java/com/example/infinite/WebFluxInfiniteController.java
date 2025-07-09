package com.example.infinite;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Educational Spring WebFlux controller demonstrating infinite SSE streams with strongly typed
 * objects.
 *
 * <p>Streams real-time stock price updates using Jackson for JSON serialization. Focuses on the
 * core SSE fields: event name, id, and data using reactive streams.
 *
 * <p>Test with: {@code curl -N -H "Accept: text/event-stream"
 * http://localhost:8080/webflux/stream/infinite} or {@code curl -N -H "Accept: text/event-stream"
 * http://localhost:8080/webflux/stream/infinite?symbol=GOOGL}
 */
@RestController
public class WebFluxInfiniteController {

  private final Logger logger = LoggerFactory.getLogger(WebFluxInfiniteController.class);
  private final StockPriceService stockPriceService;

  public WebFluxInfiniteController(StockPriceService stockPriceService) {
    this.stockPriceService = stockPriceService;
  }

  /**
   * Infinite SSE stream endpoint that emits stock price updates every second using reactive
   * streams.
   *
   * <p>Demonstrates real-world usage with strongly typed Java objects serialized to JSON. Uses only
   * the essential SSE fields: event name, id, and data. Shows the reactive approach compared to the
   * imperative Spring MVC version.
   *
   * @param symbol Stock symbol to stream (defaults to AAPL)
   * @return Flux of ServerSentEvent that streams stock price updates indefinitely
   */
  @GetMapping(path = "/webflux/stream/infinite", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<StockPrice>> infinite(
      @RequestParam(defaultValue = "AAPL") String symbol) {

    logger.info("Starting WebFlux infinite stock price stream for symbol: {}", symbol);

    AtomicInteger eventId = new AtomicInteger(1);

    return Flux.interval(Duration.ofSeconds(1)) // Emit every second
        .flatMap(
            tick ->
                // Use reactive stock price service - non-blocking!
                stockPriceService.getCurrentPriceReactive(symbol))
        .map(
            stockPrice -> {
              // Build ServerSentEvent with strongly typed object
              return ServerSentEvent.<StockPrice>builder()
                  .id(String.valueOf(eventId.getAndIncrement())) // Sequential event ID
                  .event("stock-price") // Event type for client filtering
                  .data(stockPrice) // Jackson will serialize StockPrice to JSON
                  .build();
            })
        .doOnSubscribe(
            subscription ->
                logger.info("Client subscribed to WebFlux stock price stream for {}", symbol))
        .doOnCancel(() -> logger.info("Client cancelled WebFlux stock price stream for {}", symbol))
        .doOnError(
            error -> logger.error("Error in WebFlux stock price stream for {}", symbol, error))
        .doFinally(
            signalType ->
                logger.info(
                    "WebFlux stock price stream for {} completed with signal: {}",
                    symbol,
                    signalType));
  }
}
