package com.example.stream_04.orders;

final class EventualResponse implements Response {

  private String lastEvenId;

  public EventualResponse(String lastEvenId) {
    this.lastEvenId = lastEvenId;
  }

  public String getLastEvenId() {
    return lastEvenId;
  }
}
