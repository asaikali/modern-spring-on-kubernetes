package com.example.demo.cli;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

public class InterceptorExample {

  record Post(Integer userId, Integer id, String title, String body) {}

  static class ExampleInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
      // Log the request
      System.out.println("Request: " + request.getMethod() + " " + request.getURI());

      // Execute the request
      ClientHttpResponse response = execution.execute(request, body);

      // Log the response
      System.out.println("Response: " + response.getStatusCode());

      return response;
    }
  }

  public static void main(String[] args) {

    // create a client with a base url
    RestClient restClient =
        RestClient.builder()
            .baseUrl("https://httpbin.org")
            .requestInterceptor(new ExampleInterceptor())
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
