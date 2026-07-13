package com.example.jackson.databinding.json.fields;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to use @JsonAlias to support multiple names for a field during deserialization.
 */
public class JsonAliasTest {

  // Sample input JSON using alias field names
  static final String ALIAS_JSON =
      """
      {
        "id": "p100",
        "product_name": "Widget",
        "tags": ["gadget", "tool"]
      }
      """;

  /** A record with @JsonAlias annotations to handle multiple expected JSON property names. */
  record Product(
      String id,

      // Accept either "product_name" or "productName" from JSON when deserializing into this field
      @JsonAlias({"product_name", "productName"}) String name,

      // Accept "tags" as an alternative field name for this field
      @JsonAlias("tags") List<String> labels) {}

  @Test
  @DisplayName("Deserialize using alias field names")
  void deserializeWithAlias_shouldBindFieldsCorrectly() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Product product = mapper.readValue(ALIAS_JSON, Product.class);

    assertThat(product.id()).isEqualTo("p100");
    assertThat(product.name()).isEqualTo("Widget");
    assertThat(product.labels()).containsExactly("gadget", "tool");
  }

  @Test
  @DisplayName("Serialize will use the actual field names, not aliases")
  void serialize_shouldUseCanonicalFieldNames() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Product product = new Product("p100", "Widget", List.of("gadget", "tool"));

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);

    assertThat(json).contains("\"name\""); // not "product_name" or "productName"
    assertThat(json).contains("\"labels\"");
    System.out.println(json);
  }
}
