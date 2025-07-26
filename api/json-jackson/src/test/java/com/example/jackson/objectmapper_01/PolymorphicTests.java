package com.example.jackson.objectmapper_01;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Rectangle;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PolymorphicTests {

  static final String SHAPE_JSON =
      """
      {
        "type": "circle",
        "color": "red",
        "radius": 5.0
      }
      """;

  static final String WRAPPER_JSON =
      """
      {
        "name": "Drawing 1",
        "shapes": [
          {
            "type": "circle",
            "color": "red",
            "radius": 5.0
          },
          {
            "type": "rectangle",
            "color": "blue",
            "width": 4.0,
            "height": 3.0
          }
        ]
      }
      """;

  static final Drawing WRAPPER_OBJECT =
      new Drawing("Drawing 1", List.of(new Circle("red", 5.0), new Rectangle("blue", 4.0, 3.0)));

  /**
   * notice that in on of the types records below is the "type" field from the json represented as a
   * field value, rather it is implied by the Java type itself.
   */
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = Circle.class, name = "circle"),
    @JsonSubTypes.Type(value = Rectangle.class, name = "rectangle")
  })
  sealed interface Shape permits Circle, Rectangle {
    @JsonProperty("color")
    String color();
  }

  record Circle(@JsonProperty("color") String color, @JsonProperty("radius") double radius)
      implements Shape {}

  record Rectangle(
      @JsonProperty("color") String color,
      @JsonProperty("width") double width,
      @JsonProperty("height") double height)
      implements Shape {}

  record Drawing(@JsonProperty("name") String name, @JsonProperty("shapes") List<Shape> shapes) {}

  @Test
  @DisplayName("Deserialize single polymorphic object")
  void deserializeSingleShape_shouldSucceed() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Shape shape = mapper.readValue(SHAPE_JSON, Shape.class);
    assertThat(shape).isInstanceOf(Circle.class);
    assertThat(((Circle) shape).radius()).isEqualTo(5.0);
  }

  @Test
  @DisplayName("Deserialize wrapper with list of polymorphic objects")
  void deserializeWrapper_shouldMatchObjectGraph() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Drawing drawing = mapper.readValue(WRAPPER_JSON, Drawing.class);
    assertThat(drawing).isEqualTo(WRAPPER_OBJECT);
  }

  @Test
  @DisplayName("Serialize wrapper with polymorphic types")
  void serializeWrapper_shouldMatchExpectedFormat() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(WRAPPER_OBJECT);
    System.out.println("Polymorphic object graph:\n" + json);
    Drawing parsed = mapper.readValue(json, Drawing.class);
    assertThat(parsed).isEqualTo(WRAPPER_OBJECT);
  }
}
