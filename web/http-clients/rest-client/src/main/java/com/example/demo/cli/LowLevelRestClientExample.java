package com.example.demo.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

public class LowLevelRestClientExample {

  record ExchangeResult(HttpRequest request, ConvertibleClientHttpResponse response) {}

  public static void main(String[] args) throws IOException {

    RestClient restClient = RestClient.builder().build();

    ExchangeResult result =
        restClient
            .get()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
            .header("Accept", "application/json")
            .exchange(ExchangeResult::new, false); // don't auto-close

    try (ClientHttpResponse response = result.response();
        InputStream body = response.getBody()) {

      body.transferTo(System.out); // read manually after RestClient call
    }
  }
}
