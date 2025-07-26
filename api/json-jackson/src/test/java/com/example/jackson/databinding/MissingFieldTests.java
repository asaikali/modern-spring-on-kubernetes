package com.example.jackson.databinding;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MissingFieldTests {

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Person(
      @JsonProperty("name") String name,
      @JsonProperty("age") int age,
      @JsonProperty("active") boolean active,
      @JsonProperty("emails") List<String> emails) {}

  static final String MISSING_FIELD_JSON =
      """
      {
        "name": "Alice",
        "age": 34,
        "active": true
      }
      """;

  static final String EXTRA_FIELD_JSON =
      """
      {
        "name": "Alice",
        "age": 34,
        "active": true,
        "emails": ["alice@example.com"],
        "nickname": "Ally",
        "unknownFlag": true
      }
      """;

  @Test
  @DisplayName("Deserialization handles missing fields gracefully")
  void deserializeWithMissing_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    var missing = mapper.readValue(MISSING_FIELD_JSON, Person.class);
    assertThat(missing.name()).isEqualTo("Alice");
    assertThat(missing.age()).isEqualTo(34);
    assertThat(missing.active()).isTrue();
    assertThat(missing.emails()).isNull();
  }

  @Test
  @DisplayName("Deserialization handles missing fields gracefully")
  void deserializeWithExtraFields_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    var extra = mapper.readValue(EXTRA_FIELD_JSON, Person.class);
    assertThat(extra.emails()).containsExactly("alice@example.com");
  }
}
