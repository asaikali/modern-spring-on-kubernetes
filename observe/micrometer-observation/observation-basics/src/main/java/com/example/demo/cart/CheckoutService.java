package com.example.demo.cart;

import com.example.demo.orders.Order;
import com.example.demo.orders.OrderService;
import com.example.demo.payments.PaymentRequest;
import com.example.demo.payments.PaymentService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {
  private final OrderService orderService;
  private final PaymentService paymentService;
  private final ObservationRegistry observationRegistry;

  public CheckoutService(
      OrderService orderService, PaymentService paymentService, MeterRegistry meterRegistry) {
    this.orderService = orderService;
    this.paymentService = paymentService;
    this.observationRegistry = ObservationRegistry.create();
    this.observationRegistry
        .observationConfig()
        .observationHandler(new CheckoutObservatonHandler(meterRegistry));
  }

  public void checkout(Order order, PaymentRequest paymentRequest) {
    Observation.Context context = new Observation.Context().put("order", order);

    Observation.createNotStarted("checkout", () -> context, observationRegistry)
        // .lowCardinalityKeyValue("test","value")
        .observationConvention(new MyConvention())
        .observationConvention(new MyConvention())
        .observe(
            () -> {
              this.orderService.placeOrder(order);
              this.paymentService.chargeCreditCard(paymentRequest);
            });
  }
}
