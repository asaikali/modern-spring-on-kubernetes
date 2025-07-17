package com.example.sse.sever.rabbit;

import java.util.Optional;
import java.util.function.Consumer;

public interface EventStream {

  /** Appends a value to the stream and returns the resulting Event. */
  boolean append(String value, long index);

  /**
   * Returns all events with index > after.index in the same stream. and register a consumer that
   * will be called for any new events that are aviable to the event store
   */
  void consumeAfter(Optional<EventId> after, Consumer<Event> consumer);

  /** Returns this stream's StreamId. */
  StreamId getStreamId();
}
