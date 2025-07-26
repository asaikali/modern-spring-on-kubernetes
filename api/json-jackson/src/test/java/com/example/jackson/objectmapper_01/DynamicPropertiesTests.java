package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DynamicPropertiesTests {

  // JSON string with both known fields ("id", "name") and unknown fields (e.g., "favoriteColor",
  // "hobby")
  static final String JSON_WITH_DYNAMIC_PROPS =
      """
      {
        "id": 1001,
        "name": "Alice",
        "favoriteColor": "blue",
        "hobby": "cycling",
        "twitter": "@alice"
      }
      """;

  // Java object that represents the same data
  static final User USER_OBJECT = new User(1001, "Alice");

  static {
    USER_OBJECT.addDynamic("favoriteColor", "blue");
    USER_OBJECT.addDynamic("hobby", "cycling");
    USER_OBJECT.addDynamic("twitter", "@alice");
  }

  /**
   * A user record that supports dynamic properties.
   *
   * <p>- Known fields: id, name - Unknown/dynamic fields: captured into a Map via @JsonAnySetter -
   * During serialization, entries from that Map are written into the root JSON via @JsonAnyGetter
   */
  static class User {
    private int id;
    private String name;

    // Map to hold dynamic key-value pairs not explicitly defined as fields
    private Map<String, Object> dynamicProperties = new HashMap<>();

    public User() {}

    public User(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    /**
     * @JsonAnySetter tells Jackson to call this method for any JSON property that doesn't map to a
     * known field.
     */
    @JsonAnySetter
    public void addDynamic(String key, Object value) {
      dynamicProperties.put(key, value);
    }

    /**
     * @JsonAnyGetter tells Jackson to serialize all entries from the map as if they were top-level
     * properties in the object.
     */
    @JsonAnyGetter
    public Map<String, Object> getDynamicProperties() {
      return dynamicProperties;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof User other)) return false;
      return id == other.id
          && name.equals(other.name)
          && dynamicProperties.equals(other.dynamicProperties);
    }

    @Override
    public int hashCode() {
      return id + name.hashCode() + dynamicProperties.hashCode();
    }
  }

  @Test
  @DisplayName("Deserialization with dynamic properties")
  void deserializeWithDynamicProperties_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    User user = mapper.readValue(JSON_WITH_DYNAMIC_PROPS, User.class);
    assertThat(user).isEqualTo(USER_OBJECT);
  }

  @Test
  @DisplayName("Serialization includes dynamic properties")
  void serializeWithDynamicProperties_shouldIncludeExtraFields() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(USER_OBJECT);
    System.out.println("Serialized with dynamic properties:\n" + json);

    assertThat(json).contains("\"favoriteColor\"");
    assertThat(json).contains("\"hobby\"");
    assertThat(json).contains("\"twitter\"");
    assertThat(json).contains("\"id\"");
    assertThat(json).contains("\"name\"");
  }
}
