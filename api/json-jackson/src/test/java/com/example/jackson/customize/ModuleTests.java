package com.example.jackson.customize;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

  static class StatusSerializer extends JsonSerializer<Status> {
    @Override
    public void serialize(Status value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString("[STATUS=" + value.getCode() + "]");
    }
  }

  static class StatusDeserializer extends JsonDeserializer<Status> {
    @Override
    public Status deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
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
  void deserializeWithModule_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule("StatusModule", Version.unknownVersion());
    module.addDeserializer(Status.class, new StatusDeserializer());
    mapper.registerModule(module);

    Person person = mapper.readValue(PERSON_JSON, Person.class);
    assertThat(person).isEqualTo(PERSON_OBJECT);
  }

  @Test
  @DisplayName("Serialization using registered module")
  void serializeWithModule_shouldMatchExpected() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule("StatusModule", Version.unknownVersion());
    module.addSerializer(Status.class, new StatusSerializer());
    mapper.registerModule(module);

    String json = mapper.writeValueAsString(PERSON_OBJECT);
    System.out.println("Serialized JSON:\n" + json);
    assertThat(json).contains("[STATUS=A]");
  }
}
