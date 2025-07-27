package com.example.sse;

import com.example.sse.SseStreamProcessor.ProcessingError.Type;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Parser for Server-Sent Events (SSE) streams according to WHATWG HTML Living Standard.
 *
 * <p>Implements parsing according to WHATWG HTML Living Standard: Sections 9.2.5 (Parsing an event
 * stream) and 9.2.6 (Interpreting an event stream), last updated 22 July 2025.
 */
public final class SseStreamProcessor {

  /** Result of processing an event or error, indicating whether to continue or stop the stream. */
  public enum ProcessingResult {
    /** Continue processing the stream */
    CONTINUE,
    /** Stop processing the stream */
    STOP
  }

  /** Information about a parsing error that allows recovery. */
  public record ProcessingError(Type type, String message, Throwable cause) {

    public enum Type {
      /** Event exceeded maximum size limit */
      SIZE_LIMIT_EXCEEDED,
      /** Event text was invalid (empty/null) */
      INVALID_EVENT,
      /** User's event handler threw an exception */
      HANDLER_ERROR
    }
  }

  // Prevent instantiation
  private SseStreamProcessor() {}

  /**
   * Parse SSE stream from a RestClient response with configurable limits.
   *
   * @param response the ClientHttpResponse containing the SSE stream
   * @param eventHandler callback for each complete RawSseEvent; return false to stop early
   * @param maxEventChars maximum allowed characters per event to prevent unbounded growth
   */
  public static void parseStream(
      ClientHttpResponse response,
      Function<RawSseEvent, ProcessingResult> eventHandler,
      Function<ProcessingError, ProcessingResult> errorHandler,
      int maxEventChars) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
      parseStream(reader, eventHandler, errorHandler, maxEventChars);
    } catch (IOException e) {
      throw new RuntimeException("Failed to process SSE stream", e);
    }
  }

  /**
   * Core SSE parsing implementation.
   *
   * <ul>
   *   <li>Strips a leading UTF-8 BOM if present (per §9.2.5).
   *   <li>Accumulates lines terminated by LF/CRLF/CR into events.
   *   <li>Dispatches events upon a blank line (per §9.2.5).
   *   <li>Ignores incomplete events at EOF (per §9.2.5).
   *   <li>Field lines processed via {@link RawSseEvent#parseFields()} (per §9.2.6).
   *   <li>Comment lines (starting with ':') are ignored (per §9.2.5).
   * </ul>
   *
   * @param reader buffered reader over a UTF-8 text/event-stream source
   * @param eventHandler callback for each complete RawSseEvent; return false to stop early
   * @param maxEventChars maximum allowed characters per event to prevent unbounded growth
   * @throws IOException if an I/O error occurs or maxEventChars is exceeded
   */
  private static void parseStream(
      BufferedReader reader,
      Function<RawSseEvent, ProcessingResult> eventHandler,
      Function<ProcessingError, ProcessingResult> errorHandler,
      long maxEventChars)
      throws IOException {
    StringBuilder linesBuffer = new StringBuilder();

    // Strip a leading UTF-8 BOM (U+FEFF) if present per SSE specification §9.2.5.
    reader.mark(1);
    int firstChar = reader.read();
    if (firstChar != 0xFEFF) {
      reader.reset();
    }

    String line;
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        // Blank line => dispatch complete event (§9.2.5)
        if (linesBuffer.length() > 0) {
          RawSseEvent event = new RawSseEvent(linesBuffer.toString());
          try {
            if (eventHandler.apply(event) == ProcessingResult.STOP) {
              return;
            }
          } catch (Exception e) {
            ProcessingResult result =
                errorHandler.apply(
                    new ProcessingError(
                        Type.HANDLER_ERROR, "Exception while executing handler", e));
            if (result == ProcessingResult.STOP) {
              return;
            }
          }
          linesBuffer.setLength(0);
        }
      } else {
        // Non-Blank line => store the line in the event buffer
        // Validate event size before adding line
        if (linesBuffer.length() + line.length() + 1 > maxEventChars) {
          ProcessingResult result =
              errorHandler.apply(
                  new ProcessingError(
                      Type.SIZE_LIMIT_EXCEEDED,
                      "SSE event exceeds maximum allowed size of " + maxEventChars,
                      null));
          if (result == ProcessingResult.STOP) {
            return;
          } else {
            // Skip this oversized event - reset buffer
            linesBuffer.setLength(0);
            continue; // Skip to next line
          }
        }
        linesBuffer.append(line).append('\n');
      }
    }

    // At EOF: per spec §9.2.5, discard any buffered but unterminated event
  }
}
