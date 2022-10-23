package com.example;

import com.example.cart.CheckoutService;
import com.example.orders.Order;
import com.example.payments.PaymentRequest;
import io.micrometer.core.annotation.Timed;
import java.util.Random;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  private final CheckoutService checkoutService;
  private final Random random = new Random();

  public RootController(CheckoutService checkoutService) {
    this.checkoutService = checkoutService;
  }

  @Timed(value = "generate_metrics_time")
  @GetMapping("/")
  public String generate() {
    int amount = random.nextInt(20);
    for (int i = 0; i < amount; i++) {
      Order order = new Order(i);
      PaymentRequest creditCard = new PaymentRequest();
      creditCard.setAmount(random.nextInt(199));
      this.checkoutService.checkout(order, creditCard);
    }

    return "generated and placed " + amount + " orders";
  }
}
