package com.example.number.mvc;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class NumbersResponseEmitterStream {
  private static final Logger log = LoggerFactory.getLogger(NumbersResponseEmitterStream.class);
  private final ResponseBodyEmitter emitter = new ResponseBodyEmitter(0L);

  public ResponseBodyEmitter start() {
    emitter.onCompletion(() -> log.info("SSE stream completed"));
    emitter.onTimeout(() -> log.info("SSE stream timed out"));
    emitter.onError(throwable -> log.info("SSE stream error: {}", throwable.toString()));

    Executors.newVirtualThreadPerTaskExecutor().submit(this::publishEvents);
    return emitter;
  }

  private void publishEvents() {
    log.info("Publishing events");
    int counter = 0;

    try {
      while (true) {
        String event = String.format("id: %d\nevent: number\ndata: %d\n\n", counter, counter);
        emitter.send(event, MediaType.TEXT_PLAIN);
        counter++;
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (IOException ex) {
      log.info("Client disconnected: {}", ex.toString());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt(); // Important: restore interrupt status
      log.info("Stream interrupted");
    }
  }
}
