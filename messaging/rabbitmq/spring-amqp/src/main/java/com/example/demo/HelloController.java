package com.example.demo;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  private final RabbitTemplate rabbitTemplate;
  private final Queue helloQueue;
  private final FanoutExchange fanoutExchange;

  public HelloController(RabbitTemplate rabbitTemplate, @Qualifier("hello") Queue hello, FanoutExchange fanoutExchange) {
    this.rabbitTemplate = rabbitTemplate;
    this.fanoutExchange = fanoutExchange;
    this.helloQueue = hello;
  }

  @GetMapping("/put")
  public String put() {
    this.rabbitTemplate.convertAndSend(helloQueue.getName(), "hello");
    return "Sent hello World";
  }

  @GetMapping("/fanout")
  public String fanout() {
    this.rabbitTemplate.convertAndSend(fanoutExchange.getName(), "hello");
    return "sent message on a fanout exchange";
  }

}
