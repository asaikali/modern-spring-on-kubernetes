package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CustomSerializerTests {

  static final String EMPLOYEE_JSON =
      """
      {
        "name": "Alice",
        "joinedAt": "26-July-2025"
      }
      """;

  static final Employee EMPLOYEE_OBJECT = new Employee("Alice", LocalDate.of(2025, 7, 26));

  record Employee(
      String name,
      @JsonSerialize(using = LocalDateSerializer.class)
          @JsonDeserialize(using = LocalDateDeserializer.class)
          LocalDate joinedAt) {}

  static class LocalDateSerializer extends JsonSerializer<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString(value.format(FORMATTER));
    }
  }

  static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.currentToken() != JsonToken.VALUE_STRING) {
        throw new JsonMappingException(p, "Expected string value for date");
      }
      return LocalDate.parse(p.getValueAsString(), FORMATTER);
    }
  }

  @Test
  @DisplayName("Deserialize using custom deserializer")
  void deserialize_withCustomDeserializer_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Employee employee = mapper.readValue(EMPLOYEE_JSON, Employee.class);
    assertThat(employee).isEqualTo(EMPLOYEE_OBJECT);
  }

  @Test
  @DisplayName("Serialize using custom serializer")
  void serialize_withCustomSerializer_shouldMatchExpectedJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(EMPLOYEE_OBJECT);
    System.out.println("Custom-serialized JSON:\n" + json);
    Employee roundTrip = mapper.readValue(json, Employee.class);
    assertThat(roundTrip).isEqualTo(EMPLOYEE_OBJECT);
  }
}
