package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConvertValueTest {

  static final SourcePerson SOURCE_OBJECT =
      new SourcePerson("Alice", 34, List.of("admin", "user"));

  static final TargetPerson TARGET_OBJECT =
      new TargetPerson("Alice", 34, List.of(new Role("admin"), new Role("user")));

  // Source class with basic fields
  record SourcePerson(
      @JsonProperty("name") String name,
      @JsonProperty("age") int age,
      @JsonProperty("roles") List<String> roles) {}

  // Target class with more structure
  record TargetPerson(
      @JsonProperty("name") String name,
      @JsonProperty("age") int age,
      @JsonProperty("roles") List<Role> roles) {}

  record Role(@JsonProperty("name") String name) {}

  @Test
  @DisplayName("Convert between POJOs, trees, and maps")
  void convertValue_shouldTransformCompatibleObjects() {
    ObjectMapper mapper = new ObjectMapper();

    // 1. Convert SourcePerson to ObjectNode
    ObjectNode tree = mapper.convertValue(SOURCE_OBJECT, ObjectNode.class);

    // 2. Enrich structure: convert list of strings to list of objects
    var structuredRoles = tree.putArray("roles");
    for (String roleName : SOURCE_OBJECT.roles()) {
      structuredRoles.addObject().put("name", roleName);
    }

    // 3. Convert to target structured type
    TargetPerson converted = mapper.convertValue(tree, TargetPerson.class);
    assertThat(converted).isEqualTo(TARGET_OBJECT);

    // 4. Convert from raw map to SourcePerson
    Map<String, Object> map = Map.of(
        "name", "Alice",
        "age", 34,
        "roles", List.of("admin", "user")
    );

    SourcePerson fromMap = mapper.convertValue(map, SourcePerson.class);
    assertThat(fromMap).isEqualTo(SOURCE_OBJECT);
  }
}
