package com.example.demo.basic;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import java.util.UUID;

public class Send {

  public static void main(String[] args) {
    Environment environment = Environment.builder().build();
    environment.streamCreator().stream("hello-java-stream").create();
    Producer producer = environment.producerBuilder().stream("hello-java-stream").build();
    Message message =
        producer
            .messageBuilder()
            .addData("Hello, World!".getBytes())
            .properties()
            .messageId(UUID.randomUUID())
            .contentType("application/text")
            .messageBuilder()
            .build();
    producer.send(message, null);
    System.out.println("[x] `Hello World!` message sent");
  }
}
