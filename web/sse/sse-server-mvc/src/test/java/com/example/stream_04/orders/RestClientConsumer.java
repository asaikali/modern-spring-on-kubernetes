package com.example.stream_04.orders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class RestClientConsumer {

  public static void main(String[] args) {
    RestClient client = RestClient.create(); // create default RestClient
    // [oai_citation:0‡Home](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html)

    // var streamOrder = new LimitOrderRequest("APPL", 100, BigDecimal.valueOf(101.5));
    client
        .get()
        .uri("http://localhost:8080/mvc/stream/infinite")
        .accept(MediaType.TEXT_EVENT_STREAM) // SSE MIME type
        // [oai_citation:1‡Baeldung](https://www.baeldung.com/spring-server-sent-events)
        .exchange(
            (request, response) -> {
              try (InputStream is = response.getBody()) {
                try (BufferedReader br =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                  String line;
                  while ((line = br.readLine()) != null) {
                    System.out.println(line);
                  }
                }
              }
              return "DONE";
            });
  }
}
