package com.example.jackson.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

public class VersionedUserUnifiedTest {

  static final String V1_JSON =
      """
      {
        "version": 1,
        "name": "Alice",
        "age": 30
      }
      """;

  static final String V2_JSON =
      """
      {
        "version": 2,
        "fullName": "Alice Smith",
        "age": 30,
        "email": "alice@example.com"
      }
      """;

  static final User EXPECTED_V1 = new User(1, "Alice", 30, null);
  static final User EXPECTED_V2 = new User(2, "Alice Smith", 30, "alice@example.com");

  ObjectMapper mapper() {
    SimpleModule module = new SimpleModule();
    module.addDeserializer(User.class, new VersionedUserDeserializer());
    // Jackson 3 mappers are immutable: modules are registered on the builder
    return JsonMapper.builder().addModule(module).build();
  }

  @Test
  @DisplayName("Deserialize V1 JSON into User")
  void deserializeV1_shouldSucceed() {
    User user = mapper().readValue(V1_JSON, User.class);
    assertThat(user).isEqualTo(EXPECTED_V1);
  }

  @Test
  @DisplayName("Deserialize V2 JSON into User")
  void deserializeV2_shouldSucceed() {
    User user = mapper().readValue(V2_JSON, User.class);
    assertThat(user).isEqualTo(EXPECTED_V2);
  }
}
