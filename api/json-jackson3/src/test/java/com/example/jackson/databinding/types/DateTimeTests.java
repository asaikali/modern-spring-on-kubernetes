package com.example.jackson.databinding.types;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3 has java.time support built in (no JavaTimeModule to register) and, unlike Jackson 2,
 * serializes dates as ISO-8601 strings by default: WRITE_DATES_AS_TIMESTAMPS (now on
 * DateTimeFeature) is disabled out of the box.
 */
public class DateTimeTests {

  record Person(
      @JsonProperty("name") String name,
      @JsonProperty("birthday") LocalDate birthday,
      @JsonProperty("lastLogin") ZonedDateTime lastLogin,
      @JsonProperty("createdAt") Instant createdAt) {}

  record PersonWithFormat(
      @JsonProperty("name") String name,
      @JsonProperty("birthday") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
          LocalDate birthday,
      @JsonProperty("lastLogin")
          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
          ZonedDateTime lastLogin,
      @JsonProperty("createdAt") @JsonFormat(shape = JsonFormat.Shape.STRING) Instant createdAt) {}

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
  @DisplayName("Default serialization writes ISO-8601 strings")
  void serializeWithDefaults_shouldUseIsoStrings() {
    var input = new Person("Alice", DATE, ZDT, INSTANT);
    var mapper = new JsonMapper();

    String json = mapper.writeValueAsString(input);
    System.out.println("Default (ISO-8601 strings):\n" + json);

    assertThat(json).contains("\"birthday\":\"1990-05-01\"");
    assertThat(json).contains("\"lastLogin\":\"2024-07-26T09:00:00Z\"");
    assertThat(json).contains("\"createdAt\":\"2024-07-26T13:00:00Z\"");
  }

  @Test
  @DisplayName("Enabling WRITE_DATES_AS_TIMESTAMPS restores numeric timestamps")
  void serializeWithTimestampsEnabled_shouldUseTimestamps() {
    var input = new Person("Alice", DATE, ZDT, INSTANT);
    var mapper = JsonMapper.builder().enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();

    String json = mapper.writeValueAsString(input);
    System.out.println("Timestamps enabled:\n" + json);

    assertThat(json).contains("[1990,5,1]");
    assertThat(json).contains("\"createdAt\"");
  }

  @Test
  @DisplayName("Field-level @JsonFormat overrides global timestamp setting")
  void serializeWithFieldLevelJsonFormat_shouldUseCustomPatterns() {
    var input = PERSON_OBJECT;
    var mapper =
        JsonMapper.builder()
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS) // force timestamps globally
            .build();

    String json = mapper.writeValueAsString(input);
    System.out.println("Field-level @JsonFormat overrides global timestamps:\n" + json);

    assertThat(json).contains("\"birthday\":\"1990-05-01\"");
    assertThat(json).contains("\"lastLogin\":\"2024-07-26T09:00:00Z\"");
    assertThat(json).contains("\"createdAt\":\"2024-07-26T13:00:00Z\"");
  }

  @Test
  @DisplayName("Serialization matches ISO string with field-level @JsonFormat")
  void serializeWithFormatAnnotations_shouldMatchJsonLiteral() {
    JsonMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();

    String json = mapper.writeValueAsString(PERSON_OBJECT);
    System.out.println("Serialized with @JsonFormat:\n" + json);

    PersonWithFormat actual = mapper.readValue(json, PersonWithFormat.class);
    PersonWithFormat expected = mapper.readValue(PERSON_JSON, PersonWithFormat.class);
    assertThat(actual).isEqualTo(expected);
  }
}
