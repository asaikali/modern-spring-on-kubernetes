package com.example.demo.basic;

import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

public class Receive {

  public static void main(String[] args) {
    Environment environment = Environment.builder().build();
    environment.streamCreator().stream("hello-java-stream").create();

    Consumer consumer =
        environment.consumerBuilder().stream("hello-java-stream")
            .offset(OffsetSpecification.first())
            .messageHandler(
                (unused, message) -> {
                  System.out.println("Received Message: " + new String(message.getBodyAsBinary()));
                })
            .build();
  }
}
