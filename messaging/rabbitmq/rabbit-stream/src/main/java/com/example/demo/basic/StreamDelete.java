package com.example.demo.basic;

import com.rabbitmq.stream.Environment;

public class StreamDelete {

  public static void main(String[] args) {
    Environment environment = Environment.builder().build();
    environment.deleteStream("hello-java-stream");
  }
}
