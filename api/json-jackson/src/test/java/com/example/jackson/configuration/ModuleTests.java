package com.example.jackson.configuration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

public class ModuleTests {

  static final String PERSON_JSON =
      """
      {
        "name": "Alice",
        "status": "[STATUS=A]"
      }
      """;

  static final Person PERSON_OBJECT = new Person("Alice", Status.ACTIVE);

  record Person(String name, Status status) {}

  enum Status {
    ACTIVE("A"),
    INACTIVE("I"),
    PENDING("P");

    private final String code;

    Status(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    public static Status fromCode(String code) {
      for (Status status : values()) {
        if (status.code.equalsIgnoreCase(code)) {
          return status;
        }
      }
      throw new IllegalArgumentException("Unknown status: " + code);
    }
  }

  static class StatusSerializer extends ValueSerializer<Status> {
    @Override
    public void serialize(Status value, JsonGenerator gen, SerializationContext ctxt) {
      gen.writeString("[STATUS=" + value.getCode() + "]");
    }
  }

  static class StatusDeserializer extends ValueDeserializer<Status> {
    @Override
    public Status deserialize(JsonParser p, DeserializationContext ctxt) {
      String text = p.getValueAsString();
      if (text.startsWith("[STATUS=") && text.endsWith("]")) {
        String code = text.substring(8, text.length() - 1);
        return Status.fromCode(code);
      }
      throw new IllegalArgumentException("Invalid status format: " + text);
    }
  }

  @Test
  @DisplayName("Deserialization using registered module")
  void deserializeWithModule_shouldSucceed() {
    SimpleModule module = new SimpleModule("StatusModule", Version.unknownVersion());
    module.addDeserializer(Status.class, new StatusDeserializer());
    ObjectMapper mapper = JsonMapper.builder().addModule(module).build();

    Person person = mapper.readValue(PERSON_JSON, Person.class);
    assertThat(person).isEqualTo(PERSON_OBJECT);
  }

  @Test
  @DisplayName("Serialization using registered module")
  void serializeWithModule_shouldMatchExpected() {
    SimpleModule module = new SimpleModule("StatusModule", Version.unknownVersion());
    module.addSerializer(Status.class, new StatusSerializer());
    ObjectMapper mapper = JsonMapper.builder().addModule(module).build();

    String json = mapper.writeValueAsString(PERSON_OBJECT);
    System.out.println("Serialized JSON:\n" + json);
    assertThat(json).contains("[STATUS=A]");
  }
}
