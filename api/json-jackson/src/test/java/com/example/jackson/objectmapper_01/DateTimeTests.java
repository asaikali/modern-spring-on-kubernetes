package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateTimeTests {

  record Person(
      @JsonProperty("name") String name,
      @JsonProperty("birthday") LocalDate birthday,
      @JsonProperty("lastLogin") ZonedDateTime lastLogin) {}

  // show how @JsonFormat annotations work
  record PersonWithFormat(
      @JsonProperty("name") String name,
      @JsonProperty("birthday")
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
      LocalDate birthday,

      @JsonProperty("lastLogin")
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
      ZonedDateTime lastLogin) {}

  static final LocalDate DATE = LocalDate.of(1990, 5, 1);
  static final ZonedDateTime ZDT = ZonedDateTime.parse("2024-07-26T09:00:00Z");

  @Test
  @DisplayName("Default serialization writes timestamps")
  void serializeWithDefaults_shouldUseTimestamps() throws JsonProcessingException {
    var input = new Person("Alice", DATE, ZDT);
    var mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    String json = mapper.writeValueAsString(input);
    System.out.println("Default (timestamps):\n" + json);

    assertThat(json).contains("[1990,5,1]");
    assertThat(json).doesNotContain("1990-05-01");
  }

  @Test
  @DisplayName("Global ISO config disables timestamps")
  void serializeWithGlobalIsoConfig_shouldUseIsoStrings() throws JsonProcessingException {
    var input = new Person("Alice", DATE, ZDT);
    var mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String json = mapper.writeValueAsString(input);
    System.out.println("Global ISO format:\n" + json);

    assertThat(json).contains("\"birthday\":\"1990-05-01\"");
    assertThat(json).contains("\"lastLogin\":\"2024-07-26T09:00:00Z\"");
  }

  @Test
  @DisplayName("Field-level @JsonFormat overrides global timestamp setting")
  void serializeWithFieldLevelJsonFormat_shouldUseCustomPatterns() throws JsonProcessingException {
    var input = new PersonWithFormat("Alice", DATE, ZDT);
    var mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // force timestamps globally

    String json = mapper.writeValueAsString(input);
    System.out.println("Field-level @JsonFormat overrides global timestamps:\n" + json);

    assertThat(json).contains("\"birthday\":\"1990-05-01\"");
    assertThat(json).contains("\"lastLogin\":\"2024-07-26T09:00:00Z\"");
  }
}
