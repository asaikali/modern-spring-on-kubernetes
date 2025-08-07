package com.example.number.mvc;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DirectAsyncContextAbstractSseStream {

  private static final Logger log =
      LoggerFactory.getLogger(DirectAsyncContextAbstractSseStream.class);

  protected final AsyncContext asyncContext;

  public DirectAsyncContextAbstractSseStream(AsyncContext asyncContext) {
    this.asyncContext = asyncContext;
    this.asyncContext.setTimeout(0);
  }

  public final void start() {
    /*
     * Starts the SSE stream in a virtual thread using Tomcat's AsyncContext.
     *
     * The flow is as follows:
     * 1. A virtual thread is launched to handle the SSE stream independently.
     * 2. Response headers are prepared to indicate an SSE stream (`text/event-stream`).
     * 3. The response OutputStream is opened and passed to the abstract `publishEvents()` method,
     *    which performs blocking writes of SSE-formatted events.
     * 4. If the client disconnects (e.g., pressing stop in IntelliJ, closing browser tab),
     *    Tomcat will throw a `ClientAbortException`, which we catch explicitly to indicate a normal termination.
     * 5. Other exceptions (IO or runtime) are also caught and logged.
     * 6. The `asyncContext.complete()` is called to signal the servlet container that async work is finished.
     *    If the response is already closed (e.g., after a disconnect), `complete()` may throw
     *    an `IllegalStateException`, which we catch and log quietly.
     */
    Thread.startVirtualThread(
        () -> {
          HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
          HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

          try {
            prepareResponse(response);
            try (OutputStream outputStream = response.getOutputStream()) {
              publishEvents(outputStream);
            }
          } catch (org.apache.catalina.connector.ClientAbortException e) {
            // Expected when client disconnects (e.g., closes browser or stops request)
            log.info("Client disconnected: {}", e.getMessage());
          } catch (IOException | RuntimeException e) {
            // Unexpected error while writing to client
            log.warn("SSE stream error: {}", e.getMessage(), e);
          } finally {
            try {
              asyncContext.complete();
            } catch (IllegalStateException e) {
              // Safe to ignore: Tomcat has already closed the response
              log.debug("AsyncContext already completed: {}", e.getMessage());
            }
          }
        });
  }

  /** Override this to customize HTTP headers before writing begins. */
  protected void prepareResponse(HttpServletResponse response) {
    response.setContentType("text/event-stream");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Connection", "keep-alive");
  }

  /** Subclasses implement this to emit one or more SSE events. */
  protected abstract void publishEvents(OutputStream outputStream) throws IOException;

  /** Sends a raw SSE event block. Caller is responsible for all lines and line endings. */
  protected void sendEvent(OutputStream outputStream, String sseFormattedBlock) throws IOException {
    checkInterruption();
    outputStream.write(sseFormattedBlock.getBytes(StandardCharsets.UTF_8));
    outputStream.flush(); // IOException will occur here if client disconnected
  }

  protected void pause(Duration duration) {
    checkInterruption();
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted during pause", e);
    }
  }

  protected void checkInterruption() {
    if (Thread.currentThread().isInterrupted()) {
      throw new RuntimeException("Stream interrupted");
    }
  }
}
