package com.example.stream_04.orders;

final class ImmediateResponse implements Response {
  private OrderCompleted result;

  public ImmediateResponse(OrderCompleted result) {
    this.result = result;
  }

  public OrderCompleted getResult() {
    return result;
  }
}
