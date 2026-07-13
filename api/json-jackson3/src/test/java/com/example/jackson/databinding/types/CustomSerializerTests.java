package com.example.jackson.databinding.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.json.JsonMapper;

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

  // Jackson 3 renames JsonSerializer to ValueSerializer; exceptions are unchecked so
  // serialize() no longer declares IOException
  static class LocalDateSerializer extends ValueSerializer<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext ctxt) {
      gen.writeString(value.format(FORMATTER));
    }
  }

  // Jackson 3 renames JsonDeserializer to ValueDeserializer
  static class LocalDateDeserializer extends ValueDeserializer<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) {
      if (p.currentToken() != JsonToken.VALUE_STRING) {
        // reportInputMismatch throws an unchecked DatabindException
        return ctxt.reportInputMismatch(LocalDate.class, "Expected string value for date");
      }
      return LocalDate.parse(p.getValueAsString(), FORMATTER);
    }
  }

  @Test
  @DisplayName("Deserialize using custom deserializer")
  void deserialize_withCustomDeserializer_shouldSucceed() {
    JsonMapper mapper = new JsonMapper();
    Employee employee = mapper.readValue(EMPLOYEE_JSON, Employee.class);
    assertThat(employee).isEqualTo(EMPLOYEE_OBJECT);
  }

  @Test
  @DisplayName("Serialize using custom serializer")
  void serialize_withCustomSerializer_shouldMatchExpectedJson() {
    JsonMapper mapper = new JsonMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(EMPLOYEE_OBJECT);
    System.out.println("Custom-serialized JSON:\n" + json);
    Employee roundTrip = mapper.readValue(json, Employee.class);
    assertThat(roundTrip).isEqualTo(EMPLOYEE_OBJECT);
  }
}
