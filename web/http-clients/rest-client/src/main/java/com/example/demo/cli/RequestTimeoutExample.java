package com.example.demo.cli;

import java.time.Duration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

public class RequestTimeoutExample {

  record Post(Integer userId, Integer id, String title, String body) {}

  public static void main(String[] args) {

    // Create RestClient with 1-second read timeout
    JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
    requestFactory.setReadTimeout(Duration.ofSeconds(1));

    // create a client with a base url
    RestClient restClient =
        RestClient.builder().baseUrl("https://httpbin.org").requestFactory(requestFactory).build();

    //
    // handle 400 errors by catching HttpClientErrorException or one of its
    // subtypes
    //

    try {
      System.out.println("GET /delay/10");
      String firstPost = restClient.get().uri("/delay/10").retrieve().body(String.class);
    } catch (ResourceAccessException e) {
      System.out.println("caught exception of type " + e.getClass().getName());
      System.out.println(e.getMessage());
    }
  }
}
