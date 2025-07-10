package com.example.sse.stream;

import java.util.List;

public interface EventStream {

    /**
     * Appends a value to the stream and returns the resulting Event.
     */
    Event append(String value);

    /**
     * Returns all events with index > after.index in the same stream.
     */
    List<Event> readAfter(EventId after);

    /**
     * Returns this stream's StreamId.
     */
    StreamId getStreamId();
}
