package com.example.stream.resumable;

import com.example.sse.server.*;
import com.example.sse.server.InMemoryEventStreamRepository;
import com.example.stocks.StockPrice;
import com.example.stocks.StockPriceService;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Service
public class WatchListService {

  private final Logger logger = LoggerFactory.getLogger(WatchListService.class);
  private final TaskScheduler scheduler;
  private final StockPriceService stockPriceService;
  private final EventStreamRepository repository;

  public WatchListService(TaskScheduler scheduler, StockPriceService stockPriceService) {
    this.scheduler = scheduler;
    this.stockPriceService = stockPriceService;
    this.repository = new InMemoryEventStreamRepository();
  }

  public SseEmitter createWatchList(String symbol) {
    EventStream stream = repository.create();
    logger.info("Created new watchlist stream {} for symbol {}", stream.getStreamId(), symbol);
    return createEmitterForStream(stream, symbol);
  }

  public SseEmitter resumeWatchList(String lastEventIdStr) {
    EventId startingEventId = EventId.fromString(lastEventIdStr);
    StreamId streamId = startingEventId.streamId();

    EventStream stream =
        repository
            .get(streamId)
            .orElseThrow(() -> new IllegalArgumentException("Stream not found: " + streamId));

    SseEmitter emitter = createEmitter(stream);

    // Send missed events
    List<Event> missed = stream.readAfter(startingEventId);
    try {
      for (Event event : missed) {
        emitter.send(
            SseEmitter.event().id(event.id().toString()).name("stock-price").data(event.value()));
      }
    } catch (IOException e) {
      emitter.completeWithError(e);
      return emitter;
    }

    // Continue streaming new updates
    scheduleStockPriceUpdates(emitter, stream);
    return emitter;
  }

  private SseEmitter createEmitterForStream(EventStream stream, String symbol) {
    SseEmitter emitter = createEmitter(stream);
    scheduleStockPriceUpdates(emitter, stream, symbol);
    return emitter;
  }

  private SseEmitter createEmitter(EventStream stream) {
    SseEmitter emitter = new SseEmitter(0L);
    StreamId streamId = stream.getStreamId();

    emitter.onCompletion(() -> logger.info("Stream {} completed", streamId));
    emitter.onTimeout(() -> logger.info("Stream {} timed out", streamId));
    emitter.onError(e -> logger.error("Stream {} error", streamId, e));

    return emitter;
  }

  private void scheduleStockPriceUpdates(SseEmitter emitter, EventStream stream, String symbol) {
    scheduler.scheduleAtFixedRate(
        () -> {
          try {
            StockPrice price = stockPriceService.getCurrentPrice(symbol);
            String value = price.toString(); // Replace with JSON serialization if needed

            Event event = stream.append(value);

            SseEventBuilder builder =
                SseEmitter.event().id(event.id().toString()).name("stock-price").data(value);

            emitter.send(builder);
          } catch (Exception e) {
            logger.error("Error sending stock price update", e);
            emitter.completeWithError(e);
          }
        },
        Duration.ofSeconds(1));
  }

  private void scheduleStockPriceUpdates(SseEmitter emitter, EventStream stream) {
    scheduler.scheduleAtFixedRate(
        () -> {
          try {
            String value = "update at " + System.currentTimeMillis(); // Placeholder

            Event event = stream.append(value);

            SseEventBuilder builder =
                SseEmitter.event().id(event.id().toString()).name("stock-price").data(value);

            emitter.send(builder);
          } catch (Exception e) {
            logger.error("Error sending stock price update", e);
            emitter.completeWithError(e);
          }
        },
        Duration.ofSeconds(1));
  }
}
