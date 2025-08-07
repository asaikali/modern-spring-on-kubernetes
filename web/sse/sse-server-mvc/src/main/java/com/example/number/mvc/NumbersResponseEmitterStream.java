package com.example.number.mvc;

import java.io.IOException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class NumbersResponseEmitterStream {
  private static final Logger log = LoggerFactory.getLogger(NumbersResponseEmitterStream.class);
  private final ResponseBodyEmitter emitter = new ResponseBodyEmitter(0L);
  private volatile boolean clientConnected = true;

  public ResponseBodyEmitter start() {
    // Register callback for normal completion
    emitter.onCompletion(() -> {
      log.info("SSE stream completed - client disconnected normally");
      clientConnected = false;
    });

    // Register callback for timeout
    emitter.onTimeout(() -> {
      log.info("SSE stream timed out");
      clientConnected = false;
    });

    // Register callback for errors (abrupt disconnection)
    emitter.onError(throwable -> {
      log.info("SSE stream error (client likely disconnected abruptly): {}",
          throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
      clientConnected = false;
    });

    // Start publishing in a separate thread
    Executors.newVirtualThreadPerTaskExecutor().submit(this::publishEvents);
    return emitter;
  }

  private void publishEvents() {
    log.info("Publishing events started");
    int counter = 0;
    long startTime = System.currentTimeMillis();

    try {
      // Continue sending while client is connected
      while (clientConnected) {
        String event = String.format("id: %d\nevent: number\ndata: %d\n\n", counter, counter);
        emitter.send(event, MediaType.TEXT_PLAIN);
        counter++;

        // Log progress periodically (every 10k events)
        if (counter % 10000 == 0) {
          long elapsed = System.currentTimeMillis() - startTime;
          log.debug("Still sending, counter={}, elapsed={}ms, rate={} events/sec",
              counter, elapsed, (counter * 1000L) / elapsed);
        }
      }

      // If we get here, clientConnected was set to false by a callback
      log.info("Client disconnected (detected via callback), sent {} events", counter);

    } catch (IOException ex) {
      // IOException indicates network/write failure
      // Spring will handle completion - we don't need to call complete()
      log.info("IOException detected disconnection: counter={}, exception={}",
          counter, ex.getClass().getSimpleName() + ": " + ex.getMessage());
    } catch (IllegalStateException ex) {
      // IllegalStateException thrown when emitter is already complete
      log.info("Emitter already completed: counter={}, exception={}",
          counter, ex.getMessage());
    } catch (Exception ex) {
      // Catch any unexpected exceptions
      log.error("Unexpected exception: counter={}", counter, ex);
    } finally {
      // Just log final statistics - no need to call complete()
      // Spring handles completion automatically when IOException occurs
      long totalTime = System.currentTimeMillis() - startTime;
      log.info("Publishing finished. Total events: {}, Time: {}ms, Rate: {} events/sec",
          counter, totalTime,
          totalTime > 0 ? (counter * 1000L) / totalTime : 0);
    }
  }
}
