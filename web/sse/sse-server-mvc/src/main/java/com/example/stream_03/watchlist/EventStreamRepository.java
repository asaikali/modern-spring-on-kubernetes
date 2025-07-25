package com.example.stream_03.watchlist;

import java.util.Optional;

interface EventStreamRepository {

  /** Creates a new event stream and returns it. */
  EventStream create();

  /** Retrieves an existing event stream by StreamId. */
  Optional<EventStream> get(StreamId streamId);

  /** Deletes an event stream by StreamId. */
  void delete(StreamId streamId);
}
