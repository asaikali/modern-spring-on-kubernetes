package com.example.jackson.versioning;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

public class VersionedUserDeserializer extends JsonDeserializer<User> {

  @Override
  public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    ObjectNode node = mapper.readTree(p);

    int version = node.path("version").asInt();
    int age = node.path("age").asInt();
    String name = null;
    String email = null;

    if (version == 1) {
      name = node.path("name").asText();
    } else if (version == 2) {
      name = node.path("fullName").asText();
      email = node.path("email").asText();
    }

    return new User(version, name, age, email);
  }
}
