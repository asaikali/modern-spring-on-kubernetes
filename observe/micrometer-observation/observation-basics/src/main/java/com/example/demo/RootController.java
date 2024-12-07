package com.example.demo;

import com.example.demo.cart.CheckoutService;
import com.example.demo.orders.Order;
import com.example.demo.payments.PaymentRequest;
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

  @GetMapping("/")
  public String generate() {

    Order order = new Order(1);
    var creditCard = new PaymentRequest("4012888888881881", random.nextInt(199));
    this.checkoutService.checkout(order, creditCard);

    return "generated 1 order";
  }

  @GetMapping("/random")
  public String generlateRandom() {
    int amount = random.nextInt(20) + 1;
    for (int i = 0; i < amount; i++) {
      Order order = new Order(i);
      var creditCard = new PaymentRequest("4012888888881881", random.nextInt(199));
      this.checkoutService.checkout(order, creditCard);
    }

    return "generated and placed " + amount + " orders";
  }
}
