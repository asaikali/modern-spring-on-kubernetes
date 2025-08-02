package com.example.demo;

import com.example.demo.discovery.CustomDiscovery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class RootController {

  private final RestClient restClient;

  public RootController(@CustomDiscovery RestClient.Builder builder) {
    restClient = builder.build();
  }

  @GetMapping("/")
  String get() {

    return restClient.get().uri("http://my-service/posts").retrieve().body(String.class);
  }
}
