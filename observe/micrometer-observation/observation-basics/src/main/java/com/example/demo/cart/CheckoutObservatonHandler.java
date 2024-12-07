package com.example.demo.cart;

import com.example.demo.orders.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.Observation.Event;
import io.micrometer.observation.ObservationHandler;

public class CheckoutObservatonHandler implements ObservationHandler<Observation.Context> {

  final MeterRegistry meterRegistry;
  final Counter ordersPlaced;
  final Counter ordersFlagged;

  public CheckoutObservatonHandler(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    this.ordersPlaced = meterRegistry.counter("orders.placed");
    this.ordersFlagged = meterRegistry.counter("orders.flagged");
  }

  @Override
  public void onStart(Context context) {}

  @Override
  public void onError(Context context) {}

  @Override
  public void onEvent(Event event, Context context) {}

  @Override
  public void onScopeOpened(Context context) {}

  @Override
  public void onScopeClosed(Context context) {}

  @Override
  public void onScopeReset(Context context) {}

  @Override
  public void onStop(Context context) {
    ordersPlaced.increment();
    Order order = context.get("order");
    if (order.isFlagged()) {
      ordersFlagged.increment();
    }
  }

  @Override
  public boolean supportsContext(Context context) {
    return "checkout".equals(context.getName());
  }
}
