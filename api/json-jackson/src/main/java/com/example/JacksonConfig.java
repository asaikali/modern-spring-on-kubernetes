package com.example;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  /**
   * Customizes the default Jackson ObjectMapper used across the application.
   *
   * <p>This is the recommended way to tweak global Jackson settings in a Spring Boot app. Spring
   * Boot will automatically pick this bean and apply the changes during startup.
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> {
      // Enable pretty printing (useful for dev/debug output)
      builder.indentOutput(true);

      // Disable writing dates as timestamps (write as ISO-8601)
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      // Register a custom module to serialize Long values as strings (useful for JavaScript
      // consumers)
      SimpleModule longAsStringModule = new SimpleModule();
      longAsStringModule.addSerializer(Long.class, ToStringSerializer.instance);
      longAsStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
      builder.modules(longAsStringModule);

      // Optional: You can also set a naming strategy (e.g., snake_case)
      builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    };
  }
}
