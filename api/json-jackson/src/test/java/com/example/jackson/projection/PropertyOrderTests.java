package com.example.jackson.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates how to control the order of serialized properties using @JsonPropertyOrder.
 *
 * This is useful for producing predictable or human-friendly JSON when field order matters,
 * e.g. for APIs, snapshots, or testing.
 */
public class PropertyOrderTests {

    /**
     * A simple POJO with three fields. The @JsonPropertyOrder annotation is used to define
     * the desired order of fields during serialization.
     */
    @JsonPropertyOrder({ "id", "name", "email" })
    record User(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("id") int id
    ) {}

    static final String EXPECTED_JSON = """
        {
          "id" : 1,
          "name" : "Alice",
          "email" : "alice@example.com"
        }""";

    @Test
    @DisplayName("Serialize using @JsonPropertyOrder to control field ordering")
    void serializeWithExplicitPropertyOrder() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        User user = new User("Alice", "alice@example.com", 1);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        assertThat(json).isEqualTo(EXPECTED_JSON);
    }
}
