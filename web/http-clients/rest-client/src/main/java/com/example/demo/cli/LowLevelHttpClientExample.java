package com.example.demo.cli;

import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.io.InputStream;

public class LowLevelHttpClientExample {

  public static void main(String[] args) throws IOException {

    ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    ClientHttpRequest request = requestFactory
        .createRequest(URI.create("https://jsonplaceholder.typicode.com/posts"), HttpMethod.GET);

    try (ClientHttpResponse response = request.execute();
        InputStream body = response.getBody()) {

      body.transferTo(System.out); // stream directly to screen
    }
  }
}
