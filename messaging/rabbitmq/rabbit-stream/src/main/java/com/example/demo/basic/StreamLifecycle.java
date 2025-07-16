package com.example.demo.basic;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import com.rabbitmq.stream.StreamException;
import java.time.Duration;

public class StreamLifecycle {

  public static void main(String[] args) {
    Environment environment = Environment.builder().id("test").build();

    // the same stream can be created multiple times as long as the configurations are identical
    environment.streamCreator().stream("my-stream").maxAge(Duration.ofDays(1)).create();
    environment.streamCreator().stream("my-stream").maxAge(Duration.ofDays(1)).create();

    // if stream configurations are different an exception will be thrown
    try {
      environment.streamCreator().stream("my-stream").maxAge(Duration.ofDays(2)).create();
    } catch (StreamException e) {
      System.out.println(
          "Can't create a stream with different configurations than what already exists");
      //  e.printStackTrace();
    }

    // a stream consumer can avoid creating a stream by checkinf if it exists
    if (environment.streamExists("my-stream")) {
      Producer producer = environment.producerBuilder().stream("my-stream").build();
      Message message =
          producer.messageBuilder().addData("Hello there from publisher A".getBytes()).build();
      producer.send(message, null);
    }

    //    environment.consumerBuilder()
    //        .stream("my-stream")
    //        .name("my-consumer")
    //    //    .offset(OffsetSpecification.offset(1))
    //        .singleActiveConsumer()
    //        .messageHandler( ((context, message) -> {
    //          System.out.println( new String(message.getBodyAsBinary()));
    //        }))
    //        .build();

    // delete the stream
    environment.deleteStream("my-stream");

    //    StreamStats streamStats = environment.queryStreamStats("my-stream");
    //    System.out.println(streamStats);

  }
}
