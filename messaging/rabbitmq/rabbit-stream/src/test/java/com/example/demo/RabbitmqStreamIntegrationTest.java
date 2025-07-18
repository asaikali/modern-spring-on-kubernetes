package com.example.demo;

import com.rabbitmq.stream.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class RabbitmqStreamIntegrationTest {

  static RabbitMQContainer rabbitmq;
  static Environment env;
  static String streamName = "test-stream";

  //   @BeforeAll
  static void setup() throws Exception {
    rabbitmq =
        new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.1.2-management"))
            .withExposedPorts(5552, 5672, 15672)
            .withEnv("RABBITMQ_STREAM_PORT", "5552")
            .withEnv(
                "RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS",
                "-rabbitmq_stream advertised_host 127.0.0.1 -rabbitmq_stream advertised_port 5552");

    rabbitmq.start();

    String uri = String.format("rabbitmq-stream://127.0.0.1:%d", rabbitmq.getMappedPort(5552));

    env = Environment.builder().uri(uri).build();
  }

  //   @AfterAll
  static void cleanup() {
    if (env != null) {
      env.close();
    }
    rabbitmq.stop();
  }

  //  @Test
  void testSendAndReceiveStreamMessage() throws Exception {
    // Create stream (auto-deleted)
    if (!env.streamExists(streamName)) {
      env.streamCreator().stream(streamName).create();
    }

    //        CountDownLatch latch = new CountDownLatch(1);
    //
    //        // Create consumer
    //        Consumer consumer = env.consumerBuilder()
    //            .stream(streamName)
    //            .offset(OffsetSpecification.first())
    //            .build(ctx -> {
    //                String message = new String(ctx.message().getBodyAsBinary());
    //                System.out.println("Received: " + message);
    //                latch.countDown();
    //            });
    //
    //        // Create producer and send a message
    //        Producer producer = env.producerBuilder().stream(streamName).build();
    //        producer.send(Message.builder().addData("hello-stream".getBytes()).build());
    //
    //        // Wait for message to arrive
    //        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Message was not received in
    // time");
    //
    //        consumer.close();
    //        producer.close();
  }
}
