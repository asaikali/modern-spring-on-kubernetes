package com.example.demo;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  private final RabbitTemplate rabbitTemplate;
  private final Queue helloQueue;

  public HelloController(RabbitTemplate rabbitTemplate, Queue hello) {
    this.rabbitTemplate = rabbitTemplate;
    this.helloQueue = hello;
  }

  @GetMapping("/put")
  public String put() {
    this.rabbitTemplate.convertAndSend(helloQueue.getName(), "hello");
    return "Sent hello World";
  }
}
