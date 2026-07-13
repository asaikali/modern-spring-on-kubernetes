package com.example.jackson.filter;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

@RestController
public class FilteredUserController {

  private final ObjectMapper objectMapper;

  private final User user = new User("charlie", "charlie@example.com", "internal-only note");

  public FilteredUserController(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @GetMapping(value = "/filter/public", produces = MediaType.APPLICATION_JSON_VALUE)
  public String publicView() {
    return filter(user, SimpleBeanPropertyFilter.filterOutAllExcept("username"));
  }

  @GetMapping(value = "/filter/internal", produces = MediaType.APPLICATION_JSON_VALUE)
  public String internalView() {
    return filter(
        user, SimpleBeanPropertyFilter.filterOutAllExcept("username", "email", "secretNote"));
  }

  /**
   * Serializes the value with a per-request filter using a filtered ObjectWriter. The Jackson 3
   * message converter in Spring Framework 7 no longer supports MappingJacksonValue, so the
   * controller applies the filter itself.
   */
  private String filter(Object value, SimpleBeanPropertyFilter filter) {
    FilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", filter);
    return objectMapper.writer(filters).writeValueAsString(value);
  }
}
