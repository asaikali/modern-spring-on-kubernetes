package com.example.jackson.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jackson.NoSpacePrettyPrinter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrettyPrintTests {

  static final Person PERSON_OBJECT =
      new Person("Alice", "Mathematics", List.of("student", "honor-roll", "club-member"));

  static final String expectedNoSpaceJson =
      """
    {
      "name":"Alice",
      "subject":"Mathematics",
      "tags":[ "student", "honor-roll", "club-member" ]
    }
    """;

  record Person(
      @JsonProperty("name") String name,
      @JsonProperty("subject") String subject,
      @JsonProperty("tags") List<String> tags) {}

  @Test
  @DisplayName("Pretty-print using default printer")
  void serializeWithDefaultPrettyPrinter_shouldIncludeSpacesAroundColons()
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PERSON_OBJECT);

    System.out.println("Default PrettyPrinter output:\n" + json);

    assertThat(json).contains("\"name\" : \"Alice\"");
  }

  @Test
  @DisplayName("Pretty-print using custom NoSpacePrettyPrinter")
  void serializeWithNoSpacePrettyPrinter_shouldMatchExpectedFormatting()
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    String json = mapper.writer(new NoSpacePrettyPrinter()).writeValueAsString(PERSON_OBJECT);

    System.out.println("NoSpacePrettyPrinter output:\n" + json);

    assertThat(json).contains("\"name\": \"Alice\"");
  }
}
