package com.example.demo.orders;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final Counter ordersPlaced;
  private final Counter flaggedOrders;

  public OrderService(MeterRegistry meterRegistry) {
    ordersPlaced = meterRegistry.counter("orders.placed");
    flaggedOrders = meterRegistry.counter("orders.flagged");
  }

  public void placeOrder(Order order) {
    if (isSuspiciousOrder(order)) {
      flaggedOrders.increment();
    } else {
      ordersPlaced.increment();
    }
  }

  private boolean isSuspiciousOrder(Order order) {
    return order.getId() % 2 == 0;
  }
}
