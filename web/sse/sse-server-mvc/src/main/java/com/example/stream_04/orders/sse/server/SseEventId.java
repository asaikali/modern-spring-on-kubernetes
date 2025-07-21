package com.example.stream_04.orders.sse.server;

import java.util.Objects;

/**
 * Represents a unique identifier for a specific event within a stream. It is composed of a
 * SseStreamId and a sequential index.
 *
 * <p>The canonical string format for an SseEventId is "prefix.uuid_index".
 *
 * @param sseStreamId The identifier of the stream to which this event belongs.
 * @param index The sequential index of the event within its stream, typically starting from 0 or 1.
 */
public record SseEventId(SseStreamId sseStreamId, long index) {

  private static final char INDEX_DELIMITER = '_';

  /**
   * Canonical constructor for SseEventId. Ensures that the associated SseStreamId is not null and
   * the index is non-negative.
   */
  public SseEventId {
    Objects.requireNonNull(sseStreamId, "SseStreamId cannot be null.");
    if (index < 0) {
      throw new IllegalArgumentException("Event index cannot be negative. Was: " + index);
    }
  }

  public SseEventId withIndex(long index) {
    return new SseEventId(sseStreamId, index);
  }

  public static SseEventId firstEvent(SseStreamId sseStreamId) {
    return new SseEventId(sseStreamId, 0);
  }

  /**
   * Parses a full event ID string into an SseEventId object. The input string must be in the format
   * "prefix.uuid_index".
   *
   * @param eventIdString The string representation of the event ID (e.g.,
   *     "myprefix.a1b2c3d4-e5f6-7890-1234-567890abcdef_123").
   * @return An SseEventId instance parsed from the string.
   * @throws IllegalArgumentException if the input string is null, empty, malformed, or contains
   *     invalid SseStreamId or index components.
   */
  public static SseEventId fromString(String eventIdString) {
    Objects.requireNonNull(eventIdString, "Event ID string cannot be null.");

    if (eventIdString.isEmpty()) {
      throw new IllegalArgumentException("Event ID string cannot be empty.");
    }

    // Find the last occurrence of the delimiter to separate SseStreamId from index
    int delimiterIndex = eventIdString.lastIndexOf(INDEX_DELIMITER);

    if (delimiterIndex <= 0 || delimiterIndex == eventIdString.length() - 1) {
      throw new IllegalArgumentException(
          "Event ID string '%s' must be in the format 'prefix.uuid_index'." // Updated format in
              // message
              .formatted(eventIdString));
    }

    String streamIdPartString = eventIdString.substring(0, delimiterIndex);
    String indexPartString = eventIdString.substring(delimiterIndex + 1);

    // Parse the SseStreamId part
    SseStreamId parsedSseStreamId;
    try {
      parsedSseStreamId = SseStreamId.fromString(streamIdPartString);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid SseStreamId segment in Event ID string: '%s'".formatted(streamIdPartString), e);
    }

    // Parse the index part
    long parsedIndex;
    try {
      parsedIndex = Long.parseLong(indexPartString);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid index segment in Event ID string: '%s'".formatted(indexPartString), e);
    }

    return new SseEventId(parsedSseStreamId, parsedIndex);
  }

  /**
   * Returns the canonical string representation of this SseEventId in the format
   * "prefix.uuid_index".
   *
   * @return The formatted string representation of the SseEventId.
   */
  @Override
  public String toString() {
    // SseStreamId's toString() or fullName() will provide "prefix.uuid"
    return sseStreamId.fullName() + INDEX_DELIMITER + index;
  }
}
