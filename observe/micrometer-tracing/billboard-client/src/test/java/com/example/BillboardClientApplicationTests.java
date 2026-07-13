package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMetrics
@AutoConfigureTracing
public class BillboardClientApplicationTests {

  @Test
  public void contextLoads() {}
}
