package com.example.stream_03.watchlist;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStreamRepository implements EventStreamRepository {

  private final Map<StreamId, EventStream> streams = new ConcurrentHashMap<>();

  @Override
  public EventStream create() {
    StreamId id = StreamId.newStreamId();
    EventStream stream = new InMemoryEventStream(id);
    streams.put(id, stream);
    return stream;
  }

  @Override
  public Optional<EventStream> get(StreamId streamId) {
    return Optional.ofNullable(streams.get(streamId));
  }

  @Override
  public void delete(StreamId streamId) {
    streams.remove(streamId);
  }
}
