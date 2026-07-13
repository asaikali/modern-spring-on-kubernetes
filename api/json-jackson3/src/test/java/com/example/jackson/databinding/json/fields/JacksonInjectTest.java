package com.example.jackson.databinding.json.fields;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.json.JsonMapper;

/**
 * Demonstrates the use of {@link JacksonInject} to inject values into fields that are NOT present
 * in the incoming JSON. This is useful when some values should come from the application context,
 * configuration, or service layer rather than the payload.
 */
public class JacksonInjectTest {

  static final String inputJson =
      """
      {
        "username": "alice"
      }
      """;

  static class UserWithInjectedId {

    private final String username;

    // This field is not in the JSON but will be injected by Jackson
    @JacksonInject private final long userId;

    @JsonCreator
    public UserWithInjectedId(
        @JsonProperty("username") String username, @JacksonInject long userId) {
      this.username = username;
      this.userId = userId;
    }

    public String getUsername() {
      return username;
    }

    public long getUserId() {
      return userId;
    }
  }

  @Test
  @DisplayName("Inject value into deserialized object using @JacksonInject")
  void deserializeWithInjectedValue() {
    // Jackson 3 mappers are immutable, so injectable values are configured on the builder —
    // simulate context like a Spring bean or config
    JsonMapper mapper =
        JsonMapper.builder()
            .injectableValues(
                new InjectableValues.Std().addValue(long.class, 42L)) // Inject `userId = 42L`
            .build();

    UserWithInjectedId user = mapper.readValue(inputJson, UserWithInjectedId.class);

    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getUserId()).isEqualTo(42L); // Confirm injected value
  }
}
