/*
 * Copyright 2023 Programming Mastery Inc.
 *
 * All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * Proprietary and confidential
 */

package com.example.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class JacksonJsonService implements JsonService {
  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();

    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());

    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Override
  public String format(String json) {
    return toJson(fromJson(json, Object.class));
  }

  @Override
  public <T> T fromJson(String json, Class<T> type) {
    try {
      return mapper.readValue(json, type);
    } catch (IOException e) {
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
    } catch (JsonProcessingException e) {
      throw new JsonServiceException(
          String.format(
              "Unable to convert Java object of type '%s' to json using jackson ObjectMapper",
              object.getClass().getName()),
          e);
    }
  }
}
