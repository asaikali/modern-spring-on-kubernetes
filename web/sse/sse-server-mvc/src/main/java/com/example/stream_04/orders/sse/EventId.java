package com.example.stream_04.orders.sse;

import java.util.Objects;

/**
 * Represents a unique identifier for a specific event within a stream. It is composed of a StreamId
 * and a sequential index.
 *
 * <p>The canonical string format for an EventId is "prefix.uuid_index".
 *
 * @param streamId The identifier of the stream to which this event belongs.
 * @param index The sequential index of the event within its stream, typically starting from 0 or 1.
 */
public record EventId(StreamId streamId, long index) {

  private static final char INDEX_DELIMITER = '_';

  /**
   * Canonical constructor for EventId. Ensures that the associated StreamId is not null and the
   * index is non-negative.
   */
  public EventId {
    Objects.requireNonNull(streamId, "StreamId cannot be null.");
    if (index < 0) {
      throw new IllegalArgumentException("Event index cannot be negative. Was: " + index);
    }
  }

  public EventId withIndex(long index) {
    return new EventId(streamId, index);
  }

  public static EventId firstEvent(StreamId streamId) {
    return new EventId(streamId, 0);
  }

  /**
   * Parses a full event ID string into an EventId object. The input string must be in the format
   * "prefix.uuid_index".
   *
   * @param eventIdString The string representation of the event ID (e.g.,
   *     "myprefix.a1b2c3d4-e5f6-7890-1234-567890abcdef_123").
   * @return An EventId instance parsed from the string.
   * @throws IllegalArgumentException if the input string is null, empty, malformed, or contains
   *     invalid StreamId or index components.
   */
  public static EventId fromString(String eventIdString) {
    Objects.requireNonNull(eventIdString, "Event ID string cannot be null.");

    if (eventIdString.isEmpty()) {
      throw new IllegalArgumentException("Event ID string cannot be empty.");
    }

    // Find the last occurrence of the delimiter to separate StreamId from index
    int delimiterIndex = eventIdString.lastIndexOf(INDEX_DELIMITER);

    if (delimiterIndex <= 0 || delimiterIndex == eventIdString.length() - 1) {
      throw new IllegalArgumentException(
          "Event ID string '%s' must be in the format 'prefix.uuid_index'." // Updated format in
              // message
              .formatted(eventIdString));
    }

    String streamIdPartString = eventIdString.substring(0, delimiterIndex);
    String indexPartString = eventIdString.substring(delimiterIndex + 1);

    // Parse the StreamId part
    StreamId parsedStreamId;
    try {
      parsedStreamId = StreamId.fromString(streamIdPartString);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid StreamId segment in Event ID string: '%s'".formatted(streamIdPartString), e);
    }

    // Parse the index part
    long parsedIndex;
    try {
      parsedIndex = Long.parseLong(indexPartString);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid index segment in Event ID string: '%s'".formatted(indexPartString), e);
    }

    return new EventId(parsedStreamId, parsedIndex);
  }

  /**
   * Returns the canonical string representation of this EventId in the format "prefix.uuid_index".
   *
   * @return The formatted string representation of the EventId.
   */
  @Override
  public String toString() {
    // StreamId's toString() or fullName() will provide "prefix.uuid"
    return streamId.fullName() + INDEX_DELIMITER + index;
  }
}
