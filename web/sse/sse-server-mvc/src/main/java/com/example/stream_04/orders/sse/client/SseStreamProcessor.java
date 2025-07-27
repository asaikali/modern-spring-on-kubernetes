package com.example.stream_04.orders.sse.client;

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

  // Prevent instantiation
  private SseStreamProcessor() {}


  /**
   * Parse SSE stream from a RestClient response with configurable limits.
   *
   * @param response the ClientHttpResponse containing the SSE stream
   * @param handler callback for each complete RawSseEvent; return false to stop early
   * @param maxEventChars maximum allowed characters per event to prevent unbounded growth
   */
  public static void parseStream(
      ClientHttpResponse response,
      Function<RawSseEvent, ProcessingResult> handler,
      int maxEventChars) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
      parseStream(reader, handler, maxEventChars);
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
   * @param handler callback for each complete RawSseEvent; return false to stop early
   * @param maxEventChars maximum allowed characters per event to prevent unbounded growth
   * @throws IOException if an I/O error occurs or maxEventChars is exceeded
   */
  private static void parseStream(
      BufferedReader reader, Function<RawSseEvent, ProcessingResult> handler, long maxEventChars)
      throws IOException {
    StringBuilder buffer = new StringBuilder();

    // Strip UTF-8 BOM if present (§9.2.5)
    stripBomIfPresent(reader);

    String line;
    while ((line = reader.readLine()) != null) {
      // Blank line => dispatch complete event (§9.2.5)
      if (line.isEmpty()) {
        if (buffer.length() > 0) {
          RawSseEvent event = new RawSseEvent(buffer.toString());
          if (handler.apply(event) == ProcessingResult.STOP) {
            return;
          }
          buffer.setLength(0);
        }
      } else {
        // Validate event size before adding line
        validateEventSize(buffer, line, maxEventChars);
        buffer.append(line).append('\n');
      }
    }

    // At EOF: per spec §9.2.5, discard any buffered but unterminated event
  }

  /** Strip a leading UTF-8 BOM (U+FEFF) if present per SSE specification §9.2.5. */
  private static void stripBomIfPresent(BufferedReader reader) throws IOException {
    reader.mark(1);
    int firstChar = reader.read();
    if (firstChar != 0xFEFF) {
      reader.reset();
    }
  }

  /**
   * Validate that adding a line won't exceed the maximum event size limit.
   *
   * @throws IOException if the size limit would be exceeded
   */
  private static void validateEventSize(StringBuilder buffer, String line, long maxEventChars)
      throws IOException {
    if (buffer.length() + line.length() + 1 > maxEventChars) {
      throw new IOException("SSE event exceeded maxEventChars=" + maxEventChars);
    }
  }
}
