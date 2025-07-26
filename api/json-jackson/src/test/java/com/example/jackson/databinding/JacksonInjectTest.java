package com.example.jackson.databinding;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates the use of {@link JacksonInject} to inject values into fields that are NOT present
 * in the incoming JSON. This is useful when some values should come from the application context,
 * configuration, or service layer rather than the payload.
 */
public class JacksonInjectTest {

  static final String inputJson = """
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
  void deserializeWithInjectedValue() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    // Provide injectable values — simulate context like Spring bean or config
    mapper.setInjectableValues(
        new InjectableValues.Std().addValue(long.class, 42L)); // Inject `userId = 42L`

    UserWithInjectedId user = mapper.readValue(inputJson, UserWithInjectedId.class);

    assertThat(user.getUsername()).isEqualTo("alice");
    assertThat(user.getUserId()).isEqualTo(42L); // Confirm injected value
  }
}
