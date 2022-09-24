package com.example;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MetricsApplication {

  /**
   * automatically add any common tags to the all meters created. full detail at
   * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-metrics
   */
  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
      @Value("region") String region, @Value("zone") String zone) {
    return registry ->
        registry.config().commonTags(List.of(Tag.of("region", region), Tag.of("zone", zone)));
  }

  public static void main(String[] args) {
    SpringApplication.run(MetricsApplication.class, args);
  }
}
