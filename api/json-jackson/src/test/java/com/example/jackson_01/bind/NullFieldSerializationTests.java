package com.example.jackson_01.bind;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
  void serializeWithGlobalInclusionConfig_shouldOmitNulls() throws Exception {
    ObjectMapper mapper =
        new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    var partial = new Person("Alice", 34, true, null);
    String json = mapper.writeValueAsString(partial);

    System.out.println("Serialized with global NON_NULL:\n" + json);
    assertThat(json).contains("\"name\":\"Alice\"");
    assertThat(json).doesNotContain("emails");
  }

  @Test
  @DisplayName("Serialization excludes null fields via @JsonInclude on record")
  void serializeWithAnnotationBasedInclusion_shouldOmitNulls() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    var partial = new PersonWithIncludeNonNull("Alice", 34, true, null);
    String json = mapper.writeValueAsString(partial);

    System.out.println("Serialized with record-level @JsonInclude:\n" + json);
    assertThat(json).contains("\"name\":\"Alice\"");
    assertThat(json).doesNotContain("emails");
  }
}
