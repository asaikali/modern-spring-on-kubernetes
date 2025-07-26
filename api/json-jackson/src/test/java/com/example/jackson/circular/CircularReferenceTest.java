package com.example.jackson.circular;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates how to handle circular references using Jackson's {@code @JsonManagedReference} and
 * {@code @JsonBackReference} annotations.
 *
 * <p>These annotations are used to serialize parent-child relationships without causing infinite
 * recursion. The managed side (parent) is serialized normally, and the back-reference (child →
 * parent) is skipped during serialization but restored during deserialization.
 *
 * <p><b>Important:</b> This pattern only works with <em>mutable classes</em>. Jackson needs to use
 * setters or direct field access to populate the back-reference after it instantiates the parent.
 */
public class CircularReferenceTest {

  /** The parent class must be mutable to support circular reference resolution. */
  static class Parent {
    public String name;

    // This is the forward side of the relationship (parent → children)
    @JsonManagedReference public List<Child> children;

    public Parent() {}

    public Parent(String name, List<Child> children) {
      this.name = name;
      this.children = children;
    }

    public String name() {
      return name;
    }

    public List<Child> children() {
      return children;
    }

    @Override
    public String toString() {
      return "Parent{name='%s', children=%s}".formatted(name, children);
    }
  }

  /**
   * The child class contains the back-reference to the parent. This must be a mutable field for
   * Jackson to populate it during deserialization.
   */
  static class Child {
    public String name;

    // This back-reference is skipped during serialization,
    // but automatically populated during deserialization.
    @JsonBackReference public Parent parent;

    public Child() {}

    public Child(String name, Parent parent) {
      this.name = name;
      this.parent = parent;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  // Construct the parent and set children manually (including back-references)
  static final Parent original =
      new Parent("parent", List.of(new Child("a", null), new Child("b", null)));

  static {
    // Set back-reference manually for serialization
    original.children.forEach(child -> child.parent = original);
  }

  static final String expectedJson =
      """
      {
        "name": "parent",
        "children": [
          {"name": "a"},
          {"name": "b"}
        ]
      }
      """;

  @Test
  @DisplayName("Serialize and deserialize with circular references using mutable classes")
  void serializeAndDeserialize() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    // ✅ Serialize: child.parent is skipped because of @JsonBackReference
    String serialized = mapper.writeValueAsString(original);
    System.out.println("Serialized:\n" + serialized);

    assertThat(serialized).contains("\"name\":\"parent\"");
    assertThat(serialized).contains("\"children\"");
    assertThat(serialized).doesNotContain("\"parent\":");

    // ✅ Deserialize: parent is injected into each child automatically
    Parent deserialized = mapper.readValue(expectedJson, Parent.class);
    System.out.println("Deserialized:\n" + deserialized);

    assertThat(deserialized.name()).isEqualTo("parent");
    assertThat(deserialized.children()).hasSize(2);
    assertThat(deserialized.children().get(0).name).isEqualTo("a");

    // 🔁 Back-reference restored by Jackson
    assertThat(deserialized.children().get(0).parent).isEqualTo(deserialized);
    assertThat(deserialized.children().get(1).parent).isEqualTo(deserialized);
  }
}
