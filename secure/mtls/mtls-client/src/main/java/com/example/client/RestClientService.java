package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Order(1)
public class RestClientService implements CommandLineRunner {

  private final RestClient restClient;

  public RestClientService(RestClient.Builder builder, RestClientSsl sslClient) {
    this.restClient =
        builder.apply(sslClient.fromBundle("client")).baseUrl("https://localhost:8443").build();
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using RestClient mTLS");

    var response = restClient.get().retrieve().toEntity(String.class);
    System.out.println(response);
  }
}
