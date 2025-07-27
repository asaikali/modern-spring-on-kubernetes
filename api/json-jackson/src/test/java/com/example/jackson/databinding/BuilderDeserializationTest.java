package com.example.jackson.databinding;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to deserialize a JSON object into a Java object that uses the Builder pattern.
 * Jackson does not support builder-based deserialization out-of-the-box, so we must explicitly:
 *
 * <p>- Annotate the main class with @JsonDeserialize(builder = ...) - Annotate the builder class
 * with @JsonPOJOBuilder
 *
 * <p>This allows Jackson to instantiate and populate the builder, then call the build() method to
 * get the target object.
 */
public class BuilderDeserializationTest {

  /** JSON input with fields that match the builder method names. */
  static final String inputJson =
      """
      {
        "name": "Ada Lovelace",
        "age": 36
      }
      """;

  /** Immutable Person class with a builder. */
  @JsonDeserialize(builder = Person.Builder.class)
  static class Person {
    private final String name;
    private final int age;

    private Person(Builder builder) {
      this.name = builder.name;
      this.age = builder.age;
    }

    public String name() {
      return name;
    }

    public int age() {
      return age;
    }

    /** Builder class. The `withPrefix = "with"` matches methods like `withName()`. */
    @JsonPOJOBuilder(withPrefix = "with")
    public static class Builder {
      private String name;
      private int age;

      public Builder withName(String name) {
        this.name = name;
        return this;
      }

      public Builder withAge(int age) {
        this.age = age;
        return this;
      }

      public Person build() {
        return new Person(this);
      }
    }
  }

  @Test
  @DisplayName("Deserialize JSON using Builder pattern with @JsonPOJOBuilder")
  void deserializeWithBuilder() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    Person result = mapper.readValue(inputJson, Person.class);

    assertThat(result.name()).isEqualTo("Ada Lovelace");
    assertThat(result.age()).isEqualTo(36);
  }
}
