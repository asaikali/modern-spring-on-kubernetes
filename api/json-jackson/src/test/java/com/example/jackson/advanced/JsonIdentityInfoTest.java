package com.example.jackson.advanced;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to preserve object identity during serialization and deserialization
 * using @JsonIdentityInfo. In this example, multiple employees share the same manager object.
 *
 * <p>Without @JsonIdentityInfo, Jackson would serialize the manager repeatedly and lose the
 * reference relationship when deserializing.
 *
 * <p>With @JsonIdentityInfo, Jackson inserts an identity (ID) and uses it to represent references.
 */
public class JsonIdentityInfoTest {

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
  static class Person {
    public String name;

    public Person() {} // Required by Jackson

    public Person(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  static class Company {
    public String name;
    public List<Employee> employees;

    public Company() {}

    public Company(String name, List<Employee> employees) {
      this.name = name;
      this.employees = employees;
    }
  }

  static class Employee {
    public String name;
    public Person manager;

    public Employee() {}

    public Employee(String name, Person manager) {
      this.name = name;
      this.manager = manager;
    }
  }

  static final Person sharedManager = new Person("Alice");

  static final Company originalCompany =
      new Company(
          "Acme Corp",
          List.of(new Employee("Bob", sharedManager), new Employee("Charlie", sharedManager)));

  static final String expectedJson =
      """
      {
        "name" : "Acme Corp",
        "employees" : [ {
          "name" : "Bob",
          "manager" : {
            "name" : "Alice"
          }
        }, {
          "name" : "Charlie",
          "manager" : "Alice"
        } ]
      }
      """;

  @Test
  @DisplayName("Serialize and deserialize using @JsonIdentityInfo to preserve shared references")
  void serializeAndDeserializeWithIdentity() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    // Serialize
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(originalCompany);
    System.out.println(json);

    assertThat(json).contains("\"name\" : \"Alice\"");
    assertThat(json).contains("\"manager\" : \"Alice\""); // Back reference

    // Deserialize
    Company deserialized = mapper.readValue(json, Company.class);
    assertThat(deserialized.employees).hasSize(2);

    // Ensure the manager object is shared (same instance)
    assertThat(deserialized.employees.get(0).manager)
        .isSameAs(deserialized.employees.get(1).manager);
  }
}
