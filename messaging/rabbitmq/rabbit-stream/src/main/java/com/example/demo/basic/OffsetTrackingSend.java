package com.example.demo.basic;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OffsetTrackingSend {

  public static void main(String[] args) throws InterruptedException {
    Environment environment = Environment.builder().build();
    environment.streamCreator().stream("stream-offset-tracking-java").create();

    Producer producer = environment.producerBuilder().stream("stream-offset-tracking-java").build();
    int messageCount = 100;
    CountDownLatch confirmedLatch = new CountDownLatch(messageCount);
    System.out.printf("Publishing %d messages%n", messageCount);
    for (int i = 0; i < messageCount; i++) {
      String body = i == messageCount - 1 ? "marker" : "hello";
      Message message = producer.messageBuilder().addData(body.getBytes()).build();
      producer.send(
          message,
          confirmationStatus -> {
            if (confirmationStatus.isConfirmed()) {
              confirmedLatch.countDown();
            }
          });
    }

    boolean completed = confirmedLatch.await(60, TimeUnit.SECONDS);
    System.out.printf("Messages confirmed: %b.%n", completed);
  }
}
