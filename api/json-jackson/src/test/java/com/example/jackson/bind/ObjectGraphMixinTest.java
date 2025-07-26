package com.example.jackson.bind;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ObjectGraphMixinTest {

  static final String PERSON_JSON =
      """
      {
       "name": "Alice",
       "age": 34,
       "active": true,
       "emails": ["alice@example.com", "a.smith@work.com"],
       "address": {
         "street": "123 Maple Street",
         "city": "Toronto",
         "postalCode": "M5V 2T6",
         "country": "Canada"
       },
       "roles": [
         { "name": "admin", "level": 10 },
         { "name": "user", "level": 1 }
       ],
       "status": "A"
     }
      """;

  static final Person PERSON_OBJECT =
      new Person(
          "Alice",
          34,
          true,
          List.of("alice@example.com", "a.smith@work.com"),
          new Address("123 Maple Street", "Toronto", "M5V 2T6", "Canada"),
          List.of(new Role("admin", 10), new Role("user", 1)),
          Status.ACTIVE);

  record Role(String name, int level) {}

  record Address(String street, String city, String postalCode, String country) {}

  record Person(
      String name,
      int age,
      boolean active,
      List<String> emails,
      Address address,
      List<Role> roles,
      Status status) {}

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

    public static Status fromCode(String value) {
      for (Status status : values()) {
        if (status.code.equalsIgnoreCase(value)) {
          return status;
        }
      }
      throw new IllegalArgumentException("Unknown status: " + value);
    }
  }

  // Mixins
  public abstract static class PersonMixin {
    @JsonCreator
    public PersonMixin(
        @JsonProperty("name") String name,
        @JsonProperty("age") int age,
        @JsonProperty("active") boolean active,
        @JsonProperty("emails") List<String> emails,
        @JsonProperty("address") Address address,
        @JsonProperty("roles") List<Role> roles,
        @JsonProperty("status") Status status) {}
  }

  public abstract static class RoleMixin {
    @JsonCreator
    public RoleMixin(@JsonProperty("name") String name, @JsonProperty("level") int level) {}
  }

  public abstract static class AddressMixin {
    @JsonCreator
    public AddressMixin(
        @JsonProperty("street") String street,
        @JsonProperty("city") String city,
        @JsonProperty("postalCode") String postalCode,
        @JsonProperty("country") String country) {}
  }

  public abstract static class StatusMixin {
    @JsonValue
    public abstract String getCode();

    @JsonCreator
    public static Status fromCode(String value) {
      return null; // dummy body, ignored
    }
  }

  private static ObjectMapper objectMapperWithMixins() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.addMixIn(Person.class, PersonMixin.class);
    mapper.addMixIn(Role.class, RoleMixin.class);
    mapper.addMixIn(Address.class, AddressMixin.class);
    mapper.addMixIn(Status.class, StatusMixin.class);
    return mapper;
  }

  @Test
  @DisplayName("Deserialization of object graph with mixins")
  void deserialize_withMixins_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = objectMapperWithMixins();
    Person person = mapper.readValue(PERSON_JSON, Person.class);
    assertThat(person).isEqualTo(PERSON_OBJECT);
  }

  @Test
  @DisplayName("Serialization of object graph with mixins")
  void serialize_withMixins_shouldMatchExpectedJson() throws JsonProcessingException {
    ObjectMapper mapper = objectMapperWithMixins();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PERSON_OBJECT);
    System.out.println("Serialized with mixins:\n" + json);
    Person person = mapper.readValue(json, Person.class);
    assertThat(person).isEqualTo(PERSON_OBJECT);
  }
}
