package com.example.number.mvc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersStreamUsingBaseClass extends ResponseEmitterBasedSseStream {

  private static final Logger log = LoggerFactory.getLogger(NumbersStreamUsingBaseClass.class);

  @Override
  protected void publishEvents() throws IOException, InterruptedException {
    log.info("Publishing events");
    int counter = 0;

    while (running && !Thread.currentThread().isInterrupted()) {
      log.info("publishing event: id={}, data={}", counter, counter);

      sendSseEvent(String.valueOf(counter), "number", String.valueOf(counter));

      counter++;
      TimeUnit.SECONDS.sleep(1);
    }
  }
}
