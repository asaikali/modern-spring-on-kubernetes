package com.example.stream_04.orders.sse.client;

@FunctionalInterface
public interface SseEventHandler {
  /**
   * Function interface for handling SSE events with event numbering.
   *
   * @param event the parsed SSE event
   * @return true to continue processing, false to stop
   */
  boolean handle(RawSseEvent event);
}
