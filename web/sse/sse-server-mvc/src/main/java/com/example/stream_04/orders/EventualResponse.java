package com.example.stream_04.orders;

public final class EventualResponse implements Response {

  private String lastEvenId;

  EventualResponse(String lastEvenId) {
    this.lastEvenId = lastEvenId;
  }

  public String getLastEvenId() {
    return lastEvenId;
  }
}
