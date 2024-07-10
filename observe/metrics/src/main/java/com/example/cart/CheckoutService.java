package com.example.cart;

import com.example.orders.Order;
import com.example.orders.OrderService;
import com.example.payments.PaymentRequest;
import com.example.payments.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {
  private final OrderService orderService;
  private final PaymentService paymentService;

  public CheckoutService(OrderService orderService, PaymentService paymentService) {
    this.orderService = orderService;
    this.paymentService = paymentService;
  }

  public void checkout(Order order, PaymentRequest paymentRequest) {
    this.orderService.placeOrder(order);
    this.paymentService.chargeCreditCard(paymentRequest);
  }
}
