package com.example.demo;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class RootController {

  private RestClient restClient;

  public RootController(@LoadBalanced RestClient.Builder clientBuilder) {
    this.restClient = clientBuilder.build();
  }

  @GetMapping("/")
  public String getPosts() {
    return this.restClient
        .get()
        .uri("http://foo/posts")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(String.class);
  }
}
