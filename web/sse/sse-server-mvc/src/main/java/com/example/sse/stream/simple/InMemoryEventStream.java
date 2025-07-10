package com.example.sse.stream.simple;

import com.example.sse.stream.Event;
import com.example.sse.stream.EventId;
import com.example.sse.stream.EventStream;
import com.example.sse.stream.StreamId;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
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
    public List<Event> readAfter(EventId after) {
        if (!after.streamId().equals(this.streamId)) {
            throw new IllegalArgumentException("StreamId mismatch");
        }
        return events.tailMap(after.index() + 1).entrySet().stream()
            .map(entry -> new Event(
                new EventId(streamId, entry.getKey()),
                entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public StreamId getStreamId() {
        return streamId;
    }
}
