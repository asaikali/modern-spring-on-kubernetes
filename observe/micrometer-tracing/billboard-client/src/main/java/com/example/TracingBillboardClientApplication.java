package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class TracingBillboardClientApplication {

  @Bean
  QuoteService quoteService() {
    WebClient client = WebClient.builder().baseUrl("http://localhost:8081").build();
    WebClientAdapter webClientAdapter = WebClientAdapter.create(client);
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builder().exchangeAdapter(webClientAdapter).build();

    QuoteService service = factory.createClient(QuoteService.class);

    return service;
  }

  public static void main(String[] args) {
    SpringApplication.run(TracingBillboardClientApplication.class, args);
  }
}
