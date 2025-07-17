package com.example.sse.sever.rabbit;

public record EventId(StreamId streamId, long index) {

  public EventId {
    if (index < 0) {
      throw new IllegalArgumentException("index cannot be negative");
    }
  }

  @Override
  public String toString() {
    return streamId + ":" + index;
  }

  public static EventId fromString(String value) {
    String[] parts = value.split(":");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid EventId format: " + value);
    }
    return new EventId(StreamId.fromString(parts[0]), Integer.parseInt(parts[1]));
  }
}
