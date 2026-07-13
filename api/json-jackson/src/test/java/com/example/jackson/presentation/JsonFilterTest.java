package com.example.jackson.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

public class JsonFilterTest {

  @JsonFilter("UserFilter")
  record User(String username, String email, String secretNote) {}

  static final User USER = new User("alice", "alice@example.com", "likes unicorns");

  @Test
  @DisplayName("Serialize User with dynamic filter to exclude secretNote")
  void serializeWithFilter_shouldOmitSecretNote() {
    ObjectMapper mapper = new JsonMapper();

    var filter = SimpleBeanPropertyFilter.serializeAllExcept("secretNote");
    var filters = new SimpleFilterProvider().addFilter("UserFilter", filter);

    String json = mapper.writer(filters).writeValueAsString(USER);

    System.out.println("Filtered JSON:\n" + json);

    assertThat(json).contains("username").contains("email").doesNotContain("secretNote");
  }

  @Test
  @DisplayName("Serialize User with different filter to include all fields")
  void serializeWithFilter_includeAllFields() {
    ObjectMapper mapper = new JsonMapper();

    var filter = SimpleBeanPropertyFilter.serializeAll();
    var filters = new SimpleFilterProvider().addFilter("UserFilter", filter);

    String json = mapper.writer(filters).writeValueAsString(USER);

    System.out.println("Unfiltered JSON:\n" + json);

    assertThat(json).contains("username").contains("email").contains("secretNote");
  }
}
