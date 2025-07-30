package com.example.demo.cli;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

public class HttpErrorsExample {

  record Post(Integer userId, Integer id, String title, String body) {}

  public static void main(String[] args) {

    // create a client with a base url
    RestClient restClient = RestClient.builder().baseUrl("https://httpbin.org").build();

    //
    // handle 400 errors by catching HttpClientErrorException or one of its
    // subtypes
    //

    try {
      System.out.println("GET /status/400");
      String firstPost = restClient.get().uri("/status/404", 1).retrieve().body(String.class);
    } catch (HttpClientErrorException.NotFound e) {
      System.out.println("caught exception of type " + e.getClass().getName());
      System.out.println(e.getMessage());
    }

    //
    // handle 500 errors by catching HttpClientErrorException or one of its subtypes
    //
    try {
      System.out.println("GET /status/500");
      String firstPost = restClient.get().uri("/status/500", 1).retrieve().body(String.class);
    } catch (HttpServerErrorException e) {
      System.out.println("caught exception of type " + e.getClass().getName());
      System.out.println(e.getMessage());
    }

    // handle errors with onStatus callbacks
    restClient
        .get()
        .uri("/status/400")
        .retrieve()
        .onStatus(
            response -> response.is4xxClientError(),
            (request, response) -> {
              System.out.println("Handled error without an exception using onSatus");
            });
  }
}
