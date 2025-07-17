package com.example.stream_03.watchlist;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InMemoryEventStream implements EventStream {

  private final StreamId streamId;
  private final AtomicInteger indexCounter = new AtomicInteger();
  private final ConcurrentSkipListMap<Integer, String> events = new ConcurrentSkipListMap<>();

  public InMemoryEventStream(StreamId streamId) {
    this.streamId = streamId;
  }

  @Override
  public Event append(String value) {
    int index = indexCounter.incrementAndGet();
    events.put(index, value);
    return new Event(new EventId(streamId, index), value);
  }

  @Override
  public List<Event> getEventsAfter(EventId after) {
    if (!after.streamId().equals(this.streamId)) {
      throw new IllegalArgumentException("StreamId mismatch");
    }
    return events.tailMap(after.index() + 1).entrySet().stream()
        .map(entry -> new Event(new EventId(streamId, entry.getKey()), entry.getValue()))
        .collect(Collectors.toList());
  }

  @Override
  public void consumeAfter(Optional<EventId> after, Consumer<EventId> consumer) {
    throw new UnsupportedOperationException();
  }

  @Override
  public StreamId getStreamId() {
    return streamId;
  }
}
