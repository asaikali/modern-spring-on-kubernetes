package com.example.demo.cli;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class RequestCustomizationExample {

  record Post(Integer userId, Integer id, String title, String body) {}

  public static void main(String[] args) {

    // create a client with a base url
    RestClient restClient =
        RestClient.builder()
            .baseUrl("https://httpbin.org")
            .defaultHeaders(
                httpHeaders -> {
                  httpHeaders.setBearerAuth("key");
                })
            .build();

    var response =
        restClient
            .get()
            .uri("/get")
            .accept(MediaType.APPLICATION_JSON)
            .cookie("boo", "value")
            .header("X-custom", "foo", "bar")
            .headers(
                httpHeaders -> {
                  httpHeaders.add("another", "value");
                })
            .retrieve()
            .body(String.class);

    System.out.println(response);
  }
}
