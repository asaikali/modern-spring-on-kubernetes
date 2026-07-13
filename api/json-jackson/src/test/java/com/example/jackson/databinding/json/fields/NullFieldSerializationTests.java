package com.example.jackson.databinding.json.fields;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class NullFieldSerializationTests {

  record Person(
      @JsonProperty("name") String name,
      @JsonProperty("age") int age,
      @JsonProperty("active") boolean active,
      @JsonProperty("emails") List<String> emails) {}

  @JsonInclude(JsonInclude.Include.NON_NULL)
  record PersonWithIncludeNonNull(
      @JsonProperty("name") String name,
      @JsonProperty("age") int age,
      @JsonProperty("active") boolean active,
      @JsonProperty("emails") List<String> emails) {}

  @Test
  @DisplayName("Serialization excludes null fields globally using mapper config")
  void serializeWithGlobalInclusionConfig_shouldOmitNulls() {
    // Jackson 3 mappers are immutable, so the default property inclusion is set on the builder
    JsonMapper mapper =
        JsonMapper.builder()
            .changeDefaultPropertyInclusion(
                inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    var partial = new Person("Alice", 34, true, null);
    String json = mapper.writeValueAsString(partial);

    System.out.println("Serialized with global NON_NULL:\n" + json);
    assertThat(json).contains("\"name\":\"Alice\"");
    assertThat(json).doesNotContain("emails");
  }

  @Test
  @DisplayName("Serialization excludes null fields via @JsonInclude on record")
  void serializeWithAnnotationBasedInclusion_shouldOmitNulls() {
    JsonMapper mapper = new JsonMapper();

    var partial = new PersonWithIncludeNonNull("Alice", 34, true, null);
    String json = mapper.writeValueAsString(partial);

    System.out.println("Serialized with record-level @JsonInclude:\n" + json);
    assertThat(json).contains("\"name\":\"Alice\"");
    assertThat(json).doesNotContain("emails");
  }
}
