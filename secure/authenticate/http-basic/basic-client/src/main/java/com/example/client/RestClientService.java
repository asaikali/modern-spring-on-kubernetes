package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Order(1)
public class RestClientService implements CommandLineRunner {

  private final RestClient restClient;

  public RestClientService(RestClient.Builder builder) {
    this.restClient =
        builder
            .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
            .baseUrl("http://localhost:8080")
            .build();
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using RestClient");

    var response = restClient.get().retrieve().toEntity(String.class);
    System.out.println(response);
  }
}
