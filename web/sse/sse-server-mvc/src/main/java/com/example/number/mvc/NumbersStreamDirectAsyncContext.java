package com.example.number.mvc;

import jakarta.servlet.AsyncContext;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersStreamDirectAsyncContext extends DirectAsyncContextAbstractSseStream {

  private static final Logger log = LoggerFactory.getLogger(NumbersStreamDirectAsyncContext.class);

  public NumbersStreamDirectAsyncContext(AsyncContext asyncContext) {
    super(asyncContext);
  }

  @Override
  protected void publishEvents(OutputStream outputStream) throws IOException {
    log.info("Publishing events");
    int counter = 0;
    while (true) {
      String event =
          """
                    id: %d
                    event: number
                    data: %d

                    """
              .formatted(counter, counter);

      log.info("publishing event: \n{}", event);
      sendEvent(outputStream, event);
      pause(Duration.ofSeconds(1));
      counter++;
    }
  }
}
