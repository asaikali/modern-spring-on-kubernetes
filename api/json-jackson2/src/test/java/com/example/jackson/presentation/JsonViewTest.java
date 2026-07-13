package com.example.jackson.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This test demonstrates how to use Jackson Views to conditionally include fields in the output.
 */
public class JsonViewTest {

  // Define view marker interfaces
  interface PublicView {}

  interface InternalView extends PublicView {}

  // Define POJO with @JsonView annotations
  static class User {
    @JsonView(PublicView.class)
    public String username;

    @JsonView(PublicView.class)
    public String displayName;

    @JsonView(InternalView.class)
    public String email;

    @JsonView(InternalView.class)
    public String phone;

    public User(String username, String displayName, String email, String phone) {
      this.username = username;
      this.displayName = displayName;
      this.email = email;
      this.phone = phone;
    }
  }

  static final User USER_OBJECT = new User("alice", "Alice Smith", "alice@example.com", "555-1234");

  static final String PUBLIC_JSON =
      """
      {
        "username" : "alice",
        "displayName" : "Alice Smith"
      }
      """;

  static final String INTERNAL_JSON =
      """
      {
        "username" : "alice",
        "displayName" : "Alice Smith",
        "email" : "alice@example.com",
        "phone" : "555-1234"
      }
      """;

  @Test
  @DisplayName("Serialize using Public view")
  void serializeWithPublicView_shouldIncludePublicFieldsOnly() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json =
        mapper
            .writerWithView(PublicView.class)
            .withDefaultPrettyPrinter()
            .writeValueAsString(USER_OBJECT);

    System.out.println("Public JSON:\n" + json);
    assertThat(json).isEqualToIgnoringWhitespace(PUBLIC_JSON);
  }

  @Test
  @DisplayName("Serialize using Internal view")
  void serializeWithInternalView_shouldIncludeAllFields() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json =
        mapper
            .writerWithView(InternalView.class)
            .withDefaultPrettyPrinter()
            .writeValueAsString(USER_OBJECT);

    System.out.println("Internal JSON:\n" + json);
    assertThat(json).isEqualToIgnoringWhitespace(INTERNAL_JSON);
  }
}
