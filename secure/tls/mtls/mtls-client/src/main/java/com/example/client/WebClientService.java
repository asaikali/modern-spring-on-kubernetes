package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Order(3)
public class WebClientService implements CommandLineRunner {

  private final WebClient webClient;

  public WebClientService(WebClient.Builder builder, WebClientSsl sslClient) {
    this.webClient =
        builder.baseUrl("https://localhost:8443").apply(sslClient.fromBundle("client")).build();
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using WebClient mTLS");

    var response = webClient.get().retrieve().bodyToMono(String.class).block();
    System.out.println(response);
  }
}
