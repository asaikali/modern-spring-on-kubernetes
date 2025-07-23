package com.example.stream_04.orders.sse.client;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utility for processing Server-Sent Events (SSE) streams.
 *
 * <p>Implements parsing according to WHATWG HTML Living Standard: Sections 9.2.5 (Parsing an event
 * stream) and 9.2.6 (Interpreting an event stream), last updated 22 July 2025.
 */
public final class SseStreamUtils {

  // Prevent instantiation
  private SseStreamUtils() {
    /* no-op */
  }

  /**
   * Reads lines from {@code reader}, parses SSE text/event-stream frames, and invokes the handler.
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
  public static void processStream(
      BufferedReader reader, SseEventHandler handler, long maxEventChars) throws IOException {
    StringBuilder buffer = new StringBuilder();
    long eventIndex = 0;

    // Strip a leading UTF-8 BOM (U+FEFF) if present (§9.2.5)
    reader.mark(1);
    int firstChar = reader.read();
    if (firstChar != 0xFEFF) {
      reader.reset();
    }

    String line;
    while ((line = reader.readLine()) != null) {
      // Blank line => dispatch complete event (§9.2.5)
      if (line.isEmpty()) {
        RawSseEvent event = new RawSseEvent(buffer.toString());
        if (!handler.handle(eventIndex++, event)) {
          return; // stop if handler returns false
        }
        buffer.setLength(0);

      } else {
        // Guard by character count only (simplified check)
        if (buffer.length() + line.length() + 1 > maxEventChars) {
          throw new IOException("SSE event exceeded maxEventChars=" + maxEventChars);
        }
        buffer.append(line).append('\n');
      }
    }

    // At EOF: per spec §9.2.5, discard any buffered but unterminated event
  }
}
