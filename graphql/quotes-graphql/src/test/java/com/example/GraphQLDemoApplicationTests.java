package com.example;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class GraphQLDemoApplicationTests {

  @LocalServerPort private Integer port;

  private GraphQlTester graphQlTester;

  @BeforeEach
  void setup(@Autowired ApplicationContext applicationContext) {

    WebTestClient client =
        WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port + "/graphql").build();

    this.graphQlTester = HttpGraphQlTester.create(client);
  }

  @Test
  void contextLoads() {

    var document =
        """
            query {
                allAuthors {
                    name
                    wikipediaUrl
                    field
                }
            }
            """;

    this.graphQlTester
        .document(document)
        .execute()
        .path("allAuthors")
        .entityList(Author.class)
        .hasSize(5);
  }
}
