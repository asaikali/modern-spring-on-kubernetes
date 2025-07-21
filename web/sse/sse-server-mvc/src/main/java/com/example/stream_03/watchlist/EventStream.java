package com.example.stream_03.watchlist;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

interface EventStream {

  /** Appends a value to the stream and returns the resulting Event. */
  Event append(String value);

  /** Returns all events with index > after.index in the same stream. */
  List<Event> getEventsAfter(EventId after);

  /**
   * Returns all events with index > after.index in the same stream. and register a consumer that
   * will be called for any new events that are aviable to the event store
   */
  void consumeAfter(Optional<EventId> after, Consumer<EventId> consumer);

  /** Returns this stream's StreamId. */
  StreamId getStreamId();
}
