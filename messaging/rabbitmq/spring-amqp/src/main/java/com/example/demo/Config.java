package com.example.demo;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  Queue hello() {
    return new Queue("hello");
  }

  @Bean
  public Queue anon() {
    return new AnonymousQueue();
  }

  @Bean
  FanoutExchange fanoutExchange() {
    return new FanoutExchange("tut.fanout");
  }

  @Bean
  Binding helloBinding(FanoutExchange exchange, @Qualifier("anon") Queue queue) {
    return BindingBuilder.bind(queue).to(exchange);
  }
}
