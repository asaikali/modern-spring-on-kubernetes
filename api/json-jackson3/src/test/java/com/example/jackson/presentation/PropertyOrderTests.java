package com.example.jackson.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Demonstrates how to control the order of serialized properties using @JsonPropertyOrder.
 *
 * <p>This is useful for producing predictable or human-friendly JSON when field order matters, e.g.
 * for APIs, snapshots, or testing.
 */
public class PropertyOrderTests {

  /**
   * A simple POJO with three fields. The @JsonPropertyOrder annotation is used to define the
   * desired order of fields during serialization. It also overrides Jackson 3's default of sorting
   * properties alphabetically.
   */
  @JsonPropertyOrder({"id", "name", "email"})
  record User(
      @JsonProperty("name") String name,
      @JsonProperty("email") String email,
      @JsonProperty("id") int id) {}

  static final String EXPECTED_JSON =
      """
        {
          "id" : 1,
          "name" : "Alice",
          "email" : "alice@example.com"
        }""";

  @Test
  @DisplayName("Serialize using @JsonPropertyOrder to control field ordering")
  void serializeWithExplicitPropertyOrder() {
    ObjectMapper mapper = new JsonMapper();
    User user = new User("Alice", "alice@example.com", 1);

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

    assertThat(json).isEqualTo(EXPECTED_JSON);
  }
}
