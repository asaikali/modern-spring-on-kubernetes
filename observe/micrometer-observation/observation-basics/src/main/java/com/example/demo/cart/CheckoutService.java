package com.example.demo.cart;


import com.example.demo.orders.Order;
import com.example.demo.orders.OrderService;
import com.example.demo.payments.PaymentRequest;
import com.example.demo.payments.PaymentService;
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
