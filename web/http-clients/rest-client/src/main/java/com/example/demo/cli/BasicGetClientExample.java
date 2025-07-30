package com.example.demo.cli;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

public class BasicGetClientExample {

  record Post(Integer userId, Integer id, String title, String body) {}

  public static void main(String[] args) {

    // create a client with a base url
    RestClient restClient =
        RestClient.builder().baseUrl("https://jsonplaceholder.typicode.com/").build();

    // read response as a string
    String firstPost = restClient.get().uri("/posts/{id}", 1).retrieve().body(String.class);

    System.out.println(firstPost);
    System.out.println();

    // auto convert body to a type
    List<Post> allPosts =
        restClient
            .get()
            .uri("/posts")
            .retrieve()
            .body(new ParameterizedTypeReference<List<Post>>() {});

    System.out.println("got " + allPosts.size() + " posts");

    // return an entity with both the body and headers
    ResponseEntity<Post> postEntity =
        restClient.get().uri("/posts/1").retrieve().toEntity(Post.class);
    System.out.println(postEntity.getBody());
    System.out.println("Response code " + postEntity.getStatusCode());
  }
}
