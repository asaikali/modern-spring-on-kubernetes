package com.example.jackson.databinding.json.fields;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UnwrappedTests {

  static final String FLATTENED_JSON =
      """
      {
        "firstName": "Alice",
        "lastName": "Smith",
        "street": "123 Maple Street",
        "city": "Toronto",
        "postalCode": "M5V 2T6",
        "country": "Canada"
      }
      """;

  static final Person FLATTENED_OBJECT =
      new Person("Alice", "Smith", new Address("123 Maple Street", "Toronto", "M5V 2T6", "Canada"));

  record Address(String street, String city, String postalCode, String country) {}

  record Person(String firstName, String lastName, @JsonUnwrapped Address address) {}

  @Test
  @DisplayName("Deserialization of flattened JSON with @JsonUnwrapped")
  void deserializeFlattened_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Person person = mapper.readValue(FLATTENED_JSON, Person.class);
    assertThat(person).isEqualTo(FLATTENED_OBJECT);
  }

  @Test
  @DisplayName("Serialization of object to flattened JSON with @JsonUnwrapped")
  void serializeFlattened_shouldIncludeUnwrappedFields() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(FLATTENED_OBJECT);
    System.out.println("Serialized flattened:\n" + json);

    // Ensure top-level address fields are present
    assertThat(json).contains("\"street\"");
    assertThat(json).contains("\"city\"");
    assertThat(json).contains("\"postalCode\"");
    assertThat(json).contains("\"country\"");
    assertThat(json).doesNotContain("\"address\""); // Not nested
  }
}
