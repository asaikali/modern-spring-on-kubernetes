package com.example.stream_04.orders.sse.client;

@FunctionalInterface
public interface SseEventHandler {
  /**
   * Function interface for handling SSE events with event numbering.
   *
   * @param eventIndex the 0-based index of this event in the stream
   * @param event the parsed SSE event
   * @return true to continue processing, false to stop
   */
  boolean handle(long eventIndex, RawSseEvent event);
}
