package com.example.jackson.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FilteredUserController {

  @Autowired private ObjectMapper objectMapper;

  private final User user = new User("charlie", "charlie@example.com", "internal-only note");

  @GetMapping("/filter/public")
  public MappingJacksonValue publicView() {
    return filter(user, SimpleBeanPropertyFilter.filterOutAllExcept("username"));
  }

  @GetMapping("/filter/internal")
  public MappingJacksonValue internalView() {
    return filter(
        user, SimpleBeanPropertyFilter.filterOutAllExcept("username", "email", "secretNote"));
  }

  private MappingJacksonValue filter(Object value, SimpleBeanPropertyFilter filter) {
    FilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", filter);
    MappingJacksonValue wrapper = new MappingJacksonValue(value);
    wrapper.setFilters(filters);
    return wrapper;
  }
}
