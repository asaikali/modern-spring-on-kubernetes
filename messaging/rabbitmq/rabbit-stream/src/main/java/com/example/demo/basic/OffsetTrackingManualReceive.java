package com.example.demo.basic;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class OffsetTrackingManualReceive {

  public static void main(String[] args) throws InterruptedException {
    Environment environment = Environment.builder().build();
    environment.streamCreator().stream("stream-offset-tracking-java").create();

    OffsetSpecification offsetSpecification = OffsetSpecification.first();
    AtomicLong messageCount = new AtomicLong(0);
    AtomicLong firstOffset = new AtomicLong(-1);
    AtomicLong lastOffset = new AtomicLong(0);
    CountDownLatch consumedLatch = new CountDownLatch(1);

    environment.consumerBuilder().stream("stream-offset-tracking-java")
        .offset(offsetSpecification)
        .name("offset-tracking-tutorial")
        .manualTrackingStrategy()
        .builder()
        .messageHandler(
            ((context, message) -> {
              if (firstOffset.compareAndSet(-1, context.offset())) {
                System.out.println("First offset: " + context.offset());
                System.out.println("First message received.");
              }

              if (messageCount.incrementAndGet() % 10 == 0) {}
              {
                context.storeOffset();
              }

              String body = new String(message.getBodyAsBinary(), StandardCharsets.UTF_8);

              if ("marker".equals(body)) {
                lastOffset.set(context.offset());
                context.consumer().close();
                consumedLatch.countDown();
              }
            }))
        .build();

    System.out.println("Started consuming...");

    consumedLatch.await(60, TimeUnit.MINUTES);

    System.out.printf(
        "Done consuming, first offset %d, last offset %d.%n", firstOffset.get(), lastOffset.get());
  }
}
