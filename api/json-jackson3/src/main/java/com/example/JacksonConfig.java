package com.example;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class JacksonConfig {

  /**
   * Customizes the default Jackson JsonMapper used across the application.
   *
   * <p>This is the recommended way to tweak global Jackson settings in a Spring Boot app. Jackson 3
   * mappers are immutable, so all configuration happens on the builder; Spring Boot will
   * automatically pick this bean and apply the changes during startup.
   */
  @Bean
  public JsonMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> {
      // Enable pretty printing (useful for dev/debug output)
      builder.enable(SerializationFeature.INDENT_OUTPUT);

      // Write dates as ISO-8601 strings, not numeric timestamps. This is already the default in
      // Jackson 3 (it was flipped from Jackson 2, which also moved the setting from
      // SerializationFeature to DateTimeFeature); disabling it here just makes the intent
      // explicit.
      builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);

      // Register a custom module to serialize Long values as strings (useful for JavaScript
      // consumers)
      SimpleModule longAsStringModule = new SimpleModule();
      longAsStringModule.addSerializer(Long.class, ToStringSerializer.instance);
      longAsStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
      builder.addModule(longAsStringModule);

      // Optional: You can also set a naming strategy (e.g., snake_case)
      builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    };
  }
}
