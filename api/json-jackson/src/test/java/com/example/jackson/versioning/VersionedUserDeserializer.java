package com.example.jackson.versioning;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class VersionedUserDeserializer extends ValueDeserializer<User> {

  @Override
  public User deserialize(JsonParser p, DeserializationContext ctxt) {
    // Jackson 3: the context reads the tree directly; parsers no longer expose a codec
    JsonNode node = ctxt.readTree(p);

    int version = node.path("version").asInt();
    int age = node.path("age").asInt();
    String name = null;
    String email = null;

    if (version == 1) {
      name = node.path("name").asString();
    } else if (version == 2) {
      name = node.path("fullName").asString();
      email = node.path("email").asString();
    }

    return new User(version, name, age, email);
  }
}
