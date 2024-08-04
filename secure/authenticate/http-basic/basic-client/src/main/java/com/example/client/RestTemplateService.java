package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Order(2)
public class RestTemplateService implements CommandLineRunner {

  private final RestTemplate restTemplate;

  public RestTemplateService(RestTemplateBuilder builder, SslBundles sslBundles) {
    this.restTemplate =
        builder.rootUri("http://localhost:8080").basicAuthentication("user", "password").build();
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using RestTemplate");

    var response = restTemplate.getForEntity("/", String.class);
    System.out.println(response);
  }
}
