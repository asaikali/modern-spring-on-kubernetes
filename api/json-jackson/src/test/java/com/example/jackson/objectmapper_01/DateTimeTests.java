package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateTimeTests {

  record Person(
      @JsonProperty("name") String name,
      @JsonProperty("birthday") LocalDate birthday,
      @JsonProperty("lastLogin") ZonedDateTime lastLogin,
      @JsonProperty("createdAt") Instant createdAt) {}

  record PersonWithFormat(
      @JsonProperty("name") String name,

      @JsonProperty("birthday")
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
      LocalDate birthday,

      @JsonProperty("lastLogin")
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
      ZonedDateTime lastLogin,

      @JsonProperty("createdAt")
      @JsonFormat(shape = JsonFormat.Shape.STRING)
      Instant createdAt) {}

  static final LocalDate DATE = LocalDate.of(1990, 5, 1);
  static final ZonedDateTime ZDT = ZonedDateTime.parse("2024-07-26T09:00:00Z");
  static final Instant INSTANT = Instant.parse("2024-07-26T13:00:00Z");

  static final PersonWithFormat PERSON_OBJECT = new PersonWithFormat("Alice", DATE, ZDT, INSTANT);

  static final String PERSON_JSON =
      """
      {
        "name": "Alice",
        "birthday": "1990-05-01",
        "lastLogin": "2024-07-26T09:00:00Z",
        "createdAt": "2024-07-26T13:00:00.000Z"
      }
      """;

  @Test
  @DisplayName("Default serialization writes timestamps")
  void serializeWithDefaults_shouldUseTimestamps() throws JsonProcessingException {
    var input = new Person("Alice", DATE, ZDT, INSTANT);
    var mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    String json = mapper.writeValueAsString(input);
    System.out.println("Default (timestamps):\n" + json);

    assertThat(json).contains("[1990,5,1]");
    assertThat(json).contains("\"createdAt\"");
  }

  @Test
  @DisplayName("Global ISO config disables timestamps")
  void serializeWithGlobalIsoConfig_shouldUseIsoStrings() throws JsonProcessingException {
    var input = new Person("Alice", DATE, ZDT, INSTANT);
    var mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String json = mapper.writeValueAsString(input);
    System.out.println("Global ISO format:\n" + json);

    assertThat(json).contains("\"birthday\":\"1990-05-01\"");
    assertThat(json).contains("\"lastLogin\":\"2024-07-26T09:00:00Z\"");
    assertThat(json).contains("\"createdAt\":\"2024-07-26T13:00:00Z\"");
  }

  @Test
  @DisplayName("Field-level @JsonFormat overrides global timestamp setting")
  void serializeWithFieldLevelJsonFormat_shouldUseCustomPatterns() throws JsonProcessingException {
    var input = PERSON_OBJECT;
    var mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // force timestamps globally

    String json = mapper.writeValueAsString(input);
    System.out.println("Field-level @JsonFormat overrides global timestamps:\n" + json);

    assertThat(json).contains("\"birthday\":\"1990-05-01\"");
    assertThat(json).contains("\"lastLogin\":\"2024-07-26T09:00:00Z\"");
    assertThat(json).contains("\"createdAt\":\"2024-07-26T13:00:00Z\"");
  }

  @Test
  @DisplayName("Serialization matches ISO string with field-level @JsonFormat")
  void serializeWithFormatAnnotations_shouldMatchJsonLiteral() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String json = mapper.writeValueAsString(PERSON_OBJECT);
    System.out.println("Serialized with @JsonFormat:\n" + json);

    PersonWithFormat actual = mapper.readValue(json, PersonWithFormat.class);
    PersonWithFormat expected = mapper.readValue(PERSON_JSON, PersonWithFormat.class);
    assertThat(actual).isEqualTo(expected);
  }
}
