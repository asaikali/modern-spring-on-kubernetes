package com.example.jackson.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jackson.NoSpacePrettyPrinter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

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
  void serializeWithDefaultPrettyPrinter_shouldIncludeSpacesAroundColons() {
    ObjectMapper mapper = new JsonMapper();

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PERSON_OBJECT);

    System.out.println("Default PrettyPrinter output:\n" + json);

    assertThat(json).contains("\"name\" : \"Alice\"");
  }

  @Test
  @DisplayName("Pretty-print using custom NoSpacePrettyPrinter")
  void serializeWithNoSpacePrettyPrinter_shouldMatchExpectedFormatting() {
    ObjectMapper mapper = new JsonMapper();

    // Jackson 3 removed ObjectMapper.writer(PrettyPrinter); attach it via ObjectWriter.with(...)
    String json =
        mapper.writer().with(new NoSpacePrettyPrinter()).writeValueAsString(PERSON_OBJECT);

    System.out.println("NoSpacePrettyPrinter output:\n" + json);

    assertThat(json).contains("\"name\": \"Alice\"");
  }
}
