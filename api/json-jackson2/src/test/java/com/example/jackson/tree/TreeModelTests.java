package com.example.jackson.tree;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to use Jackson's Tree Model API (JsonNode) to - read unknown or dynamic JSON
 * into a navigable object tree - manipulate fields - serialize back to a JSON string
 */
public class TreeModelTests {

  // Multiline string for a flexible JSON structure with extra metadata
  static final String ORIGINAL_JSON =
      """
      {
        "user": {
          "id": 123,
          "name": "Alice"
        },
        "metadata": {
          "timestamp": "2024-07-25T12:34:56Z",
          "source": "mobile"
        }
      }
      """;

  @Test
  @DisplayName("Read JSON into tree model and access fields")
  void readTree_shouldParseDynamicJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(ORIGINAL_JSON);

    // Navigate into the tree
    JsonNode userNode = root.get("user");
    JsonNode metadataNode = root.get("metadata");

    assertThat(userNode.get("id").asInt()).isEqualTo(123);
    assertThat(userNode.get("name").asText()).isEqualTo("Alice");
    assertThat(metadataNode.get("source").asText()).isEqualTo("mobile");
  }

  @Test
  @DisplayName("Write JSON tree programmatically")
  void writeTree_shouldCreateJsonDynamically() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    // Build up an object tree dynamically
    ObjectNode userNode = mapper.createObjectNode();
    userNode.put("id", 456);
    userNode.put("name", "Bob");

    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.set("user", userNode);
    rootNode.put("status", "active");

    // Serialize to JSON string
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

    System.out.println("Generated JSON:\n" + json);

    assertThat(json).contains("\"id\" : 456");
    assertThat(json).contains("\"status\" : \"active\"");
  }
}
