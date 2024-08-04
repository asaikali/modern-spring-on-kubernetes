package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Order(3)
public class WebClientService implements CommandLineRunner {

  private final WebClient webClient;

  public WebClientService(WebClient.Builder builder) {
    this.webClient =
        builder
            .baseUrl("http://localhost:8080")
            .defaultHeaders(headers -> headers.setBasicAuth("user", "password"))
            .build();
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using WebClient");

    var response = webClient.get().retrieve().bodyToMono(String.class).block();
    System.out.println(response);
  }
}
