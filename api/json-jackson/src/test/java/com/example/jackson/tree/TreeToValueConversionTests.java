package com.example.jackson.tree;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

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
  void treeToValue_shouldConvertJsonNodeToPojo() {
    ObjectMapper mapper = new JsonMapper();
    JsonNode node = mapper.readTree(PERSON_JSON);

    Person person = mapper.treeToValue(node, Person.class);
    assertThat(person).isEqualTo(PERSON_OBJECT);
  }

  @Test
  @DisplayName("Convert POJO to JsonNode using valueToTree")
  void valueToTree_shouldConvertPojoToJsonNode() {
    ObjectMapper mapper = new JsonMapper();
    JsonNode node = mapper.valueToTree(PERSON_OBJECT);

    // Jackson 3 renamed JsonNode.asText() to asString()
    assertThat(node.get("name").asString()).isEqualTo("Alice");
    assertThat(node.get("age").asInt()).isEqualTo(34);
  }
}
