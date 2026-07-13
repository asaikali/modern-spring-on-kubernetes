package com.example.json;

import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Service
public class JacksonJsonService implements JsonService {

  // Jackson 3 mappers are immutable; configuration happens on a builder. Java 8 types,
  // java.time, and constructor parameter names are supported out of the box (the old
  // Jdk8Module, JavaTimeModule, and ParameterNamesModule are built into jackson-databind),
  // and dates serialize as ISO-8601 strings by default (WRITE_DATES_AS_TIMESTAMPS is off).
  private static final ObjectMapper mapper =
      JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();

  @Override
  public String format(String json) {
    return toJson(fromJson(json, Object.class));
  }

  @Override
  public <T> T fromJson(String json, Class<T> type) {
    try {
      return mapper.readValue(json, type);
    } catch (JacksonException e) {
      throw new JsonServiceException(
          String.format(
              "Unable to parse json value into java object of type '%s' using jackson ObjectMapper",
              type.getName()),
          e);
    }
  }

  public String toJson(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JacksonException e) {
      throw new JsonServiceException(
          String.format(
              "Unable to convert Java object of type '%s' to json using jackson ObjectMapper",
              object.getClass().getName()),
          e);
    }
  }
}
