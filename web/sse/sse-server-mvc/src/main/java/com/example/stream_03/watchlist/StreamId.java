package com.example.stream_03.watchlist;

import java.util.UUID;

public record StreamId(UUID value) {

  public static StreamId newStreamId() {
    return new StreamId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public static StreamId fromString(String s) {
    return new StreamId(UUID.fromString(s));
  }
}
