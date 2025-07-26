package com.example.jackson.advanced;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to use @JsonMerge with mutable POJOs to apply partial updates.
 *
 * <p>💡 NOTE: @JsonMerge does not work properly with Java records because they are immutable.
 * Jackson cannot update their fields in-place and will instead create a new instance, discarding
 * any merge behavior.
 *
 * <p>✅ Use @JsonMerge with mutable classes (non-records) and mutable fields (e.g., Map, List).
 */
public class JsonMergeTests {

  static class User {
    public String name;

    @JsonMerge // Merge map entries instead of replacing the whole map
    public Map<String, String> preferences;

    @JsonMerge // Merge list items instead of replacing the whole list
    public List<String> roles;

    public User() {}

    public User(String name, Map<String, String> preferences, List<String> roles) {
      this.name = name;
      this.preferences = preferences;
      this.roles = roles;
    }
  }

  static final String ORIGINAL_JSON =
      """
      {
        "name": "Alice",
        "preferences": {
          "theme": "light",
          "notifications": "enabled"
        },
        "roles": ["user", "viewer"]
      }
      """;

  static final String PATCH_JSON =
      """
      {
        "preferences": {
          "notifications": "disabled"
        },
        "roles": ["admin"]
      }
      """;

  @Test
  @DisplayName("Apply JSON Merge Patch using @JsonMerge and readerForUpdating")
  void mergePatch_shouldCombineFields() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    // Step 1: Deserialize the original object
    User original = mapper.readValue(ORIGINAL_JSON, User.class);

    // Step 2: Create ObjectReader for updating
    ObjectReader updater = mapper.readerForUpdating(original);

    // Step 3: Apply patch JSON to the existing object
    User updated = updater.readValue(PATCH_JSON);

    // 🔍 Assert merge behavior (map merged, list merged instead of replaced)
    assertThat(updated.name).isEqualTo("Alice");
    assertThat(updated.preferences)
        .containsEntry("theme", "light")
        .containsEntry("notifications", "disabled");
    assertThat(updated.roles).containsExactly("user", "viewer", "admin");
  }
}
