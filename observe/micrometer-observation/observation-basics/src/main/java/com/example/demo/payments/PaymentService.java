package com.example.demo.payments;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
  // private final Timer creditCardGatewayTimer;
  private final Random random = new Random();

  public PaymentService(MeterRegistry meterRegistry) {
    //  creditCardGatewayTimer = meterRegistry.timer("payments.chargeCard.time");
  }

  public void chargeCreditCard(PaymentRequest paymentRequest) {

    // Sample sample = Timer.start();

    //    creditCardGatewayTimer.record(
    //        () -> {

    try {
      TimeUnit.MILLISECONDS.sleep(random.nextInt(2000) + 100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // sample.stop(creditCardGatewayTimer);
  }
}
