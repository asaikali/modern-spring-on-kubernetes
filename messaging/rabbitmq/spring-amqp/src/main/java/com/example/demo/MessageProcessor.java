package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RabbitListener(queues = "hello")
@Service
public class MessageProcessor {

  private Logger logger = LoggerFactory.getLogger(MessageProcessor.class);

  @RabbitHandler
  public void process(String messagePaylod) {
    logger.info(messagePaylod);
  }
}
