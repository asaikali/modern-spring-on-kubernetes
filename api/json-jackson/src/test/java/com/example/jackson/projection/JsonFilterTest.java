package com.example.jackson.projection;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JsonFilterTest {

  @JsonFilter("UserFilter")
  record User(String username, String email, String secretNote) {}

  static final User USER = new User("alice", "alice@example.com", "likes unicorns");

  @Test
  @DisplayName("Serialize User with dynamic filter to exclude secretNote")
  void serializeWithFilter_shouldOmitSecretNote() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    var filter = SimpleBeanPropertyFilter.serializeAllExcept("secretNote");
    var filters = new SimpleFilterProvider().addFilter("UserFilter", filter);

    String json = mapper.writer(filters).writeValueAsString(USER);

    System.out.println("Filtered JSON:\n" + json);

    assertThat(json).contains("username").contains("email").doesNotContain("secretNote");
  }

  @Test
  @DisplayName("Serialize User with different filter to include all fields")
  void serializeWithFilter_includeAllFields() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    var filter = SimpleBeanPropertyFilter.serializeAll();
    var filters = new SimpleFilterProvider().addFilter("UserFilter", filter);

    String json = mapper.writer(filters).writeValueAsString(USER);

    System.out.println("Unfiltered JSON:\n" + json);

    assertThat(json).contains("username").contains("email").contains("secretNote");
  }
}
