package com.example.jackson.bind;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GenericTypesTest {

  static final String GENERIC_JSON =
      """
      {
        "data": [
          { "name": "Alice", "age": 34 },
          { "name": "Bob", "age": 29 }
        ]
      }
      """;

  static final GenericWrapper<Person> GENERIC_OBJECT =
      new GenericWrapper<>(List.of(new Person("Alice", 34), new Person("Bob", 29)));

  record Person(@JsonProperty("name") String name, @JsonProperty("age") int age) {}

  record GenericWrapper<T>(@JsonProperty("data") List<T> data) {}

  @Test
  @DisplayName("Deserialization of generic type with TypeReference")
  void deserializeGeneric_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    // TypeReference is used here to retain the generic type information (GenericWrapper<Person>)
    // at runtime. This is necessary because Java's type erasure removes generic type info,
    // and Jackson needs the concrete type to deserialize properly.
    var value = mapper.readValue(GENERIC_JSON, new TypeReference<GenericWrapper<Person>>() {});

    assertThat(value).isEqualTo(GENERIC_OBJECT);
  }

  @Test
  @DisplayName("Serialization of generic type with TypeReference")
  void serializeGeneric_shouldMatchExpectedJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(GENERIC_OBJECT);
    System.out.println("Serialized generic object:\n" + json);

    // Again, use TypeReference to deserialize the JSON back into a strongly-typed generic object.
    var roundTrip = mapper.readValue(json, new TypeReference<GenericWrapper<Person>>() {});

    assertThat(roundTrip).isEqualTo(GENERIC_OBJECT);
  }
}
