package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TreeToValueConversionTests {

  static final String PERSON_JSON =
      """
      {
        "name": "Alice",
        "age": 34
      }
      """;

  static final Person PERSON_OBJECT = new Person("Alice", 34);

  record Person(@JsonProperty("name") String name, @JsonProperty("age") int age) {}

  @Test
  @DisplayName("Convert JsonNode to POJO using treeToValue")
  void treeToValue_shouldConvertJsonNodeToPojo() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(PERSON_JSON);

    Person person = mapper.treeToValue(node, Person.class);
    assertThat(person).isEqualTo(PERSON_OBJECT);
  }

  @Test
  @DisplayName("Convert POJO to JsonNode using valueToTree")
  void valueToTree_shouldConvertPojoToJsonNode() {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.valueToTree(PERSON_OBJECT);

    assertThat(node.get("name").asText()).isEqualTo("Alice");
    assertThat(node.get("age").asInt()).isEqualTo(34);
  }
}
